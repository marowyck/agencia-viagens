package br.edu.uemg.agencia.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PainelGraficoPorStatus extends JPanel {

    private final Map<String, Map<String, Integer>> dados;

    public PainelGraficoPorStatus(Map<String, Map<String, Integer>> dados) {
        this.dados = dados;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dados == null || dados.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int paddingBottom = 30;
        int paddingTop = 10;
        int areaH = h - paddingBottom - paddingTop;

        Color[] cores = { ModernUI.SUCCESS, ModernUI.BRAND, ModernUI.DANGER };
        String[] statusOrdem = { "Confirmada", "Pendente", "Cancelada" };

        int qtdDestinos = dados.size();
        int barMaxWidth = 60;
        int spaceAvailable = w / qtdDestinos;
        int barWidth = Math.min(spaceAvailable - 20, barMaxWidth);

        int x = 20;

        int maxGlobal = 0;
        for (var entry : dados.values()) {
            int total = entry.values().stream().mapToInt(Integer::intValue).sum();
            if (total > maxGlobal) maxGlobal = total;
        }
        if (maxGlobal == 0) maxGlobal = 1;

        for (Map.Entry<String, Map<String, Integer>> entry : dados.entrySet()) {
            String destino = entry.getKey();
            Map<String, Integer> valores = entry.getValue();

            int y = h - paddingBottom;

            int corIndex = 0;
            for (String status : statusOrdem) {
                int qtd = valores.getOrDefault(status, 0);

                int altura = (int) (((double) qtd / maxGlobal) * areaH);

                if (qtd > 0) {
                    if(altura < 4) altura = 4;
                    y -= altura;

                    g2.setColor(cores[corIndex % cores.length]);
                    g2.fillRoundRect(x, y, barWidth, altura, 6, 6);
                }
                corIndex++;
            }

            g2.setColor(ModernUI.COL_TEXT_BODY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

            String lbl = destino.length() > 10 ? destino.substring(0, 8) + ".." : destino;

            int txtW = g2.getFontMetrics().stringWidth(lbl);
            int txtX = x + (barWidth / 2) - (txtW / 2);

            g2.drawString(lbl, txtX, h - paddingBottom + 20);

            x += spaceAvailable;
        }
    }
}