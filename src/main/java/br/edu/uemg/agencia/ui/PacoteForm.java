package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.modelo.PacoteNacional;
import br.edu.uemg.agencia.repos.PacoteRepo;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PacoteForm extends JFrame {

    private final PacoteRepo repo = new PacoteRepo();
    private final Color THEME_COLOR = new Color(139, 92, 246);

    private JTextField tfId;
    private JComboBox<String> cbTipo;
    private JTextField tfDestino;
    private JTextField tfDuracao;
    private JTextField tfValorBase;

    private JTextField tfMoeda;
    private JTextField tfTaxaCambio;
    private JTextField tfTaxaEmbarque;

    private DefaultTableModel tableModel;
    private JTable table;

    public PacoteForm() {
        setTitle("Gestão de Pacotes de Viagem");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadTableData();
        toggleTipoFields();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Pacotes");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +10");

        JLabel subtitleLabel = new JLabel("Cadastre destinos nacionais e internacionais");
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 20, 15));
        formPanel.setBackground(Color.WHITE);

        tfId = createStyledInput("Automático", false);

        cbTipo = new JComboBox<>(new String[]{"nacional", "internacional"});
        styleComboBox(cbTipo);

        tfDestino = createStyledInput("Ex: Paris, França", true);
        tfDuracao = createStyledInput("Dias", true);
        tfValorBase = createStyledInput("0.00", true);

        tfMoeda = createStyledInput("Ex: USD", true);
        tfTaxaCambio = createStyledInput("0.00", true);
        tfTaxaEmbarque = createStyledInput("250.0", true);

        formPanel.add(createFieldContainer("Código", tfId));
        formPanel.add(createFieldContainer("Tipo de Viagem", cbTipo));
        formPanel.add(createFieldContainer("Destino", tfDestino));
        formPanel.add(createFieldContainer("Duração (dias)", tfDuracao));

        formPanel.add(createFieldContainer("Valor Base (R$)", tfValorBase));
        formPanel.add(createFieldContainer("Moeda (Int.)", tfMoeda));
        formPanel.add(createFieldContainer("Câmbio (Int.)", tfTaxaCambio));
        formPanel.add(createFieldContainer("Tx. Embarque", tfTaxaEmbarque));

        contentPanel.add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnNovo = createButton("Novo", Color.WHITE, Color.BLACK, false);
        JButton btnSalvar = createButton("Salvar Pacote", THEME_COLOR, Color.WHITE, true);
        JButton btnExcluir = createButton("Excluir", new Color(239, 68, 68), Color.WHITE, true);
        JButton btnLimpar = createButton("Limpar", Color.WHITE, Color.BLACK, false);

        cbTipo.addActionListener(e -> toggleTipoFields());
        btnNovo.addActionListener(e -> onNovo());
        btnLimpar.addActionListener(e -> clearForm());
        btnSalvar.addActionListener(e -> onSalvar());
        btnExcluir.addActionListener(e -> onExcluir());

        JButton btnRefresh = createButton("Recarregar", Color.WHITE, Color.GRAY, false);
        btnRefresh.addActionListener(e -> loadTableData());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnExcluir);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 15));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(buttonPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tipo", "Destino", "Duração", "Valor Base", "Moeda", "Câmbio", "Tx. Emb."}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        centerContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(centerContainer, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTableSelection();
        });
    }


    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(139, 92, 246, 40)); // Roxo bem claro
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setBackground(Color.WHITE);
            label.setForeground(new Color(100, 100, 100));
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, THEME_COLOR));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setBorder(BorderFactory.createCompoundBorder(
                    label.getBorder(),
                    BorderFactory.createEmptyBorder(0, 5, 0, 0)));
            return label;
        });
    }

    private void styleComboBox(JComboBox box) {
        box.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        box.setBackground(Color.WHITE);
    }

    private JTextField createStyledInput(String placeholder, boolean editable) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        tf.setEditable(editable);
        if(!editable) tf.setBackground(new Color(245, 245, 245));
        return tf;
    }

    private JPanel createFieldContainer(String labelText, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold small");
        lbl.setForeground(new Color(80, 80, 80));
        p.add(lbl, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean bold) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        String style = "arc: 10; margin: 8,20,8,20;";
        if(bold) style += "font: bold";
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        return btn;
    }


    private void toggleTipoFields() {
        String tipo = (String) cbTipo.getSelectedItem();
        boolean inter = "internacional".equalsIgnoreCase(tipo);

        tfMoeda.setEnabled(inter);
        tfTaxaCambio.setEnabled(inter);
        tfTaxaEmbarque.setEnabled(inter);

        Color disabledColor = new Color(245, 245, 245);
        Color enabledColor = Color.WHITE;

        tfMoeda.setBackground(inter ? enabledColor : disabledColor);
        tfTaxaCambio.setBackground(inter ? enabledColor : disabledColor);
        tfTaxaEmbarque.setBackground(inter ? enabledColor : disabledColor);
    }

    private void onNovo() {
        clearForm();
        tfDestino.requestFocus();
    }

    private void onSalvar() {
        String tipo = (String) cbTipo.getSelectedItem();
        String destino = tfDestino.getText().trim();
        String duracaoS = tfDuracao.getText().trim();
        String valorBaseS = tfValorBase.getText().trim();

        if (destino.isEmpty() || duracaoS.isEmpty() || valorBaseS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Destino, duração e valor base são obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int duracao = Integer.parseInt(duracaoS);
            double valorBase = Double.parseDouble(valorBaseS);

            if (tfId.getText().isEmpty()) {
                if ("nacional".equalsIgnoreCase(tipo)) {
                    PacoteNacional pn = new PacoteNacional(null, destino, duracao, valorBase);
                    repo.insert(pn);
                } else {
                    String moeda = tfMoeda.getText().trim();
                    double taxaCambio = Double.parseDouble(tfTaxaCambio.getText().trim());
                    double taxaEmbarque = Double.parseDouble(tfTaxaEmbarque.getText().trim());
                    PacoteInternacional pi = new PacoteInternacional(null, destino, duracao, valorBase, moeda, taxaCambio);
                    pi.setTaxaEmbarque(taxaEmbarque);
                    repo.insert(pi);
                }
                JOptionPane.showMessageDialog(this, "Pacote salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int id = Integer.parseInt(tfId.getText());
                if ("nacional".equalsIgnoreCase(tipo)) {
                    PacoteNacional pn = new PacoteNacional(id, destino, duracao, valorBase);
                    repo.update(pn);
                } else {
                    String moeda = tfMoeda.getText().trim();
                    double taxaCambio = Double.parseDouble(tfTaxaCambio.getText().trim());
                    double taxaEmbarque = Double.parseDouble(tfTaxaEmbarque.getText().trim());
                    PacoteInternacional pi = new PacoteInternacional(id, destino, duracao, valorBase, moeda, taxaCambio);
                    pi.setTaxaEmbarque(taxaEmbarque);
                    repo.update(pi);
                }
                JOptionPane.showMessageDialog(this, "Pacote atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            loadTableData();
            clearForm();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Verifique se os campos numéricos estão corretos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExcluir() {
        if (tfId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este pacote?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(tfId.getText());
            repo.delete(id);
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        tfId.setText("");
        tfDestino.setText("");
        tfDuracao.setText("");
        tfValorBase.setText("");
        tfMoeda.setText("");
        tfTaxaCambio.setText("");
        tfTaxaEmbarque.setText("250.0");
        cbTipo.setSelectedIndex(0);
        table.clearSelection();
        toggleTipoFields();
    }

    private void onTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tfId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
            String tipoNaTabela = (String) tableModel.getValueAt(row, 1);
            cbTipo.setSelectedItem(tipoNaTabela);

            tfDestino.setText((String) tableModel.getValueAt(row, 2));
            tfDuracao.setText(String.valueOf(tableModel.getValueAt(row, 3)));
            tfValorBase.setText(String.valueOf(tableModel.getValueAt(row, 4)));

            Object moeda = tableModel.getValueAt(row, 5);
            tfMoeda.setText(moeda != null ? moeda.toString() : "");

            Object taxaCambio = tableModel.getValueAt(row, 6);
            tfTaxaCambio.setText(taxaCambio != null ? taxaCambio.toString() : "");

            Object taxaEmbarque = tableModel.getValueAt(row, 7);
            tfTaxaEmbarque.setText(taxaEmbarque != null ? taxaEmbarque.toString() : "250.0");

            toggleTipoFields();
        }
    }

    private void loadTableData() {
        try {
            List<Pacote> pacotes = repo.findAll();
            tableModel.setRowCount(0);
            for (Pacote p : pacotes) {
                if (p instanceof PacoteNacional) {
                    tableModel.addRow(new Object[]{
                            p.getId(),
                            "nacional",
                            p.getDestino(),
                            p.getDuracao(),
                            p.getValorBase(),
                            null,
                            null,
                            null
                    });
                } else if (p instanceof PacoteInternacional) {
                    PacoteInternacional pi = (PacoteInternacional) p;
                    tableModel.addRow(new Object[]{
                            p.getId(),
                            "internacional",
                            p.getDestino(),
                            p.getDuracao(),
                            p.getValorBase(),
                            pi.getMoeda(),
                            pi.getTaxaCambio(),
                            pi.getTaxaEmbarque()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}