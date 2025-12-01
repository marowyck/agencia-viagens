package br.edu.uemg.agencia.repos;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.util.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepo {

    public Cliente insert(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo");
        if (!Validator.isValidCPF(c.getCpf())) throw new IllegalArgumentException("CPF inválido");

        String sql = "INSERT INTO cliente(nome, cpf, email, telefone, cep, logradouro, bairro, cidade, uf, pontos_fidelidade) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelefone());
            ps.setString(5, c.getCep());
            ps.setString(6, c.getLogradouro());
            ps.setString(7, c.getBairro());
            ps.setString(8, c.getCidade());
            ps.setString(9, c.getUf());
            ps.setInt(10, c.getPontosFidelidade());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }

    public void update(Cliente c) {
        if (c == null || c.getId() == null) throw new IllegalArgumentException("Cliente inválido");
        if (!Validator.isValidCPF(c.getCpf())) throw new IllegalArgumentException("CPF inválido");

        String sql = "UPDATE cliente SET nome=?, cpf=?, email=?, telefone=?, cep=?, logradouro=?, bairro=?, cidade=?, uf=?, pontos_fidelidade=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelefone());
            ps.setString(5, c.getCep());
            ps.setString(6, c.getLogradouro());
            ps.setString(7, c.getBairro());
            ps.setString(8, c.getCidade());
            ps.setString(9, c.getUf());
            ps.setInt(10, c.getPontosFidelidade());
            ps.setInt(11, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void adicionarPontos(int clienteId, int pontosExtras) {
        String sql = "UPDATE cliente SET pontos_fidelidade = pontos_fidelidade + ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pontosExtras);
            ps.setInt(2, clienteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public Optional<Cliente> findById(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Cliente> findAll() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
    }

    public List<Cliente> searchByName(String query) {
        List<Cliente> list = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE LOWER(nome) LIKE ? LIMIT 5";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query.toLowerCase() + "%");
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reserva> findHistorico(int clienteId) {
        List<Reserva> list = new ArrayList<>();
        String sql = "SELECT id FROM reserva WHERE cliente_id = ? ORDER BY data_reserva DESC";
        ReservaRepo reservaRepo = new ReservaRepo();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservaRepo.findById(rs.getInt("id")).ifPresent(list::add);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("email"),
                rs.getString("telefone")
        );
        c.setCep(rs.getString("cep"));
        c.setLogradouro(rs.getString("logradouro"));
        c.setBairro(rs.getString("bairro"));
        c.setCidade(rs.getString("cidade"));
        c.setUf(rs.getString("uf"));
        c.setPontosFidelidade(rs.getInt("pontos_fidelidade"));
        return c;
    }
}