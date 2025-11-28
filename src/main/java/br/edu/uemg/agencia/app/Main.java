package br.edu.uemg.agencia.app;

import br.edu.uemg.agencia.repos.DatabaseInitializer;
import br.edu.uemg.agencia.ui.LoginFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println(">> Iniciando Agência Viagens++...");
            DatabaseInitializer.init();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro crítico ao conectar ao banco de dados:\n" + e.getMessage(),
                    "Erro Fatal",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}