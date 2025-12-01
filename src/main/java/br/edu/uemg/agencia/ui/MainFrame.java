package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.modelo.Usuario;
import br.edu.uemg.agencia.servico.ReservaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {

    private final Usuario usuario;
    private JTextField txtSearch;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;
        Sessao.setUsuario(usuario);

        setTitle("Agência Viagens++ | Pro");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        iniciarRobo();
    }

    private void iniciarRobo() {
        new Thread(() -> {
            try {
                new ReservaService().verificarExpiracao();
            } catch (Exception e) {
                System.err.println("Falha no robô de expiração: " + e.getMessage());
            }
        }).start();
    }

    private void initUI() {
        ModernUI.setupTheme(this);
        JPanel layout = new JPanel(new BorderLayout());
        layout.setBackground(ModernUI.COL_BG);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ModernUI.COL_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(40, 25, 40, 25));

        JLabel logo = new JLabel("AGÊNCIA++");
        logo.setFont(ModernUI.FONT_BIG);
        logo.setForeground(ModernUI.BRAND);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(50));

        sidebar.add(createSidebarItem("Clientes", "👤", e -> new ClienteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Pacotes", "🌎", e -> new PacoteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Reservas", "📅", e -> new ReservaForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Analytics", "📊", e -> {
            if(PermissionUtil.requireAdmin(this, "Relatórios", "Dash")) new RelatorioFrame().setVisible(true);
        }));

        sidebar.add(Box.createVerticalGlue());

        JButton btnTheme = new JButton("Alternar Tema 🌗");
        btnTheme.setForeground(ModernUI.COL_TEXT_BODY);
        btnTheme.setBorderPainted(false);
        btnTheme.setContentAreaFilled(false);
        btnTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTheme.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTheme.addActionListener(e -> {
            ModernUI.toggleTheme(this);
            dispose();
            new MainFrame(usuario).setVisible(true);
        });
        sidebar.add(btnTheme);

        JButton btnExit = createSidebarItem("Sair", "🚪", e -> dispose());
        btnExit.setForeground(ModernUI.DANGER);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnExit);

        JPanel content = new JPanel(new BorderLayout(0, 30));
        content.setBackground(ModernUI.COL_BG);
        content.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel texts = new JPanel(new GridLayout(2,1));
        texts.setOpaque(false);
        JLabel title = new JLabel("Olá, " + usuario.getNome() + " 👋");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);
        JLabel sub = new JLabel("Bem-vindo ao sistema de gestão.");
        sub.setFont(ModernUI.FONT_PLAIN);
        sub.setForeground(ModernUI.COL_TEXT_BODY);
        texts.add(title); texts.add(sub);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        txtSearch = ModernUI.createInput("Comando rápido (Ex: novo cliente)...");
        txtSearch.setPreferredSize(new Dimension(300, 35));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) executeCommand(); }
        });
        searchPanel.add(txtSearch, BorderLayout.EAST);

        header.add(texts, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 25, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 200));

        cards.add(createStatCard("Vendas", "Nova Reserva", "🛒", ModernUI.BRAND, e -> new ReservaForm().setVisible(true)));
        cards.add(createStatCard("CRM", "Novo Cliente", "✨", ModernUI.SUCCESS, e -> new ClienteForm().setVisible(true)));
        cards.add(createStatCard("Admin", "Relatórios", "📈", ModernUI.ACCENT, e -> {
            if(PermissionUtil.requireAdmin(this, "Rel", "Dash")) new RelatorioFrame().setVisible(true);
        }));

        content.add(header, BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);

        layout.add(sidebar, BorderLayout.WEST);
        layout.add(content, BorderLayout.CENTER);

        setContentPane(layout);

        sidebar.add(createSidebarItem("Clientes", "👤", e -> new ClienteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Pacotes", "🌎", e -> new PacoteForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Reservas", "📅", e -> new ReservaForm().setVisible(true)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createSidebarItem("Analytics", "📊", e -> {
            if(PermissionUtil.requireAdmin(this, "Relatórios", "Dash")) new RelatorioFrame().setVisible(true);
        }));

        if (PermissionUtil.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(createSidebarItem("Auditoria", "🛡️", e -> new AuditFrame().setVisible(true)));
        }
    }

    private JButton createSidebarItem(String text, String icon, java.awt.event.ActionListener act) {
        JButton btn = new JButton(icon + "   " + text);
        btn.setFont(ModernUI.FONT_BOLD);
        btn.setForeground(ModernUI.COL_TEXT_H1);
        btn.setBackground(new Color(0,0,0,0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 45));
        btn.addActionListener(act);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(ModernUI.BRAND); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(ModernUI.COL_TEXT_H1); }
        });
        return btn;
    }

    private JPanel createStatCard(String sup, String main, String icon, Color cor, java.awt.event.ActionListener act) {
        JPanel card = ModernUI.createCard();
        card.setLayout(new BorderLayout());

        JLabel lSup = new JLabel(sup.toUpperCase());
        lSup.setFont(new Font("SansSerif", Font.BOLD, 11));
        lSup.setForeground(ModernUI.COL_TEXT_BODY);

        JLabel lMain = new JLabel(main);
        lMain.setFont(ModernUI.FONT_H1);
        lMain.setForeground(ModernUI.COL_TEXT_H1);

        JLabel lIcon = new JLabel(icon);
        lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JButton btn = ModernUI.createOutlineButton("Acessar");
        btn.addActionListener(act);

        JPanel top = new JPanel(new GridLayout(2,1)); top.setOpaque(false);
        top.add(lSup); top.add(lMain);

        card.add(top, BorderLayout.NORTH);
        card.add(lIcon, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private void executeCommand() {
        String cmd = txtSearch.getText().toLowerCase();
        if(cmd.contains("sair")) dispose();
        else if(cmd.contains("reserva")) new ReservaForm().setVisible(true);
        else if(cmd.contains("cliente")) new ClienteForm().setVisible(true);
        else if(cmd.contains("pacote")) new PacoteForm().setVisible(true);
        else {
            var list = new br.edu.uemg.agencia.repos.ClienteRepo().searchByName(cmd);
            if(!list.isEmpty()) {
                ClienteForm f = new ClienteForm(); f.setVisible(true);
                JOptionPane.showMessageDialog(f, "Encontrei " + list.size() + " clientes!");
            } else JOptionPane.showMessageDialog(this, "Nada encontrado.");
        }
        txtSearch.setText("");
    }
}