package br.edu.uemg.agencia.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernUI {

    public static boolean isDark = true;

    public static Color COL_BG;
    public static Color COL_SIDEBAR;
    public static Color COL_CARD;
    public static Color COL_TEXT_H1;
    public static Color COL_TEXT_BODY;
    public static Color COL_INPUT;
    public static Color COL_BORDER;

    public static final Color BRAND = Color.decode("#6C63FF");
    public static final Color ACCENT = Color.decode("#00D2D3");
    public static final Color DANGER = Color.decode("#FF6B6B");
    public static final Color SUCCESS = Color.decode("#1DD1A1");

    public static final Font FONT_BIG = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_PLAIN = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_ICON = new Font("Segoe UI Emoji", Font.PLAIN, 18);

    static {
        updateThemeColors();
    }

    public static void updateThemeColors() {
        if (isDark) {
            COL_BG = Color.decode("#121212");
            COL_SIDEBAR = Color.decode("#1A1A1A");
            COL_CARD = Color.decode("#1E1E1E");
            COL_TEXT_H1 = Color.decode("#FFFFFF");
            COL_TEXT_BODY = Color.decode("#B3B3B3");
            COL_INPUT = Color.decode("#2C2C2C");
            COL_BORDER = Color.decode("#333333");
        } else {
            COL_BG = Color.decode("#F4F7FC");
            COL_SIDEBAR = Color.decode("#FFFFFF");
            COL_CARD = Color.decode("#FFFFFF");
            COL_TEXT_H1 = Color.decode("#2D3436");
            COL_TEXT_BODY = Color.decode("#636E72");
            COL_INPUT = Color.decode("#EDF2F7");
            COL_BORDER = Color.decode("#DFE6E9");
        }
    }

    public static void setupTheme(JFrame frame) {
        updateThemeColors();
        frame.getContentPane().setBackground(COL_BG);
        try {
            UIManager.put("ScrollBar.thumb", new javax.swing.plaf.ColorUIResource(BRAND));
            UIManager.put("ScrollBar.track", new javax.swing.plaf.ColorUIResource(COL_BG));
            UIManager.put("ScrollBar.width", 10);
        } catch(Exception ignored){}
    }

    public static void toggleTheme(JFrame frame) {
        isDark = !isDark;
        setupTheme(frame);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(!isDark) {
                    g2.setColor(new Color(0,0,0,10));
                    g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 15, 15);
                } else {
                    g2.setColor(new Color(255,255,255,10));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1, 15, 15);
                }
                g2.setColor(COL_CARD);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        return card;
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, BRAND,
                        getWidth(), 0, getModel().isRollover() ? BRAND.brighter() : BRAND.darker()
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
            }
        };
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return btn;
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(BRAND.getRed(), BRAND.getGreen(), BRAND.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(BRAND);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 10, 10);

                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
            }
        };
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return btn;
    }

    public static JTextField createInput(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(COL_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                if (isFocusOwner()) {
                    g2.setColor(BRAND);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                }
                super.paintComponent(g);
            }
        };
        tf.setText(placeholder);
        tf.setFont(FONT_PLAIN);
        tf.setForeground(COL_TEXT_BODY);
        tf.setCaretColor(BRAND);
        tf.setOpaque(false);

        tf.setBorder(new EmptyBorder(5, 10, 5, 10));
        tf.setPreferredSize(new Dimension(200, 35));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(COL_TEXT_H1); }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(COL_TEXT_BODY); }
            }
        });
        return tf;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(FONT_PLAIN);
        table.setBackground(COL_CARD);
        table.setForeground(COL_TEXT_H1);
        table.setSelectionBackground(new Color(108, 99, 255, 50));
        table.setSelectionForeground(COL_TEXT_H1);
        table.setShowVerticalLines(false);
        table.setGridColor(COL_BORDER);
        table.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer((t, value, isSelected, hasFocus, row, col) -> {
            JLabel l = new JLabel(value.toString().toUpperCase());
            l.setFont(new Font("SansSerif", Font.BOLD, 10));
            l.setForeground(COL_TEXT_BODY);
            l.setBackground(COL_BG);
            l.setOpaque(true);
            l.setBorder(new EmptyBorder(8, 10, 8, 10));
            return l;
        });

        if(table.getParent() instanceof JViewport) ((JComponent)table.getParent()).setBackground(COL_CARD);
    }

    public static JPanel createLabelGroup(String text, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0,4));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(COL_TEXT_BODY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}