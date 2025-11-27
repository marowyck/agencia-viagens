package br.edu.uemg.agencia.app;

import br.edu.uemg.agencia.repos.DatabaseInitializer;
import br.edu.uemg.agencia.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        FlatLightLaf.setup();

        DatabaseInitializer.init();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
