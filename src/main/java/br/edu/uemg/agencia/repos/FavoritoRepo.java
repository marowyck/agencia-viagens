package br.edu.uemg.agencia.repos;

import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.modelo.PacoteNacional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FavoritoRepo {

    public void adicionar(int clienteId, int pacoteId) {
        String sql = "INSERT INTO favorito(cliente_id, pacote_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, pacoteId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Info: Pacote já favoritado ou erro de banco.");
        }
    }

    public void remover(int clienteId, int pacoteId) {
        String sql = "DELETE FROM favorito WHERE cliente_id = ? AND pacote_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, pacoteId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pacote> listarPorCliente(int clienteId) {
        List<Pacote> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, 
                   (CASE WHEN p.tipo = 'nacional' THEN pn.imposto_turismo ELSE NULL END) as imposto_turismo,
                   (CASE WHEN p.tipo = 'internacional' THEN pi.moeda ELSE NULL END) as moeda,
                   (CASE WHEN p.tipo = 'internacional' THEN pi.taxa_cambio ELSE NULL END) as taxa_cambio,
                   (CASE WHEN p.tipo = 'internacional' THEN pi.taxa_embarque ELSE NULL END) as taxa_embarque
            FROM favorito f
            JOIN pacote p ON p.id = f.pacote_id
            -- Left Join simulado para SQLite se as tabelas fossem separadas, 
            -- mas como usamos Single Table, o SELECT * FROM pacote já traz tudo.
            WHERE f.cliente_id = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT pacote_id FROM favorito WHERE cliente_id = ?")) {
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();
            PacoteRepo pr = new PacoteRepo();
            while (rs.next()) {
                pr.findById(rs.getInt("pacote_id")).ifPresent(lista::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}