package br.edu.uemg.agencia.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LogRepo {

    public static class LogEntry {
        public int id;
        public String usuario, acao, tela, tipo, ip, data;

        public LogEntry(int id, String u, String a, String t, String tp, String ip, String d) {
            this.id = id; this.usuario = u; this.acao = a; this.tela = t; this.tipo = tp; this.ip = ip; this.data = d;
        }
    }

    public List<LogEntry> findAll() {
        List<LogEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM log_operacoes ORDER BY id DESC LIMIT 500";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LogEntry(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("acao"),
                        rs.getString("tela"),
                        rs.getString("tipo"),
                        rs.getString("ip_local"),
                        rs.getString("data_hora")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}