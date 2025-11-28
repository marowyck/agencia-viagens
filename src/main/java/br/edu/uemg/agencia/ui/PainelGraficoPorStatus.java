package br.edu.uemg.agencia.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PainelGraficoPorStatus extends JPanel {

    private final Map<String, Map<String, Integer>> dados;

    public PainelGraficoPorStatus(Map<String, Map<String, Integer>> dados) {
        this.dados = dados;
        setPreferredSize(new Dimension(800, 300));
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

        int areaH = h - paddingBottom - paddingTop;

        Color[] cores = { ModernUI.COL_ACCENT_SUCCESS, ModernUI.COL_ACCENT_WARNING, ModernUI.COL_ACCENT_DANGER };
        String[] statusOrdem = { "Confirmada", "Pendente", "Cancelada" };

        int qtdDestinos = dados.size();
        int larguraDestino = (w - 2 * paddingSide) / qtdDestinos;
        int espacoEntre = 15;

        int x = paddingSide;
        for (Map.Entry<String, Map<String, Integer>> entry : dados.entrySet()) {
            String destino = entry.getKey();
            Map<String, Integer> valores = entry.getValue();
            int y = h - paddingBottom;

            int total = valores.values().stream().mapToInt(Integer::intValue).sum();
            int maxTotal = total == 0 ? 1 : total;

            int corIndex = 0;
            for (String status : statusOrdem) {
                int qtd = valores.getOrDefault(status, 0);
                int altura = (int) (((double) qtd / maxTotal) * areaH);
                if (qtd > 0 && altura < 5) altura = 5;

                y -= altura;
                g2.setColor(cores[corIndex % cores.length]);
                g2.fillRect(x, y, larguraDestino - espacoEntre, altura);
                corIndex++;
            }

            g2.setColor(ModernUI.COL_TEXT_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String labelCurto = destino.length() > 10 ? destino.substring(0, 8) + "..." : destino;
            int lblW = g2.getFontMetrics().stringWidth(labelCurto);
            g2.drawString(labelCurto, x + (larguraDestino - espacoEntre)/2 - lblW/2, h - paddingBottom + 20);

            x += larguraDestino;
        }
    }
}
