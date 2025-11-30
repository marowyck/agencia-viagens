package br.edu.uemg.agencia.repos;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("PRAGMA foreign_keys = ON;");

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS cliente (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT NOT NULL," +
                            "cpf TEXT UNIQUE NOT NULL," +
                            "email TEXT," +
                            "telefone TEXT," +
                            "cep TEXT," +
                            "logradouro TEXT," +
                            "bairro TEXT," +
                            "cidade TEXT," +
                            "uf TEXT," +
                            "pontos_fidelidade INTEGER DEFAULT 0," +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "updated_at TEXT" +
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
                            "taxa_embarque REAL," +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "updated_at TEXT" +
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
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "updated_at TEXT," +
                            "FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT," +
                            "FOREIGN KEY (pacote_id) REFERENCES pacote(id) ON DELETE RESTRICT" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pagamento (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "reserva_id INTEGER UNIQUE NOT NULL," +
                            "metodo TEXT CHECK(metodo IN ('pix','cartao')) NOT NULL," +
                            "taxa REAL," +
                            "data_pagamento TEXT NOT NULL," +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (reserva_id) REFERENCES reserva(id) ON DELETE CASCADE" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS favorito (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "cliente_id INTEGER NOT NULL," +
                            "pacote_id INTEGER NOT NULL," +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE," +
                            "FOREIGN KEY (pacote_id) REFERENCES pacote(id) ON DELETE CASCADE" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS log_operacoes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "usuario TEXT," +
                            "acao TEXT," +
                            "tela TEXT," +
                            "tipo TEXT," +
                            "ip_local TEXT," +
                            "data_hora TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                            ");"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuario (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT UNIQUE NOT NULL," +
                            "password_hash TEXT NOT NULL," +
                            "perfil TEXT CHECK(perfil IN ('admin','atendente')) NOT NULL," +
                            "nome TEXT," +
                            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "updated_at TEXT" +
                            ");"
            );

            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_reserva_cliente ON reserva(cliente_id);");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_reserva_pacote ON reserva(pacote_id);");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_pagamento_reserva ON pagamento(reserva_id);");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON cliente(cpf);");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_usuario_username ON usuario(username);");

            String adminHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
            String atendenteHash = "e94e143d3a999c2004bed70fdc93ae37470fb3c3c5cd328fa20fbd053e65c4f9";

            st.executeUpdate(
                    "INSERT INTO usuario(username, password_hash, perfil, nome) VALUES " +
                            "('admin', '" + adminHash + "', 'admin', 'Administrador') " +
                            "ON CONFLICT(username) DO UPDATE SET " +
                            "password_hash = excluded.password_hash, perfil = excluded.perfil, nome = excluded.nome;"
            );

            st.executeUpdate(
                    "INSERT INTO usuario(username, password_hash, perfil, nome) VALUES " +
                            "('atendente', '" + atendenteHash + "', 'atendente', 'Atendente Padrão') " +
                            "ON CONFLICT(username) DO UPDATE SET " +
                            "password_hash = excluded.password_hash, perfil = excluded.perfil, nome = excluded.nome;"
            );

            System.out.println("✔ Banco atualizado com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}