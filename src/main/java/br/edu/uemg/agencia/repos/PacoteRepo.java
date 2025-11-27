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
        String sql = "INSERT INTO pacote(tipo, destino, duracao, valor_base, imposto_turismo, moeda, taxa_cambio, taxa_embarque) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindInsert(ps, p);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (p instanceof PacoteNacional) {
                        ((PacoteNacional) p).setId(id);
                    } else if (p instanceof PacoteInternacional) {
                        ((PacoteInternacional) p).setId(id);
                    } else {
                    }
                }
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pacote: " + e.getMessage(), e);
        }
    }

    public void update(Pacote p) {
        String sql = "UPDATE pacote SET tipo = ?, destino = ?, duracao = ?, valor_base = ?, imposto_turismo = ?, moeda = ?, taxa_cambio = ?, taxa_embarque = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindInsert(ps, p);
            Integer id = p.getId();
            if (id == null) throw new RuntimeException("Pacote sem id para update");
            ps.setInt(9, id);
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
            throw new RuntimeException("Erro ao deletar pacote: " + e.getMessage(), e);
        }
    }

    public Optional<Pacote> findById(int id) {
        String sql = "SELECT * FROM pacote WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacote por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Pacote> findAll() {
        String sql = "SELECT * FROM pacote ORDER BY destino";
        List<Pacote> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacotes: " + e.getMessage(), e);
        }
        return list;
    }

    private void bindInsert(PreparedStatement ps, Pacote p) throws SQLException {
        if (p instanceof PacoteNacional) {
            ps.setString(1, "nacional");
            ps.setString(2, p.getDestino());
            ps.setInt(3, p.getDuracao());
            ps.setDouble(4, p.getValorBase());
            ps.setDouble(5, ((PacoteNacional) p).getImpostoTurismo());
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.REAL);
            ps.setNull(8, Types.REAL);
        } else if (p instanceof PacoteInternacional) {
            PacoteInternacional pi = (PacoteInternacional) p;
            ps.setString(1, "internacional");
            ps.setString(2, p.getDestino());
            ps.setInt(3, p.getDuracao());
            ps.setDouble(4, p.getValorBase());
            ps.setNull(5, Types.REAL);
            ps.setString(6, pi.getMoeda());
            ps.setDouble(7, pi.getTaxaCambio());
            ps.setDouble(8, pi.getTaxaEmbarque());
        } else {
            ps.setString(1, "nacional");
            ps.setString(2, p.getDestino());
            ps.setInt(3, p.getDuracao());
            ps.setDouble(4, p.getValorBase());
            ps.setNull(5, Types.REAL);
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.REAL);
            ps.setNull(8, Types.REAL);
        }
    }

    private Pacote mapRow(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        int id = rs.getInt("id");
        String destino = rs.getString("destino");
        int duracao = rs.getInt("duracao");
        double valorBase = rs.getDouble("valor_base");

        if ("nacional".equalsIgnoreCase(tipo)) {
            PacoteNacional pn = new PacoteNacional(id, destino, duracao, valorBase);
            double imposto = rs.getDouble("imposto_turismo");
            if (!rs.wasNull()) {
                try {
                    pn.setImpostoTurismo(imposto);
                } catch (Exception ignored) {
                }
            }
            return pn;
        } else {
            String moeda = rs.getString("moeda");
            double taxaCambio = rs.getDouble("taxa_cambio");
            double taxaEmbarque = rs.getDouble("taxa_embarque");
            PacoteInternacional pi = new PacoteInternacional(id, destino, duracao, valorBase, moeda, taxaCambio);
            try {
                pi.setTaxaEmbarque(taxaEmbarque);
            } catch (Exception ignored) {
            }
            return pi;
        }
    }
}
