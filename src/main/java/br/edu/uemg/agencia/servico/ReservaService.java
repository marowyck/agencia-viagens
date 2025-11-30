package br.edu.uemg.agencia.servico;

import br.edu.uemg.agencia.auth.Sessao;
import br.edu.uemg.agencia.log.LogService;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.pagamento.PagamentoFactory;
import br.edu.uemg.agencia.pagamento.Pagavel;
import br.edu.uemg.agencia.repos.ClienteRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.repos.ReservaRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public class ReservaService {

    private final ReservaRepo repo = new ReservaRepo();
    private final PacoteRepo pacoteRepo = new PacoteRepo();

    public double calcularSazonalidade(double valorBase, LocalDate dataViagem) {
        if (dataViagem == null) return valorBase;

        Month mes = dataViagem.getMonth();
        double fator = 1.0;

        if (mes == Month.DECEMBER || mes == Month.JANUARY || mes == Month.JULY) {
            fator += 0.20;
        }

        if (dataViagem.isBefore(LocalDate.now().plusDays(7))) {
            fator += 0.10;
        }

        return valorBase * fator;
    }

    public double simularValorFinal(Pacote pacote, boolean pagamentoCartao, LocalDate dataViagem) {
        if (pacote == null) return 0.0;
        double valorBase = pacote.calcularValorFinal();
        double valorComSazonalidade = calcularSazonalidade(valorBase, dataViagem);
        String metodo = pagamentoCartao ? "CARTAO" : "PIX";
        Pagavel estrategia = PagamentoFactory.criar(metodo);
        return round(estrategia.calcularValorFinal(valorComSazonalidade));
    }

    public Reserva criarReserva(Reserva reserva) {
        if (reserva.getCliente() == null) throw new IllegalArgumentException("Cliente obrigatório.");
        if (reserva.getPacote() == null) throw new IllegalArgumentException("Pacote obrigatório.");

        Optional<Pacote> op = pacoteRepo.findById(reserva.getPacote().getId());
        if (op.isEmpty()) throw new IllegalArgumentException("Pacote não encontrado.");

        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatus("Pendente");

        if (reserva.getValorTotal() == 0.0) {
            reserva.setValorTotal(round(op.get().calcularValorFinal()));
        }

        Reserva criada = repo.insert(reserva);
        LogService.save(Sessao.getUsuarioNome(), "Criou reserva " + criada.getId(), "ReservaService", "CREATE", "localhost");
        return criada;
    }

    public void confirmarPagamento(Reserva reserva, String metodo) {
        if (reserva == null || "Confirmada".equalsIgnoreCase(reserva.getStatus()))
            throw new IllegalStateException("Reserva já processada.");

        Pagavel estrategia = PagamentoFactory.criar(metodo);
        double valorFinal = estrategia.calcularValorFinal(reserva.getValorTotal());
        double taxa = valorFinal - reserva.getValorTotal();

        repo.insertPagamentoAndConfirm(
                reserva.getId(), metodo.toLowerCase(), taxa, LocalDateTime.now(), valorFinal
        );

        int pontosGanhos = (int) (valorFinal / 10.0);
        if (pontosGanhos > 0) {
            new ClienteRepo().adicionarPontos(reserva.getCliente().getId(), pontosGanhos);
        }
    }

    public void cancelarReserva(Reserva reserva) {
        if (reserva == null || "Confirmada".equalsIgnoreCase(reserva.getStatus())) return;
        repo.updateStatus(reserva.getId(), "Cancelada");
        reserva.setStatus("Cancelada");
    }

    public List<Reserva> listarTodas() { return repo.findAll(); }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}