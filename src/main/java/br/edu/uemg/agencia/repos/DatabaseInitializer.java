package br.edu.uemg.agencia.repos;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS cliente (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT NOT NULL," +
                            "cpf TEXT UNIQUE NOT NULL," +
                            "email TEXT," +
                            "telefone TEXT" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pacote (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "tipo TEXT CHECK(tipo IN ('nacional','internacional')) NOT NULL," +
                            "destino TEXT NOT NULL," +
                            "duracao INTEGER NOT NULL," +
                            "valor_base REAL NOT NULL," +
                            "imposto_turismo REAL," +
                            "moeda TEXT," +
                            "taxa_cambio REAL," +
                            "taxa_embarque REAL" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS reserva (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "cliente_id INTEGER NOT NULL," +
                            "pacote_id INTEGER NOT NULL," +
                            "status TEXT CHECK(status IN ('Criada','Pendente','Confirmada','Cancelada','Expirada')) NOT NULL DEFAULT 'Criada'," +
                            "data_reserva TEXT NOT NULL," +
                            "valor_total REAL," +
                            "FOREIGN KEY (cliente_id) REFERENCES cliente(id)," +
                            "FOREIGN KEY (pacote_id) REFERENCES pacote(id)" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pagamento (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "reserva_id INTEGER UNIQUE NOT NULL," +
                            "metodo TEXT CHECK(metodo IN ('pix','cartao')) NOT NULL," +
                            "taxa REAL," +
                            "data_pagamento TEXT NOT NULL," +
                            "FOREIGN KEY (reserva_id) REFERENCES reserva(id)" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS favorito (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "cliente_id INTEGER NOT NULL," +
                            "pacote_id INTEGER NOT NULL," +
                            "FOREIGN KEY (cliente_id) REFERENCES cliente(id)," +
                            "FOREIGN KEY (pacote_id) REFERENCES pacote(id)" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS log_operacoes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "usuario TEXT," +
                            "acao TEXT," +
                            "data_hora TEXT NOT NULL" +
                            ");"
            );

            System.out.println("Database initialized or already exists.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
