package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.repos.RelatorioRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class RelatorioFrame extends JFrame {

    private final RelatorioRepo repo = new RelatorioRepo();
    private JLabel valArrecadado, valTotalReservas, valPendentes, valConfirmadas, valCanceladas;
    private JPanel chartContainer;

    public RelatorioFrame() {
        if (!PermissionUtil.requireAdmin(null, "Tentativa de Instanciação Direta", "RelatorioFrame")) {
            dispose();
            return;
        }

        ModernUI.applyTheme(this);
        setTitle("Analytics | Agência++");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        carregarDados();
    }

    private void initUI() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ModernUI.getBgColor());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(ModernUI.getSurfaceColor());
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, ModernUI.getBorderColor()));

        JLabel title = new JLabel("KPIs & Relatórios");
        title.setFont(ModernUI.FONT_HEADER);
        title.setForeground(ModernUI.getTextColor());
        title.setBorder(new EmptyBorder(15, 30, 15, 30));
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 30));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel gridKpi = new JPanel(new GridLayout(1, 5, 20, 0));
        gridKpi.setOpaque(false);
        gridKpi.setPreferredSize(new Dimension(0, 140));

        valArrecadado = createBigValue(ModernUI.COL_PRIMARY_1);
        valTotalReservas = createBigValue(ModernUI.getTextColor());
        valPendentes = createBigValue(ModernUI.COL_ACCENT_WARNING);
        valConfirmadas = createBigValue(ModernUI.COL_ACCENT_SUCCESS);
        valCanceladas = createBigValue(Color.RED);

        gridKpi.add(createKpiCard("Faturamento Total", valArrecadado));
        gridKpi.add(createKpiCard("Vendas Realizadas", valTotalReservas));
        gridKpi.add(createKpiCard("Aguardando Pagto", valPendentes));
        gridKpi.add(createKpiCard("Confirmadas", valConfirmadas));
        gridKpi.add(createKpiCard("Canceladas", valCanceladas));

        content.add(gridKpi, BorderLayout.NORTH);

        JPanel chartCard = ModernUI.createCard();
        chartCard.setLayout(new BorderLayout());

        JLabel lblChart = new JLabel("Destinos Mais Populares");
        lblChart.setFont(ModernUI.FONT_BOLD);
        lblChart.setForeground(ModernUI.getTextGrayColor());
        lblChart.setBorder(new EmptyBorder(0,0,20,0));

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(ModernUI.getSurfaceColor());
        chartContainer.add(new JLabel("Carregando dados...", SwingConstants.CENTER));

        chartCard.add(lblChart, BorderLayout.NORTH);
        chartCard.add(chartContainer, BorderLayout.CENTER);
        content.add(chartCard, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton btnUpdate = ModernUI.createFlatButton("↻ Atualizar Dados", ModernUI.COL_PRIMARY_1);
        btnUpdate.addActionListener(e -> carregarDados());
        footer.add(btnUpdate);
        content.add(footer, BorderLayout.SOUTH);

        main.add(content, BorderLayout.CENTER);
        setContentPane(main);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel) {
        JPanel p = ModernUI.createCard();
        p.setLayout(new BorderLayout());

        JLabel l = new JLabel(title.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(ModernUI.getTextGrayColor());

        p.add(l, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JLabel createBigValue(Color color) {
        JLabel l = new JLabel("...");
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        l.setForeground(color);
        l.setVerticalAlignment(SwingConstants.CENTER);
        return l;
    }

    private void carregarDados() {
        try {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            valArrecadado.setText(nf.format(repo.getTotalArrecadado()));
            valTotalReservas.setText(String.valueOf(repo.getTotalReservas()));

            Map<String, Integer> status = repo.getTotalPorStatus();
            valPendentes.setText(String.valueOf(status.getOrDefault("Pendente", 0)));
            valConfirmadas.setText(String.valueOf(status.getOrDefault("Confirmada", 0)));
            valCanceladas.setText(String.valueOf(status.getOrDefault("Cancelada", 0)));

            Map<String, Map<String, Integer>> ranking = repo.getRankingDestinosPorStatus();

            chartContainer.removeAll();
            if (ranking.isEmpty()) {
                chartContainer.add(new JLabel("Sem dados para exibir", SwingConstants.CENTER));
            } else {
                chartContainer.add(new PainelGraficoPorStatus(ranking), BorderLayout.CENTER);
            }

            chartContainer.revalidate();
            chartContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
