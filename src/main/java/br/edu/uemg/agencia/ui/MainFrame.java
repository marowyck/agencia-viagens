package br.edu.uemg.agencia.ui;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Agência Viagens++");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 145));
        headerPanel.setBorder(new EmptyBorder(25, 0, 25, 0));

        JLabel titleLabel = new JLabel("AGÊNCIA VIAGENS++", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +12");

        JLabel subtitleLabel = new JLabel("Sistema de Gestão Integrado", SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(200, 200, 255));
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "font: regular");

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));

        gridPanel.add(createCardButton("Clientes", "Gerenciar cadastro", new Color(59, 130, 246), e -> {
            new ClienteForm().setVisible(true);
        }));

        gridPanel.add(createCardButton("Pacotes", "Destinos e preços", new Color(139, 92, 246), e -> {
            new PacoteForm().setVisible(true);
        }));

        gridPanel.add(createCardButton("Reservas", "Vendas e tickets", new Color(16, 185, 129), e -> {
            new ReservaForm().setVisible(true);
        }));

        gridPanel.add(createCardButton("Relatórios", "Dados do sistema", new Color(245, 158, 11), e -> {
            new RelatorioFrame().setVisible(true);
        }));

        contentPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton createCardButton(String title, String subtitle, Color bgColor, java.awt.event.ActionListener action) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());

        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +8");

        JLabel lblSubtitle = new JLabel(subtitle, SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.putClientProperty(FlatClientProperties.STYLE, "font: regular -1");

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        btn.add(textPanel, BorderLayout.CENTER);
        btn.addActionListener(action);

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Erro ao carregar tema");
        }

        EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}