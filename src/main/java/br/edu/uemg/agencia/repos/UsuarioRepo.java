package br.edu.uemg.agencia.repos;

import br.edu.uemg.agencia.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UsuarioRepo {

    public Usuario login(String username, String passwordHash) throws Exception {
        String sql = "SELECT * FROM usuario WHERE username = ? AND password_hash = ? LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    public boolean authenticate(String username, String passwordHash) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ? AND password_hash = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Usuario> findByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ? LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private Usuario map(ResultSet rs) throws Exception {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("perfil"),
                rs.getString("nome")
        );
    }
}
