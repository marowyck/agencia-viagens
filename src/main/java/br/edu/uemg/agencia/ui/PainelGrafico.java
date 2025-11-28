package br.edu.uemg.agencia.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PainelGrafico extends JPanel {

    private final Map<String, Integer> dados;

    public PainelGrafico(Map<String, Integer> dados) {
        this.dados = dados;
        setPreferredSize(new Dimension(600, 250));
        setBackground(ModernUI.COL_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (dados == null || dados.isEmpty()) {
            g.setColor(ModernUI.COL_TEXT_LIGHT);
            g.drawString("Sem dados para exibir", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int paddingBottom = 40;
        int paddingTop = 30;
        int paddingSide = 40;

        int areaUtilH = h - paddingBottom - paddingTop;

        int maxVal = dados.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        int qtdBarras = dados.size();
        int larguraBarra = (w - (2 * paddingSide)) / qtdBarras;
        int espacoEntre = 20;

        int x = paddingSide;

        Color[] cores = {
                ModernUI.COL_PRIMARY_1,
                ModernUI.COL_PRIMARY_2,
                ModernUI.COL_ACCENT_SUCCESS,
                ModernUI.COL_ACCENT_WARNING,
                ModernUI.COL_ACCENT_DANGER
        };
        int corIndex = 0;

        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            String label = entry.getKey();
            int valor = entry.getValue();

            int alturaBarra = (int) (((double) valor / maxVal) * areaUtilH);
            if (valor > 0 && alturaBarra < 5) alturaBarra = 5;

            int yPos = h - paddingBottom - alturaBarra;
            int barWidth = larguraBarra - espacoEntre;

            g2.setColor(cores[corIndex % cores.length]);
            g2.fillRoundRect(x, yPos, barWidth, alturaBarra, 8, 8);

            g2.setColor(ModernUI.COL_TEXT_MAIN);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String valStr = String.valueOf(valor);
            int strW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (barWidth / 2) - (strW / 2), yPos - 5);

            g2.setColor(ModernUI.COL_TEXT_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            String labelCurto = label.length() > 10 ? label.substring(0, 8) + "..." : label;
            int lblW = g2.getFontMetrics().stringWidth(labelCurto);
            g2.drawString(labelCurto, x + (barWidth / 2) - (lblW / 2), h - paddingBottom + 20);

            x += larguraBarra;
            corIndex++;
        }
    }
}