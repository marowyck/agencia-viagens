package br.edu.uemg.agencia.log;

import br.edu.uemg.agencia.repos.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class LogService {

    public static void save(String usuario, String acao, String tela, String tipo, String ipLocal) {
        String sql = "INSERT INTO log_operacoes(usuario, acao, tela, tipo, ip_local, data_hora) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, acao);
            ps.setString(3, tela);
            ps.setString(4, tipo);
            ps.setString(5, ipLocal);
            ps.setString(6, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Falha ao gravar log: " + e.getMessage());
        }
    }
}
