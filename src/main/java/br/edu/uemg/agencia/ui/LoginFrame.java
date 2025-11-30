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
        ModernUI.setupTheme(this);
        setTitle("Login | Agência++");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ModernUI.BRAND, getWidth(), getHeight(), ModernUI.ACCENT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255,255,255,30));
                g2.fillOval(-50, -50, 300, 300);
                g2.fillOval(getWidth()-200, getHeight()-200, 400, 400);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 40));
                g2.drawString("Agência++", 40, getHeight()/2);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g2.drawString("Sua próxima viagem começa aqui.", 40, getHeight()/2 + 40);
            }
        };

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(ModernUI.COL_CARD);
        rightPanel.setBorder(new EmptyBorder(80, 50, 80, 50));

        JLabel title = new JLabel("Bem-vindo");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser = ModernUI.createInput("Usuário");
        txtUser.setMaximumSize(new Dimension(400, 35));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass = new JPasswordField();
        txtPass.setFont(ModernUI.FONT_PLAIN);
        txtPass.setForeground(ModernUI.COL_TEXT_H1);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.COL_BORDER),
                new EmptyBorder(5,10,5,10)
        ));
        txtPass.setBackground(ModernUI.COL_INPUT);
        txtPass.setMaximumSize(new Dimension(400, 35));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn = ModernUI.createButton("ENTRAR NA CONTA");
        btn.setMaximumSize(new Dimension(400, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> doLogin());

        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(ModernUI.createLabelGroup("USUÁRIO", txtUser));
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(ModernUI.createLabelGroup("SENHA", txtPass));
        rightPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(btn);

        KeyAdapter ka = new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        };
        txtUser.addKeyListener(ka); txtPass.addKeyListener(ka);

        main.add(leftPanel);
        main.add(rightPanel);
        setContentPane(main);
    }

    private void doLogin() {
        Usuario u = AuthService.login(txtUser.getText(), new String(txtPass.getPassword()));
        if (u != null) {
            dispose();
            new MainFrame(u).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso Negado");
        }
    }
}