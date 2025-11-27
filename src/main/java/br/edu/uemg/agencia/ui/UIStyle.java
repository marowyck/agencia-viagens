package br.edu.uemg.agencia.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UIStyle {

    public static final Color COLOR_PRIMARY     = new Color(99, 102, 241);
    public static final Color COLOR_PRIMARY_DRK = new Color(79, 70, 229);
    public static final Color COLOR_BACKGROUND  = new Color(243, 244, 246);
    public static final Color COLOR_SURFACE     = new Color(255, 255, 255);
    public static final Color COLOR_TEXT_MAIN   = new Color(31, 41, 55);
    public static final Color COLOR_TEXT_LIGHT  = new Color(107, 114, 128);
    public static final Color COLOR_BORDER      = new Color(229, 231, 235);

    public static final Font FONT_TITLE = new Font("Segoe UI Semibold", Font.PLAIN, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI Semibold", Font.PLAIN, 15);
    public static final Font FONT_GENERAL = new Font("Segoe UI Emoji", Font.PLAIN, 14);

    public static void setup() {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("Panel.background", COLOR_BACKGROUND);
            UIManager.put("Label.font", FONT_GENERAL);
            UIManager.put("Label.foreground", COLOR_TEXT_MAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDER, 15),
                new EmptyBorder(20, 25, 20, 25)
        ));
        return card;
    }

    public static JPanel createHeader(String title, String subtitle, String emoji) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel(emoji + " " + title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(COLOR_TEXT_LIGHT);
        lblSub.setBorder(new EmptyBorder(0, 5, 0, 0));

        panel.add(lblTitle);
        panel.add(lblSub);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(20, 0, 20, 0));

        return wrapper;
    }

    public static JButton createButton(String text, String emoji) {
        JButton btn = new JButton(emoji + "  " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(COLOR_PRIMARY_DRK.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_PRIMARY_DRK);
                } else {
                    g2.setColor(COLOR_PRIMARY);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));

        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(240, 240, 240));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                }

                g2.setColor(COLOR_TEXT_LIGHT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BUTTON);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));

        return btn;
    }

    public static JTextField createInput() {
        JTextField txt = new JTextField() {
            @Override
            public void setBorder(javax.swing.border.Border border) {
            }
        };

        txt.setFont(FONT_GENERAL);
        txt.setForeground(COLOR_TEXT_MAIN);
        txt.setBackground(COLOR_SURFACE);
        superSetBorder(txt, new RoundedBorder(COLOR_BORDER, 10));

        txt.setPreferredSize(new Dimension(200, 40));

        return txt;
    }

    private static void superSetBorder(JComponent c, javax.swing.border.Border b) {
        c.setBorder(BorderFactory.createCompoundBorder(b, new EmptyBorder(0, 10, 0, 10)));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(COLOR_PRIMARY_DRK);
        table.setFont(FONT_GENERAL);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer((t, value, isSelected, hasFocus, row, col) -> {
            JLabel l = new JLabel(value.toString());
            l.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
            l.setForeground(COLOR_TEXT_LIGHT);
            l.setBackground(COLOR_BACKGROUND);
            l.setOpaque(true);
            l.setBorder(new EmptyBorder(10, 15, 10, 10));
            return l;
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 15, 0, 0));

                if (!isSelected) {
                    c.setBackground(COLOR_SURFACE);
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                return c;
            }
        });
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color c, int r) {
            this.color = c;
            this.radius = r;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }
}