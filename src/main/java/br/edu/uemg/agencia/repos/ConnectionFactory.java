package br.edu.uemg.agencia.repos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:agencia_viagens.db";

    static {
        try {
            String path = new File("agencia_viagens.db").getAbsolutePath();
            System.out.println("USING DB FILE: " + path);
        } catch (Exception e) {
            System.out.println("Failed to get DB absolute path");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
