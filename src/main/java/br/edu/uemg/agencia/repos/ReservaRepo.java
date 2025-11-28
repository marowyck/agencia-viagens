package br.edu.uemg.agencia.repos;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaRepo {

    public Reserva insert(Reserva reserva) {
        String sql = "INSERT INTO reserva(cliente_id, pacote_id, status, data_reserva, valor_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reserva.getCliente().getId());
            ps.setInt(2, reserva.getPacote().getId());
            ps.setString(3, reserva.getStatus());
            ps.setString(4, reserva.getDataReserva().toString());
            ps.setDouble(5, reserva.getValorTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) reserva.setId(rs.getInt(1));
            }
            return reserva;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir reserva: " + e.getMessage(), e);
        }
    }

    public void updateValorTotal(int reservaId, double novoValor) {
        String sql = "UPDATE reserva SET valor_total = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, novoValor);
            ps.setInt(2, reservaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar valor da reserva: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM reserva WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar reserva: " + e.getMessage(), e);
        }
    }

    public Optional<Reserva> findById(int id) {
        String sql = "SELECT * FROM reserva WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Reserva> findAll() {
        String sql = "SELECT * FROM reserva ORDER BY data_reserva DESC";
        List<Reserva> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas: " + e.getMessage(), e);
        }
        return list;
    }

    private Reserva mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int clienteId = rs.getInt("cliente_id");
        int pacoteId = rs.getInt("pacote_id");
        String status = rs.getString("status");
        String dataStr = rs.getString("data_reserva");
        double valorTotal = rs.getDouble("valor_total");

        Cliente cliente = null;
        Pacote pacote = null;
        try {
            ClienteRepo cr = new ClienteRepo();
            cliente = cr.findById(clienteId).orElse(null);
        } catch (Exception ignored) {
        }

        try {
            PacoteRepo pr = new PacoteRepo();
            pacote = pr.findById(pacoteId).orElse(null);
        } catch (Exception ignored) {
        }

        Reserva r = new Reserva();
        r.setId(id);
        r.setCliente(cliente);
        r.setPacote(pacote);
        r.setStatus(status);
        try {
            r.setDataReserva(LocalDateTime.parse(dataStr));
        } catch (Exception ex) {
            r.setDataReserva(LocalDateTime.now());
        }
        r.setValorTotal(valorTotal);
        return r;
    }

    public void insertPagamentoAndConfirm(int reservaId, String metodo, double taxa, LocalDateTime dataPagamento, double novoValorTotal) {
        String sqlInsert = "INSERT INTO pagamento(reserva_id, metodo, taxa, data_pagamento, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String sqlUpdateReserva = "UPDATE reserva SET status = 'Confirmada', valor_total = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsert)) {
                ps1.setInt(1, reservaId);
                ps1.setString(2, metodo);
                ps1.setDouble(3, taxa);
                ps1.setString(4, dataPagamento.toString());
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateReserva)) {
                ps2.setDouble(1, novoValorTotal);
                ps2.setInt(2, reservaId);
                ps2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {
            }
            throw new RuntimeException("Erro ao processar pagamento/confirmar: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                conn.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void updateStatus(Integer id, String novoStatus) {
        String sql = "UPDATE reserva SET status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Erro: Nenhuma reserva encontrada com o ID " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro de Banco ao atualizar status: " + e.getMessage());
        }
    }
}
