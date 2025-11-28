package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Usuario usuario;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;
        Sessao.setUsuario(usuario);
        ModernUI.applyTheme(this);
        setTitle("Dashboard | Agência++");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel mainLayout = new JPanel(new BorderLayout());

        JPanel sidebar = ModernUI.createGradientPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel lblLogo = new JLabel("AGÊNCIA++");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRole = new JLabel("Painel " + usuario.getPerfil());
        lblRole.setForeground(new Color(255, 255, 255, 150));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblRole);
        sidebar.add(Box.createVerticalStrut(60));

        sidebar.add(createMenuBtn("👥  Clientes", e -> new ClienteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createMenuBtn("🌍  Pacotes", e -> new PacoteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createMenuBtn("📅  Reservas", e -> new ReservaForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(createMenuBtn("📊  Relatórios", e -> {
            if (PermissionUtil.requireAdmin(this, "Acessar Relatórios", "MainFrame")) {
                new RelatorioFrame().setVisible(true);
            }
        }));

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Sair do Sistema");
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(255, 255, 255, 30));
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.addActionListener(e -> dispose());
        sidebar.add(btnLogout);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(ModernUI.COL_BG);
        content.setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel headerInfo = new JPanel(new GridLayout(2, 1));
        headerInfo.setOpaque(false);
        JLabel lblHello = new JLabel("Olá, " + usuario.getNome() + " 👋");
        lblHello.setFont(ModernUI.FONT_HERO);
        lblHello.setForeground(ModernUI.COL_TEXT_MAIN);

        JLabel lblSub = new JLabel("Aqui está o resumo da operação.");
        lblSub.setFont(ModernUI.FONT_BODY);
        lblSub.setForeground(ModernUI.COL_TEXT_LIGHT);
        headerInfo.add(lblHello);
        headerInfo.add(lblSub);

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(0, 200));

        cardsGrid.add(createActionCard("Nova Reserva", "Iniciar Venda", "🛒", ModernUI.COL_PRIMARY_1, e -> new ReservaForm().setVisible(true)));
        cardsGrid.add(createActionCard("Novo Cliente", "Cadastrar", "👤", ModernUI.COL_ACCENT_SUCCESS, e -> new ClienteForm().setVisible(true)));

        cardsGrid.add(createActionCard("Relatórios", "Ver Dados", "📈", ModernUI.COL_ACCENT_DANGER, e -> {
            if (PermissionUtil.requireAdmin(this, "Acessar Relatórios (Atalho)", "MainFrame")) {
                new RelatorioFrame().setVisible(true);
            }
        }));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(headerInfo, BorderLayout.NORTH);
        topSection.add(Box.createVerticalStrut(40), BorderLayout.CENTER);

        content.add(topSection, BorderLayout.NORTH);
        content.add(cardsGrid, BorderLayout.CENTER);

        mainLayout.add(sidebar, BorderLayout.WEST);
        mainLayout.add(content, BorderLayout.CENTER);

        setContentPane(mainLayout);
    }

    private JButton createMenuBtn(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(new Color(255, 255, 255, 200));
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 45));
        btn.addActionListener(action);
        return btn;
    }

    private JPanel createActionCard(String title, String btnText, String icon, Color accent, java.awt.event.ActionListener action) {
        JPanel card = ModernUI.createCard();
        card.setLayout(new BorderLayout());

        JPanel inner = new JPanel(new GridLayout(3, 1));
        inner.setOpaque(false);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ModernUI.FONT_H2);
        lblTitle.setForeground(ModernUI.COL_TEXT_MAIN);

        JButton btn = ModernUI.createButton(btnText, false);
        btn.setForeground(accent);
        btn.addActionListener(action);

        inner.add(lblIcon);
        inner.add(lblTitle);
        inner.add(btn);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }
}