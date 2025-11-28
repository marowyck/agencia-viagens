package br.edu.uemg.agencia.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernUI {

    public static boolean isDarkMode = false;

    public static final Color COL_BG = Color.decode("#F3F4F6");
    public static final Color COL_SURFACE = Color.WHITE;

    public static final Color COL_PRIMARY_1 = Color.decode("#4F46E5");
    public static final Color COL_PRIMARY_2 = Color.decode("#7C3AED");

    public static final Color COL_TEXT_MAIN = Color.decode("#1F2937");
    public static final Color COL_TEXT_LIGHT = Color.decode("#6B7280");

    public static final Color COL_ACCENT_SUCCESS = Color.decode("#10B981");
    public static final Color COL_ACCENT_DANGER = Color.decode("#EF4444");
    public static final Color COL_ACCENT_WARNING = Color.decode("#F59E0B");
    public static final Color COL_BORDER = Color.decode("#E5E7EB");

    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public static Color getBgColor() { return COL_BG; }
    public static Color getSurfaceColor() { return COL_SURFACE; }
    public static Color getTextColor() { return COL_TEXT_MAIN; }
    public static Color getTextGrayColor() { return COL_TEXT_LIGHT; }
    public static Color getBorderColor() { return COL_BORDER; }

    public static void applyTheme(JFrame frame) {
        frame.getContentPane().setBackground(COL_BG);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}
    }

    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, COL_PRIMARY_1, getWidth(), getHeight(), COL_PRIMARY_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 20, 20);

                g2.setColor(COL_SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        return card;
    }

    public static JPanel createFieldGroup(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COL_TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputComponent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(inputComponent);

        return panel;
    }

    public static JButton createButton(String text, boolean isPrimary) {
        return createStyledButton(text, isPrimary ? COL_PRIMARY_1 : COL_SURFACE, isPrimary ? Color.WHITE : COL_TEXT_MAIN, isPrimary);
    }

    public static JButton createFlatButton(String text, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(textColor);
        btn.setBackground(new Color(0,0,0,0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(textColor.darker()); }
            public void mouseExited(MouseEvent e) { btn.setForeground(textColor); }
        });
        return btn;
    }

    private static JButton createStyledButton(String text, Color bg, Color fg, boolean isGradient) {
        JButton btn = new JButton(text) {
            private boolean hover = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isGradient) {
                    GradientPaint gp = new GradientPaint(0, 0,
                            hover ? COL_PRIMARY_1.brighter() : COL_PRIMARY_1,
                            getWidth(), 0,
                            hover ? COL_PRIMARY_2.brighter() : COL_PRIMARY_2);
                    g2.setPaint(gp);
                } else {
                    g2.setColor(hover ? new Color(245, 247, 250) : bg);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (!isGradient) { // Borda sutil para botões brancos
                    g2.setColor(COL_BORDER);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                }
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                try {
                    java.lang.reflect.Field f = btn.getClass().getDeclaredField("hover");
                    f.setAccessible(true); f.setBoolean(btn, true); btn.repaint();
                } catch(Exception ex) {}
            }
            public void mouseExited(MouseEvent e) {
                try {
                    java.lang.reflect.Field f = btn.getClass().getDeclaredField("hover");
                    f.setAccessible(true); f.setBoolean(btn, false); btn.repaint();
                } catch(Exception ex) {}
            }
        });
        return btn;
    }

    public static JTextField createInput(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COL_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        tf.setFont(FONT_BODY);
        tf.setForeground(COL_TEXT_MAIN);
        tf.setBackground(Color.WHITE);
        tf.setBorder(new EmptyBorder(8, 12, 8, 12));
        return tf;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(COL_BORDER);
        table.setSelectionBackground(new Color(238, 242, 255)); // Indigo claro
        table.setSelectionForeground(COL_PRIMARY_1);
        table.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer((t, value, isSelected, hasFocus, row, col) -> {
            JLabel l = new JLabel(value.toString());
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(COL_TEXT_LIGHT);
            l.setBackground(COL_SURFACE);
            l.setOpaque(true);
            l.setBorder(new EmptyBorder(10, 10, 10, 10));
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0, COL_BORDER),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            return l;
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBorder(new EmptyBorder(0, 15, 0, 0)); // Padding na célula
                if (!s) {
                    comp.setBackground(Color.WHITE);
                    comp.setForeground(COL_TEXT_MAIN);
                }
                return comp;
            }
        });
    }
}