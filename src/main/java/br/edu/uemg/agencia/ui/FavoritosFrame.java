package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.repos.FavoritoRepo;
import br.edu.uemg.agencia.util.ExternalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FavoritosFrame extends JFrame {

    private final Cliente cliente;
    private final FavoritoRepo repo = new FavoritoRepo();
    private DefaultTableModel tableModel;
    private JTable table;

    public FavoritosFrame(Cliente cliente) {
        this.cliente = cliente;
        ModernUI.setupTheme(this);
        setTitle("Favoritos de " + cliente.getNome());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Lista de Desejos ⭐");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);

        JLabel sub = new JLabel("Cliente: " + cliente.getNome());
        sub.setFont(ModernUI.FONT_PLAIN);
        sub.setForeground(ModernUI.COL_TEXT_BODY);

        JPanel titleBox = new JPanel(new GridLayout(2,1)); titleBox.setOpaque(false);
        titleBox.add(title); titleBox.add(sub);

        header.add(titleBox, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        JPanel tableCard = ModernUI.createCard();
        tableCard.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Destino", "Tipo", "Duração", "Preço"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton btnMap = ModernUI.createOutlineButton("Ver no Mapa");
        JButton btnRemove = ModernUI.createButton("Remover Favorito");
        btnRemove.setBackground(ModernUI.DANGER);

        btnMap.addActionListener(e -> openMap());
        btnRemove.addActionListener(e -> removeFav());

        footer.add(btnMap);
        footer.add(btnRemove);

        main.add(tableCard, BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Pacote> favoritos = repo.listarPorCliente(cliente.getId());

        if (favoritos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este cliente ainda não tem favoritos.");
        }

        for (Pacote p : favoritos) {
            String tipo = p instanceof PacoteInternacional ? "Internacional" : "Nacional";
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getDestino(),
                    tipo,
                    p.getDuracao() + " dias",
                    String.format("R$ %.2f", p.calcularValorFinal())
            });
        }
    }

    private void removeFav() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para remover.");
            return;
        }

        int pacoteId = (int) tableModel.getValueAt(row, 0);
        repo.remover(cliente.getId(), pacoteId);
        loadData();
        JOptionPane.showMessageDialog(this, "Removido dos favoritos!");
    }

    private void openMap() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String destino = (String) tableModel.getValueAt(row, 1);
            ExternalService.abrirNoMapa(destino);
        }
    }
}