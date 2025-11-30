package br.edu.uemg.agencia.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PainelGrafico extends JPanel {

    private final Map<String, Integer> dados;

    public PainelGrafico(Map<String, Integer> dados) {
        this.dados = dados;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dados == null || dados.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int paddingBottom = 40;
        int areaH = h - paddingBottom - 10;

        int maxVal = dados.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int qtd = dados.size();

        int barMaxWidth = 80;
        int space = w / qtd;
        int width = Math.min(space - 20, barMaxWidth);
        int x = 20;

        Color[] cores = { ModernUI.BRAND, ModernUI.ACCENT, ModernUI.SUCCESS, ModernUI.DANGER };
        int i = 0;

        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            int val = entry.getValue();
            int height = (int) (((double) val / maxVal) * areaH);
            if (val > 0 && height < 5) height = 5;

            int y = h - paddingBottom - height;

            g2.setColor(cores[i % cores.length]);
            g2.fillRoundRect(x, y, width, height, 10, 10);

            g2.setColor(ModernUI.COL_TEXT_H1);
            g2.setFont(ModernUI.FONT_BOLD);
            String valStr = String.valueOf(val);
            int vW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (width/2) - (vW/2), y - 5);

            g2.setColor(ModernUI.COL_TEXT_BODY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            String lbl = entry.getKey();
            if(lbl.length() > 8) lbl = lbl.substring(0,6)+"..";
            int lW = g2.getFontMetrics().stringWidth(lbl);
            g2.drawString(lbl, x + (width/2) - (lW/2), h - paddingBottom + 20);

            x += space;
            i++;
        }
    }
}