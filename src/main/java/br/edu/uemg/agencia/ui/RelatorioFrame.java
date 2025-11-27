package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.repos.RelatorioRepo;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class RelatorioFrame extends JFrame {

    private final RelatorioRepo repo = new RelatorioRepo();

    private final Color THEME_COLOR = new Color(245, 158, 11);

    private JLabel valArrecadado;
    private JLabel valTotalReservas;
    private JLabel valPendentes;
    private JLabel valConfirmadas;
    private JLabel valCanceladas;

    private DefaultTableModel tableModel;
    private JTable table;

    public RelatorioFrame() {
        setTitle("Dashboard e Relatórios");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Relatórios Gerenciais");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +10");

        JLabel subtitleLabel = new JLabel("Visão geral de desempenho e rankings");
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel kpiPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        kpiPanel.setBackground(Color.WHITE);

        valArrecadado = createValueLabel(THEME_COLOR); // Cor do tema
        valTotalReservas = createValueLabel(Color.BLACK);
        valPendentes = createValueLabel(new Color(234, 179, 8));
        valConfirmadas = createValueLabel(new Color(16, 185, 129));
        valCanceladas = createValueLabel(new Color(239, 68, 68));

        kpiPanel.add(createCard("Faturamento Total", valArrecadado));
        kpiPanel.add(createCard("Total Reservas", valTotalReservas));
        kpiPanel.add(createCard("Pendentes", valPendentes));
        kpiPanel.add(createCard("Confirmadas", valConfirmadas));
        kpiPanel.add(createCard("Canceladas", valCanceladas));

        contentPanel.add(kpiPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);

        JLabel lblTableTitle = new JLabel("Destinos Mais Vendidos");
        lblTableTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +4");
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Destino", "Quantidade Vendida"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        tablePanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        JButton btnRefresh = new JButton("Atualizar Dados");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,15,5,15");
        btnRefresh.addActionListener(e -> carregarDados());
        footerPanel.add(btnRefresh);

        contentPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(220, 220, 220), 1, 15),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setForeground(Color.GRAY);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold small");

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createValueLabel(Color color) {
        JLabel l = new JLabel("...");
        l.setForeground(color);
        l.putClientProperty(FlatClientProperties.STYLE, "font: bold +14");
        return l;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40); // Linhas mais altas
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(245, 158, 11, 40)); // Laranja claro
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
                    label.getBorder(), BorderFactory.createEmptyBorder(0, 10, 0, 0)));
            return label;
        });
    }


    private void carregarDados() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        double total = repo.getTotalArrecadado();
        valArrecadado.setText(nf.format(total));

        int totalR = repo.getTotalReservas();
        valTotalReservas.setText(String.valueOf(totalR));

        Map<String, Integer> status = repo.getTotalPorStatus();

        valPendentes.setText(String.valueOf(status.getOrDefault("Pendente", 0)));
        valConfirmadas.setText(String.valueOf(status.getOrDefault("Confirmada", 0)));
        valCanceladas.setText(String.valueOf(status.getOrDefault("Cancelada", 0)));

        tableModel.setRowCount(0);
        repo.getRankingDestinos().forEach((nome, qtd) ->
                tableModel.addRow(new Object[]{nome, qtd + " vendas"})
        );
    }
}