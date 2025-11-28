package br.edu.uemg.agencia.export;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.repos.ClienteRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.repos.ReservaRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.List;

public class ExportService {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static File exportClientes(File destino) throws Exception {
        List<Cliente> clientes = new ClienteRepo().findAll();
        MAPPER.writeValue(destino, clientes);
        return destino;
    }

    public static File exportPacotes(File destino) throws Exception {
        List<Pacote> pacotes = new PacoteRepo().findAll();
        MAPPER.writeValue(destino, pacotes);
        return destino;
    }

    public static File exportReservas(File destino) throws Exception {
        List<Reserva> reservas = new ReservaRepo().findAll();
        MAPPER.writeValue(destino, reservas);
        return destino;
    }
}
