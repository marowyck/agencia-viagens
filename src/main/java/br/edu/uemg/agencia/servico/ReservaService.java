package br.edu.uemg.agencia.servico;

import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.log.LogService;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.repos.ReservaRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.pagamento.Pagavel;
import br.edu.uemg.agencia.pagamento.PagamentoFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservaService {

    private final ReservaRepo repo = new ReservaRepo();
    private final PacoteRepo pacoteRepo = new PacoteRepo();

    public double simularValorFinal(Pacote pacote, boolean pagamentoCartao) {
        if (pacote == null) return 0.0;

        double valorBase = pacote.calcularValorFinal();

        String metodo = pagamentoCartao ? "CARTAO" : "PIX";
        Pagavel estrategia = PagamentoFactory.criar(metodo);

        return round(estrategia.calcularValorFinal(valorBase));
    }

    public Reserva criarReserva(Reserva reserva) {
        if (reserva.getCliente() == null) throw new IllegalArgumentException("Reserva precisa de cliente.");
        if (reserva.getPacote() == null) throw new IllegalArgumentException("Reserva precisa de pacote.");

        Optional<Pacote> op = pacoteRepo.findById(reserva.getPacote().getId());
        if (op.isEmpty()) throw new IllegalArgumentException("Pacote não encontrado.");

        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatus("Pendente");

        double valor = op.get().calcularValorFinal();
        reserva.setValorTotal(round(valor));

        Reserva criada = repo.insert(reserva);

        LogService.save(
                Sessao.getUsuarioNome(),
                "Criou reserva ID " + criada.getId(),
                "ReservaService",
                "CREATE",
                "localhost"
        );
        return criada;
    }

    public void confirmarPagamento(Reserva reserva, String metodo) {
        if (reserva == null || reserva.getId() == null)
            throw new IllegalArgumentException("Reserva inválida.");
        if ("Confirmada".equalsIgnoreCase(reserva.getStatus()))
            throw new IllegalStateException("Reserva já está confirmada.");
        if ("Cancelada".equalsIgnoreCase(reserva.getStatus()))
            throw new IllegalStateException("Reserva está cancelada.");

        Pagavel estrategia = PagamentoFactory.criar(metodo);

        double valorOriginal = reserva.getValorTotal();
        double valorFinal = estrategia.calcularValorFinal(valorOriginal);
        double taxaOuDesconto = valorFinal - valorOriginal;

        String logMensagem = estrategia.processar(valorFinal);

        repo.insertPagamentoAndConfirm(
                reserva.getId(),
                metodo.toLowerCase(),
                taxaOuDesconto,
                LocalDateTime.now(),
                valorFinal
        );

        LogService.save(
                Sessao.getUsuarioNome(),
                "Pagamento Confirmado: " + logMensagem,
                "ReservaService",
                "UPDATE",
                "localhost"
        );
    }

    public void cancelarReserva(Reserva reserva) {
        if (reserva == null || reserva.getId() == null)
            throw new IllegalArgumentException("Reserva inválida.");

        if ("Confirmada".equalsIgnoreCase(reserva.getStatus()))
            throw new IllegalStateException("Não é permitido cancelar reserva já paga (Confirmada).");

        if ("Cancelada".equalsIgnoreCase(reserva.getStatus()))
            return;

        repo.updateStatus(reserva.getId(), "Cancelada");

        reserva.setStatus("Cancelada");

        br.edu.uemg.agencia.log.LogService.save(
                br.edu.uemg.agencia.auth.Sessao.getUsuarioNome(),
                "Cancelou reserva ID " + reserva.getId(),
                "ReservaService",
                "UPDATE",
                "localhost"
        );
    }

    public List<Reserva> listarTodas() { return repo.findAll(); }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}