package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.modelo.*;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.util.ExternalService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PacoteForm extends JFrame {
    private final PacoteRepo repo = new PacoteRepo();
    private JTextField tfId, tfDestino, tfDuracao, tfValor, tfMoeda, tfCambio, tfEmb;
    private JComboBox<String> cbTipo;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnExcluir;

    public PacoteForm() {
        ModernUI.setupTheme(this);
        setTitle("Pacotes");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = ModernUI.createCard();
        form.setLayout(new BorderLayout(0,15));

        JPanel grid = new JPanel(new GridLayout(2, 4, 15, 15));
        grid.setOpaque(false);

        tfId = ModernUI.createInput("Auto"); tfId.setEditable(false);
        cbTipo = new JComboBox<>(new String[]{"Nacional", "Internacional"});
        tfDestino = ModernUI.createInput("Destino");
        tfDuracao = ModernUI.createInput("Dias");
        tfValor = ModernUI.createInput("Valor");
        tfMoeda = ModernUI.createInput("Moeda");

        JPanel pCambio = new JPanel(new BorderLayout(5,0)); pCambio.setOpaque(false);
        tfCambio = ModernUI.createInput("Câmbio");
        pCambio.add(tfCambio, BorderLayout.CENTER);
        JButton btnUp = ModernUI.createOutlineButton("↻"); btnUp.setPreferredSize(new Dimension(40,30));
        btnUp.addActionListener(e -> fetch());
        pCambio.add(btnUp, BorderLayout.EAST);

        tfEmb = ModernUI.createInput("Taxa Emb.");

        grid.add(ModernUI.createLabelGroup("ID", tfId));
        grid.add(ModernUI.createLabelGroup("Tipo", cbTipo));
        grid.add(ModernUI.createLabelGroup("Destino", tfDestino));
        grid.add(ModernUI.createLabelGroup("Duração", tfDuracao));
        grid.add(ModernUI.createLabelGroup("Valor Base", tfValor));
        grid.add(ModernUI.createLabelGroup("Moeda", tfMoeda));
        grid.add(ModernUI.createLabelGroup("Câmbio", pCambio));
        grid.add(ModernUI.createLabelGroup("Embarque", tfEmb));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton btnMap = ModernUI.createOutlineButton("Mapa");
        JButton btnSave = ModernUI.createButton("Salvar");
        btnExcluir = ModernUI.createButton("Excluir"); btnExcluir.setBackground(ModernUI.DANGER);
        JButton btnClr = ModernUI.createOutlineButton("Novo");

        btnMap.addActionListener(e -> ExternalService.abrirNoMapa(tfDestino.getText()));
        btnSave.addActionListener(e -> save());
        btnExcluir.addActionListener(e -> delete());
        btnClr.addActionListener(e -> clear());
        cbTipo.addActionListener(e -> toggle());

        btns.add(btnMap); btns.add(btnClr); btns.add(btnExcluir); btns.add(btnSave);
        form.add(grid, BorderLayout.CENTER);
        form.add(btns, BorderLayout.SOUTH);

        JPanel tableP = ModernUI.createCard();
        tableP.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tipo", "Destino", "Valor"},0);
        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        tableP.add(new JScrollPane(table));
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) pop(); });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, tableP);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(300);
        main.add(split, BorderLayout.CENTER);
        setContentPane(main);
        toggle();
        applyPermissions();
    }

    private void toggle() {
        boolean inter = cbTipo.getSelectedItem().equals("Internacional");
        tfMoeda.setEnabled(inter); tfCambio.setEnabled(inter); tfEmb.setEnabled(inter);
    }

    private void fetch() {
        new Thread(() -> {
            Double val = ExternalService.getCotacao(tfMoeda.getText());
            if(val != null) SwingUtilities.invokeLater(() -> tfCambio.setText(String.valueOf(val)));
        }).start();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        repo.findAll().forEach(p -> tableModel.addRow(new Object[]{p.getId(), (p instanceof PacoteNacional ? "Nac" : "Intl"), p.getDestino(), p.getValorBase()}));
    }

    private void save() {
        try {
            Pacote p;
            String dest = tfDestino.getText();
            int dur = Integer.parseInt(tfDuracao.getText());
            double val = Double.parseDouble(tfValor.getText().replace(",","."));
            if(cbTipo.getSelectedItem().equals("Nacional")) {
                p = new PacoteNacional(null, dest, dur, val);
            } else {
                p = new PacoteInternacional(null, dest, dur, val, tfMoeda.getText(), Double.parseDouble(tfCambio.getText()));
                ((PacoteInternacional)p).setTaxaEmbarque(Double.parseDouble(tfEmb.getText()));
            }
            if(!tfId.getText().equals("Auto")) {
                p.setId(Integer.parseInt(tfId.getText()));
                repo.update(p);
            } else repo.insert(p);
            loadData(); clear(); JOptionPane.showMessageDialog(this, "Salvo!");
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void pop() {
        int r = table.getSelectedRow();
        if(r >= 0) {
            repo.findById((int)tableModel.getValueAt(r,0)).ifPresent(p -> {
                tfId.setText(String.valueOf(p.getId()));
                tfDestino.setText(p.getDestino());
                tfDuracao.setText(String.valueOf(p.getDuracao()));
                tfValor.setText(String.valueOf(p.getValorBase()));
                if(p instanceof PacoteInternacional pi) {
                    cbTipo.setSelectedItem("Internacional");
                    tfMoeda.setText(pi.getMoeda());
                    tfCambio.setText(String.valueOf(pi.getTaxaCambio()));
                    tfEmb.setText(String.valueOf(pi.getTaxaEmbarque()));
                } else cbTipo.setSelectedItem("Nacional");
                toggle();
            });
        }
    }

    private void delete() {
        if(PermissionUtil.requireAdmin(this, "Excluir", "Pacotes") && !tfId.getText().equals("Auto")) {
            repo.delete(Integer.parseInt(tfId.getText()));
            loadData(); clear();
        }
    }

    private void clear() {
        tfId.setText("Auto"); tfDestino.setText("Destino"); tfValor.setText("0.0");
        table.clearSelection();
    }

    private void applyPermissions() {
        if(btnExcluir!=null) btnExcluir.setEnabled(Sessao.getPerfil()!=null && Sessao.getPerfil().equals("admin"));
    }
}