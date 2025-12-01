package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ComparadorFrame extends JFrame {

    public ComparadorFrame(List<Pacote> pacotes) {
        ModernUI.setupTheme(this);
        setTitle("Comparador de Pacotes");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI(pacotes);
    }

    private void initUI(List<Pacote> pacotes) {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Comparativo Lado a Lado");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);
        main.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 3, 20, 0));
        grid.setOpaque(false);

        if (pacotes.isEmpty()) {
            grid.add(new JLabel("Nenhum pacote selecionado."));
        } else {
            for (Pacote p : pacotes) {
                grid.add(createPackageCard(p));
            }
        }

        main.add(grid, BorderLayout.CENTER);
        setContentPane(main);
    }

    private JPanel createPackageCard(Pacote p) {
        JPanel card = ModernUI.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel(p instanceof PacoteInternacional ? "✈" : "🚌");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel dest = new JLabel(p.getDestino());
        dest.setFont(ModernUI.FONT_H1);
        dest.setForeground(ModernUI.COL_TEXT_H1);
        dest.setAlignmentX(CENTER_ALIGNMENT);

        JLabel price = new JLabel(String.format("R$ %.2f", p.calcularValorFinal()));
        price.setFont(ModernUI.FONT_BIG);
        price.setForeground(ModernUI.BRAND);
        price.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(20));
        card.add(icon);
        card.add(Box.createVerticalStrut(15));
        card.add(dest);
        card.add(Box.createVerticalStrut(10));
        card.add(price);
        card.add(Box.createVerticalStrut(30));

        card.add(createRow("Duração", p.getDuracao() + " dias"));
        card.add(createRow("Tipo", p instanceof PacoteInternacional ? "Internacional" : "Nacional"));
        card.add(createRow("Valor Base", "R$ " + p.getValorBase()));

        if (p instanceof PacoteInternacional pi) {
            card.add(createRow("Moeda", pi.getMoeda()));
            card.add(createRow("Taxa Emb.", "R$ " + pi.getTaxaEmbarque()));
        } else {
            card.add(createRow("Imposto", "5% (Incluso)"));
        }

        return card;
    }

    private JPanel createRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(400, 35));
        p.setBorder(BorderFactory.createMatteBorder(0,0,1,0, ModernUI.COL_BORDER));

        JLabel l = new JLabel(label);
        l.setForeground(ModernUI.COL_TEXT_BODY);
        l.setFont(ModernUI.FONT_PLAIN);

        JLabel v = new JLabel(value);
        v.setFont(ModernUI.FONT_BOLD);
        v.setForeground(ModernUI.COL_TEXT_H1);

        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }
}