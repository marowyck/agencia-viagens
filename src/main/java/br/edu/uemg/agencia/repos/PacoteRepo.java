package br.edu.uemg.agencia.repos;

import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.modelo.PacoteNacional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacoteRepo {

    public Pacote insert(Pacote p) {
        String sql = "INSERT INTO pacote(tipo, destino, duracao, valor_base, imposto_turismo, moeda, taxa_cambio, taxa_embarque) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p instanceof PacoteNacional ? "nacional" : "internacional");
            ps.setString(2, p.getDestino());
            ps.setInt(3, p.getDuracao());
            ps.setDouble(4, p.getValorBase());
            if (p instanceof PacoteNacional) {
                ps.setDouble(5, ((PacoteNacional) p).getImpostoTurismo());
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.REAL);
                ps.setNull(8, Types.REAL);
            } else if (p instanceof PacoteInternacional) {
                PacoteInternacional pi = (PacoteInternacional) p;
                ps.setNull(5, Types.REAL);
                ps.setString(6, pi.getMoeda());
                ps.setDouble(7, pi.getTaxaCambio());
                ps.setDouble(8, pi.getTaxaEmbarque());
            } else {
                ps.setNull(5, Types.REAL);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.REAL);
                ps.setNull(8, Types.REAL);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pacote: " + e.getMessage(), e);
        }
    }

    public void update(Pacote p) {
        String sql = "UPDATE pacote SET tipo=?, destino=?, duracao=?, valor_base=?, imposto_turismo=?, moeda=?, taxa_cambio=?, taxa_embarque=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p instanceof PacoteNacional ? "nacional" : "internacional");
            ps.setString(2, p.getDestino());
            ps.setInt(3, p.getDuracao());
            ps.setDouble(4, p.getValorBase());
            if (p instanceof PacoteNacional) {
                ps.setDouble(5, ((PacoteNacional) p).getImpostoTurismo());
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.REAL);
                ps.setNull(8, Types.REAL);
            } else {
                PacoteInternacional pi = (PacoteInternacional) p;
                ps.setNull(5, Types.REAL);
                ps.setString(6, pi.getMoeda());
                ps.setDouble(7, pi.getTaxaCambio());
                ps.setDouble(8, pi.getTaxaEmbarque());
            }
            ps.setInt(9, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pacote: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM pacote WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir pacote: " + e.getMessage(), e);
        }
    }

    public Optional<Pacote> findById(int id) {
        String sql = "SELECT * FROM pacote WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacote: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Pacote> findAll() {
        String sql = "SELECT * FROM pacote ORDER BY destino";
        List<Pacote> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pacotes: " + e.getMessage(), e);
        }
        return list;
    }

    private Pacote mapRow(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        if ("nacional".equalsIgnoreCase(tipo)) {
            PacoteNacional pn = new PacoteNacional(
                    rs.getInt("id"),
                    rs.getString("destino"),
                    rs.getInt("duracao"),
                    rs.getDouble("valor_base")
            );
            double imposto = rs.getDouble("imposto_turismo");
            pn.setImpostoTurismo(imposto);
            return pn;
        } else {
            PacoteInternacional pi = new PacoteInternacional(
                    rs.getInt("id"),
                    rs.getString("destino"),
                    rs.getInt("duracao"),
                    rs.getDouble("valor_base"),
                    rs.getString("moeda"),
                    rs.getDouble("taxa_cambio")
            );
            pi.setTaxaEmbarque(rs.getDouble("taxa_embarque"));
            return pi;
        }
    }
}
