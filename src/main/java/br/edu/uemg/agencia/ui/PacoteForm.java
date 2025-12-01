package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.modelo.*;
import br.edu.uemg.agencia.repos.FavoritoRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.util.ExternalService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PacoteForm extends JFrame {
    private final PacoteRepo repo = new PacoteRepo();
    private JTextField tfId, tfDestino, tfDuracao, tfValor, tfMoeda, tfCambio, tfEmb;
    private JComboBox<String> cbTipo;
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnExcluir, btnSave, btnClr;

    public PacoteForm() {
        ModernUI.setupTheme(this);
        setTitle("Pacotes");
        setSize(1000, 750);
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
        JButton btnCompare = ModernUI.createOutlineButton("⚖ Comparar");
        JButton btnFav = ModernUI.createOutlineButton("❤ Favoritar");

        btnSave = ModernUI.createButton("Salvar");
        btnExcluir = ModernUI.createButton("Excluir"); btnExcluir.setBackground(ModernUI.DANGER);
        btnClr = ModernUI.createOutlineButton("Novo");

        btnMap.addActionListener(e -> ExternalService.abrirNoMapa(tfDestino.getText()));
        btnCompare.addActionListener(e -> compare());
        btnFav.addActionListener(e -> favorite());
        btnSave.addActionListener(e -> save());
        btnExcluir.addActionListener(e -> delete());
        btnClr.addActionListener(e -> clear());
        cbTipo.addActionListener(e -> toggle());

        btns.add(btnFav); btns.add(btnCompare); btns.add(btnMap);
        btns.add(Box.createHorizontalStrut(20));
        btns.add(btnClr); btns.add(btnExcluir); btns.add(btnSave);

        form.add(grid, BorderLayout.CENTER);
        form.add(btns, BorderLayout.SOUTH);

        JPanel listCard = ModernUI.createCard();
        listCard.setLayout(new BorderLayout(0, 10));

        JPanel filterPanel = new JPanel(new BorderLayout(10,0));
        filterPanel.setOpaque(false);
        JTextField txtFilter = ModernUI.createInput("🔎 Filtrar por Destino, Tipo ou Valor...");
        filterPanel.add(txtFilter, BorderLayout.CENTER);

        listCard.add(filterPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tipo", "Destino", "Valor"},0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        ModernUI.styleTable(table);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        txtFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtFilter.getText();
                if (text.trim().length() == 0 || text.equals("🔎 Filtrar por Destino, Tipo ou Valor...")) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        listCard.add(new JScrollPane(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) pop(); });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, listCard);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(320);
        main.add(split, BorderLayout.CENTER);
        setContentPane(main);
        toggle();
        applyPermissions();
    }

    private void applyPermissions() {
        boolean isAdmin = PermissionUtil.isAdmin();

        btnSave.setEnabled(isAdmin);
        btnExcluir.setEnabled(isAdmin);
        btnClr.setEnabled(isAdmin);

        if (!isAdmin) {
            tfDestino.setEditable(false);
            tfDuracao.setEditable(false);
            tfValor.setEditable(false);
            tfMoeda.setEditable(false);
            tfEmb.setEditable(false);
            cbTipo.setEnabled(false);

            btnSave.setText("🔒 Leitura");
            btnSave.setBackground(Color.GRAY);
        }
    }

    private void favorite() {
        if(tfId.getText().equals("Auto")) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote.");
            return;
        }
        String idStr = JOptionPane.showInputDialog(this, "Digite o ID do Cliente para favoritar:");
        if(idStr != null && !idStr.isEmpty()) {
            try {
                int clienteId = Integer.parseInt(idStr);
                int pacoteId = Integer.parseInt(tfId.getText());
                new FavoritoRepo().adicionar(clienteId, pacoteId);
                JOptionPane.showMessageDialog(this, "Pacote favoritado com sucesso!");
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        }
    }

    private void compare() {
        int[] rows = table.getSelectedRows();
        if(rows.length < 2 || rows.length > 3) {
            JOptionPane.showMessageDialog(this, "Selecione entre 2 e 3 pacotes na tabela para comparar (Segure CTRL).");
            return;
        }
        List<Pacote> list = new ArrayList<>();
        for(int r : rows) {
            int modelRow = table.convertRowIndexToModel(r);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            repo.findById(id).ifPresent(list::add);
        }
        new ComparadorFrame(list).setVisible(true);
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
        if (!PermissionUtil.isAdmin()) return;
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
            int modelRow = table.convertRowIndexToModel(r);
            repo.findById((int)tableModel.getValueAt(modelRow,0)).ifPresent(p -> {
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
        if(!tfId.getText().equals("Auto")) {
            repo.delete(Integer.parseInt(tfId.getText()));
            loadData(); clear();
        }
    }

    private void clear() {
        tfId.setText("Auto"); tfDestino.setText("Destino"); tfValor.setText("0.0");
        table.clearSelection();
    }
}