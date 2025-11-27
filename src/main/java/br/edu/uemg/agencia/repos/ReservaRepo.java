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
                if (rs.next()) {
                    reserva.setId(rs.getInt(1));
                }
            }
            return reserva;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir reserva: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int reservaId, String novoStatus) {
        String sql = "UPDATE reserva SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoStatus);
            ps.setInt(2, reservaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da reserva: " + e.getMessage(), e);
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
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
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

            while (rs.next()) {
                list.add(mapRow(rs));
            }
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
            Optional<Cliente> oc = cr.findById(clienteId);
            if (oc.isPresent()) cliente = oc.get();
        } catch (Exception ignored) {
        }

        try {
            PacoteRepo pr = new PacoteRepo();
            Optional<Pacote> op = pr.findById(pacoteId);
            if (op.isPresent()) pacote = op.get();
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

    public int insertPagamento(int reservaId, String metodo, double taxa, LocalDateTime dataPagamento) {
        String sql = "INSERT INTO pagamento(reserva_id, metodo, taxa, data_pagamento) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservaId);
            ps.setString(2, metodo);
            ps.setDouble(3, taxa);
            ps.setString(4, dataPagamento.toString());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pagamento: " + e.getMessage(), e);
        }
    }
}
