package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.repos.RelatorioRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class RelatorioFrame extends JFrame {

    private final RelatorioRepo repo = new RelatorioRepo();

    private JLabel lblFaturamento, lblVendas, lblTicketMedio, lblCancelamento;
    private JPanel chartArea, statusArea;

    public RelatorioFrame() {
        if (!PermissionUtil.requireAdmin(null, "Tentativa de Acesso", "Analytics")) {
            dispose();
            return;
        }

        ModernUI.setupTheme(this);
        setTitle("Agência++ | Business Intelligence");
        setSize(1200, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        carregarDadosReais();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(30, 30));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Visão Geral de Performance");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);

        JLabel sub = new JLabel("Métricas em tempo real");
        sub.setFont(ModernUI.FONT_PLAIN);
        sub.setForeground(ModernUI.COL_TEXT_BODY);

        JPanel texts = new JPanel(new GridLayout(2,1));
        texts.setOpaque(false);
        texts.add(title); texts.add(sub);

        JButton btnRefresh = ModernUI.createOutlineButton("Atualizar Dados ↻");
        btnRefresh.setPreferredSize(new Dimension(160, 40));
        btnRefresh.addActionListener(e -> carregarDadosReais());

        header.add(texts, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setPreferredSize(new Dimension(0, 140));

        lblFaturamento = new JLabel("R$ 0,00");
        lblVendas = new JLabel("0");
        lblTicketMedio = new JLabel("R$ 0,00");
        lblCancelamento = new JLabel("0%");

        kpiGrid.add(createKpiCard("Receita Total", lblFaturamento, "💰", ModernUI.BRAND));
        kpiGrid.add(createKpiCard("Vendas Totais", lblVendas, "🛒", ModernUI.SUCCESS));
        kpiGrid.add(createKpiCard("Ticket Médio", lblTicketMedio, "💳", ModernUI.ACCENT));
        kpiGrid.add(createKpiCard("Cancelamentos", lblCancelamento, "📉", ModernUI.DANGER));

        JPanel chartsPanel = new JPanel(new BorderLayout(20, 0));
        chartsPanel.setOpaque(false);

        JPanel leftChartCard = ModernUI.createCard();
        leftChartCard.setLayout(new BorderLayout());
        JLabel lTitleChart = new JLabel("Top Destinos & Performance");
        lTitleChart.setFont(ModernUI.FONT_H1);
        lTitleChart.setForeground(ModernUI.COL_TEXT_H1);
        lTitleChart.setBorder(new EmptyBorder(0, 0, 20, 0));

        chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);

        leftChartCard.add(lTitleChart, BorderLayout.NORTH);
        leftChartCard.add(chartArea, BorderLayout.CENTER);

        JPanel rightStatusCard = ModernUI.createCard();
        rightStatusCard.setPreferredSize(new Dimension(350, 0));
        rightStatusCard.setLayout(new BorderLayout());

        JLabel lTitleStatus = new JLabel("Status dos Pedidos");
        lTitleStatus.setFont(ModernUI.FONT_H1);
        lTitleStatus.setForeground(ModernUI.COL_TEXT_H1);
        lTitleStatus.setBorder(new EmptyBorder(0, 0, 20, 0));

        statusArea = new JPanel();
        statusArea.setLayout(new BoxLayout(statusArea, BoxLayout.Y_AXIS));
        statusArea.setOpaque(false);

        rightStatusCard.add(lTitleStatus, BorderLayout.NORTH);
        rightStatusCard.add(statusArea, BorderLayout.CENTER);

        chartsPanel.add(leftChartCard, BorderLayout.CENTER);
        chartsPanel.add(rightStatusCard, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 30));
        centerWrapper.setOpaque(false);
        centerWrapper.add(kpiGrid, BorderLayout.NORTH);
        centerWrapper.add(chartsPanel, BorderLayout.CENTER);

        main.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(main);
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String icon, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ModernUI.COL_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(accent);
                g2.fillRoundRect(0, getHeight()-6, getWidth(), 6, 20, 20);

                GradientPaint gp = new GradientPaint(0,0, new Color(255,255,255, ModernUI.isDark?10:50), 0, getHeight(), new Color(0,0,0,0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lTitle.setForeground(ModernUI.COL_TEXT_BODY);

        valLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valLabel.setForeground(ModernUI.COL_TEXT_H1);

        JLabel lIcon = new JLabel(icon);
        lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textP = new JPanel(new GridLayout(2, 1));
        textP.setOpaque(false);
        textP.add(lTitle);
        textP.add(valLabel);

        card.add(textP, BorderLayout.CENTER);
        card.add(lIcon, BorderLayout.EAST);

        return card;
    }

    private void carregarDadosReais() {
        new Thread(() -> {
            try {
                double totalFat = repo.getTotalArrecadado();
                int totalVendas = repo.getTotalReservas();
                Map<String, Integer> status = repo.getTotalPorStatus();
                var ranking = repo.getRankingDestinosPorStatus();

                double ticketMedio = totalVendas > 0 ? totalFat / totalVendas : 0.0;
                int canceladas = status.getOrDefault("Cancelada", 0);
                double taxaCancel = totalVendas > 0 ? ((double) canceladas / totalVendas) * 100 : 0.0;

                SwingUtilities.invokeLater(() -> {
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                    lblFaturamento.setText(nf.format(totalFat));
                    lblVendas.setText(String.valueOf(totalVendas));
                    lblTicketMedio.setText(nf.format(ticketMedio));
                    lblCancelamento.setText(String.format("%.1f%%", taxaCancel));

                    chartArea.removeAll();
                    if (ranking.isEmpty()) {
                        chartArea.add(new JLabel("Sem dados suficientes.", SwingConstants.CENTER));
                    } else {
                        chartArea.add(new PainelGraficoPorStatus(ranking), BorderLayout.CENTER);
                    }
                    chartArea.revalidate();
                    chartArea.repaint();

                    statusArea.removeAll();
                    statusArea.add(createStatusRow("Confirmadas", status.getOrDefault("Confirmada", 0), totalVendas, ModernUI.SUCCESS));
                    statusArea.add(Box.createVerticalStrut(15));
                    statusArea.add(createStatusRow("Pendentes", status.getOrDefault("Pendente", 0), totalVendas, ModernUI.BRAND));
                    statusArea.add(Box.createVerticalStrut(15));
                    statusArea.add(createStatusRow("Canceladas", status.getOrDefault("Cancelada", 0), totalVendas, ModernUI.DANGER));
                    statusArea.revalidate();
                    statusArea.repaint();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private JPanel createStatusRow(String label, int val, int total, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lName = new JLabel(label);
        lName.setFont(ModernUI.FONT_BOLD);
        lName.setForeground(ModernUI.COL_TEXT_H1);

        JLabel lVal = new JLabel(String.valueOf(val));
        lVal.setFont(ModernUI.FONT_BOLD);
        lVal.setForeground(color);

        top.add(lName, BorderLayout.WEST);
        top.add(lVal, BorderLayout.EAST);

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ModernUI.COL_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                double pct = total == 0 ? 0 : (double) val / total;
                int fillW = (int) (getWidth() * pct);

                g2.setColor(color);
                g2.fillRoundRect(0, 0, fillW, getHeight(), 10, 10);
            }
        };
        bar.setPreferredSize(new Dimension(0, 12));
        bar.setOpaque(false);

        p.add(top, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }
}