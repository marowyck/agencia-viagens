package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.AuthService;
import br.edu.uemg.agencia.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        ModernUI.applyTheme(this);
        setTitle("Agência++ Login");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel bg = ModernUI.createGradientPanel();
        bg.setLayout(new GridBagLayout());

        JPanel card = ModernUI.createCard();
        card.setPreferredSize(new Dimension(420, 520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel("🚀");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Bem-vindo");
        lblTitle.setFont(ModernUI.FONT_HERO);
        lblTitle.setForeground(ModernUI.COL_TEXT_MAIN);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Faça login para continuar");
        lblSub.setFont(ModernUI.FONT_BODY);
        lblSub.setForeground(ModernUI.COL_TEXT_LIGHT);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        txtUser = ModernUI.createInput("Usuário");
        txtUser.setMaximumSize(new Dimension(400, 50));

        txtPass = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        txtPass.setBorder(new EmptyBorder(10, 15, 10, 15));
        txtPass.setFont(ModernUI.FONT_BODY);
        txtPass.setMaximumSize(new Dimension(400, 50));

        JButton btnLogin = ModernUI.createButton("ACESSAR SISTEMA", true);
        btnLogin.setMaximumSize(new Dimension(400, 50));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnExit = ModernUI.createButton("Sair", false);
        btnExit.setMaximumSize(new Dimension(400, 50));
        btnExit.setForeground(ModernUI.COL_ACCENT_DANGER); // Ajuste de cor manual
        btnExit.addActionListener(e -> System.exit(0));

        KeyAdapter ka = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtUser.addKeyListener(ka);
        txtPass.addKeyListener(ka);

        card.add(Box.createVerticalStrut(20));
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTitle);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(40));
        card.add(ModernUI.createFieldGroup("Usuário", txtUser));
        card.add(Box.createVerticalStrut(15));
        card.add(ModernUI.createFieldGroup("Senha", txtPass));
        card.add(Box.createVerticalStrut(30));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnExit);

        bg.add(card);
        setContentPane(bg);
    }

    private void doLogin() {
        Usuario u = AuthService.login(txtUser.getText(), new String(txtPass.getPassword()));
        if (u != null) {
            dispose();
            new MainFrame(u).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Credenciais inválidas.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}