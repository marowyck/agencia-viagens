package br.edu.uemg.agencia.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class RelatorioRepo {

    public double getTotalArrecadado() {
        String sql = """
                    SELECT SUM(valor_total) AS total
                    FROM reserva
                    WHERE status = 'Confirmada'
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble("total") : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular total arrecadado: " + e.getMessage());
        }
    }

    public int getTotalReservas() {
        String sql = "SELECT COUNT(*) AS total FROM reserva";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar total reservas: " + e.getMessage());
        }
    }

    public Map<String, Integer> getTotalPorStatus() {
        String sql = """
                    SELECT status, COUNT(*) AS qtd
                    FROM reserva
                    GROUP BY status
                """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("qtd"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar reservas por status: " + e.getMessage());
        }
        return map;
    }

    public Map<String, Integer> getRankingDestinos() {
        String sql = """
                    SELECT p.destino, COUNT(r.id) AS qtd
                    FROM reserva r
                    JOIN pacote p ON p.id = r.pacote_id
                    GROUP BY r.pacote_id
                    ORDER BY qtd DESC
                """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("destino"), rs.getInt("qtd"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro no ranking destinos: " + e.getMessage());
        }
        return map;
    }
}
