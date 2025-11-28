package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.modelo.PacoteNacional;
import br.edu.uemg.agencia.repos.PacoteRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PacoteForm extends JFrame {

    private final PacoteRepo repo = new PacoteRepo();
    private JTextField tfId, tfDestino, tfDuracao, tfValorBase;
    private JTextField tfMoeda, tfTaxaCambio, tfTaxaEmbarque;
    private JComboBox<String> cbTipo;
    private DefaultTableModel tableModel;
    private JTable table;

    private JButton btnExcluir;
    private JButton btnSave;

    public PacoteForm() {
        ModernUI.applyTheme(this);
        setTitle("Gestão de Pacotes");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadTableData();
        toggleTipoFields();
        applyPermissions();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ModernUI.COL_BG);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(ModernUI.COL_SURFACE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        JLabel title = new JLabel("Pacotes de Viagem");
        title.setFont(ModernUI.FONT_H1);
        title.setBorder(new EmptyBorder(20, 30, 20, 30));
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel formCard = ModernUI.createCard();
        formCard.setLayout(new BorderLayout(0, 20));

        JPanel fields = new JPanel(new GridLayout(2, 4, 20, 20));
        fields.setOpaque(false);

        tfId = ModernUI.createInput("Auto");
        tfId.setEditable(false);
        tfId.setBackground(new Color(245, 245, 245));

        cbTipo = new JComboBox<>(new String[]{"Nacional", "Internacional"});
        styleCombo(cbTipo);

        tfDestino = ModernUI.createInput("Ex: Paris");
        tfDuracao = ModernUI.createInput("Dias");
        tfValorBase = ModernUI.createInput("R$ 0.00");
        tfMoeda = ModernUI.createInput("Moeda");
        tfTaxaCambio = ModernUI.createInput("Câmbio");
        tfTaxaEmbarque = ModernUI.createInput("Tx Emb");

        fields.add(ModernUI.createFieldGroup("ID", tfId));
        fields.add(ModernUI.createFieldGroup("Tipo", cbTipo));
        fields.add(ModernUI.createFieldGroup("Destino", tfDestino));
        fields.add(ModernUI.createFieldGroup("Duração", tfDuracao));
        fields.add(ModernUI.createFieldGroup("Valor Base", tfValorBase));
        fields.add(ModernUI.createFieldGroup("Moeda", tfMoeda));
        fields.add(ModernUI.createFieldGroup("Câmbio", tfTaxaCambio));
        fields.add(ModernUI.createFieldGroup("Tx Embarque", tfTaxaEmbarque));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        btnSave = ModernUI.createButton("Salvar", true);
        JButton btnNew = ModernUI.createButton("Novo / Limpar", false);
        btnExcluir = ModernUI.createButton("Excluir", false);
        btnExcluir.setForeground(ModernUI.COL_ACCENT_DANGER);

        cbTipo.addActionListener(e -> toggleTipoFields());
        btnSave.addActionListener(e -> onSalvar());
        btnNew.addActionListener(e -> clearForm());
        btnExcluir.addActionListener(e -> onExcluir());

        actions.add(btnNew);
        actions.add(btnExcluir);
        actions.add(btnSave);

        formCard.add(fields, BorderLayout.CENTER);
        formCard.add(actions, BorderLayout.SOUTH);

        JPanel tableCard = ModernUI.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tipo", "Destino", "Valor", "Moeda", "Cambio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        ModernUI.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTableSelection();
        });

        content.add(formCard, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);
        main.add(content, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void styleCombo(JComboBox box) {
        box.setFont(ModernUI.FONT_BODY);
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));
    }

    private void toggleTipoFields() {
        boolean inter = "Internacional".equalsIgnoreCase((String) cbTipo.getSelectedItem());
        setSt(tfMoeda, inter);
        setSt(tfTaxaCambio, inter);
        setSt(tfTaxaEmbarque, inter);
    }

    private void setSt(JTextField tf, boolean en) {
        tf.setEnabled(en);
        tf.setBackground(en ? Color.WHITE : new Color(243, 244, 246));
    }

    private void onSalvar() {
        try {
            String dest = tfDestino.getText();
            if(dest.isEmpty()) throw new IllegalArgumentException("Destino é obrigatório");

            int dur = Integer.parseInt(tfDuracao.getText());
            double val = Double.parseDouble(tfValorBase.getText().replace(",", "."));

            Pacote p;
            if (cbTipo.getSelectedItem().equals("Nacional")) {
                p = new PacoteNacional(null, dest, dur, val);
            } else {
                p = new PacoteInternacional(null, dest, dur, val, tfMoeda.getText(), Double.parseDouble(tfTaxaCambio.getText()));
                ((PacoteInternacional) p).setTaxaEmbarque(Double.parseDouble(tfTaxaEmbarque.getText()));
            }

            if (!tfId.getText().isEmpty() && !tfId.getText().equals("Auto")) {
                p.setId(Integer.parseInt(tfId.getText()));
                repo.update(p);
                JOptionPane.showMessageDialog(this, "Pacote atualizado!");
            } else {
                repo.insert(p);
                JOptionPane.showMessageDialog(this, "Pacote criado!");
            }

            loadTableData();
            clearForm();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Verifique os campos numéricos (Duração, Valor, Taxas).");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void onTableSelection() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        try {
            Object idObj = table.getValueAt(r, 0);
            int id = Integer.parseInt(idObj.toString());

            repo.findById(id).ifPresent(p -> {
                tfId.setText(String.valueOf(p.getId()));
                tfDestino.setText(p.getDestino());
                tfDuracao.setText(String.valueOf(p.getDuracao()));
                tfValorBase.setText(String.valueOf(p.getValorBase()));

                if (p instanceof PacoteInternacional pi) {
                    cbTipo.setSelectedItem("Internacional");
                    tfMoeda.setText(pi.getMoeda());
                    tfTaxaCambio.setText(String.valueOf(pi.getTaxaCambio()));
                    tfTaxaEmbarque.setText(String.valueOf(pi.getTaxaEmbarque()));
                } else {
                    cbTipo.setSelectedItem("Nacional");
                    clearInternacional();
                }

                toggleTipoFields();
                btnSave.setText("Atualizar");
            });
        } catch (Exception e) {
            System.err.println("Erro ao selecionar: " + e.getMessage());
        }
    }

    private void onExcluir() {
        if (!PermissionUtil.requireAdmin(this, "Excluir", "PacoteForm")) return;

        if (!tfId.getText().isEmpty() && !tfId.getText().equals("Auto")) {
            if(JOptionPane.showConfirmDialog(this, "Excluir pacote?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                repo.delete(Integer.parseInt(tfId.getText()));
                loadTableData();
                clearForm();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para excluir.");
        }
    }

    private void clearForm() {
        tfId.setText("Auto");
        tfDestino.setText("");
        tfDuracao.setText("");
        tfValorBase.setText("");
        clearInternacional();
        table.clearSelection();
        btnSave.setText("Salvar");
    }

    private void clearInternacional() {
        tfMoeda.setText("");
        tfTaxaCambio.setText("");
        tfTaxaEmbarque.setText("250.0");
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        repo.findAll().forEach(p -> {
            if (p instanceof PacoteInternacional pi)
                tableModel.addRow(new Object[]{p.getId(), "Intl", p.getDestino(), p.getValorBase(), pi.getMoeda(), pi.getTaxaCambio()});
            else tableModel.addRow(new Object[]{p.getId(), "Nac", p.getDestino(), p.getValorBase(), "-", "-"});
        });
    }

    private void applyPermissions() {
        if (btnExcluir != null) btnExcluir.setEnabled(Sessao.getPerfil() != null && Sessao.getPerfil().equals("admin"));
    }
}