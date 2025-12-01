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
import java.time.temporal.ChronoUnit;
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

    public double simularValorFinal(Pacote pacote, boolean isCartao, int parcelas, LocalDate dataViagem) {
        if (pacote == null) return 0.0;
        double valorBase = pacote.calcularValorFinal();
        double valorComSazonalidade = calcularSazonalidade(valorBase, dataViagem);
        double valorFinal = valorComSazonalidade;

        if (!isCartao) {
            valorFinal = valorFinal * 0.95;
        } else {
            valorFinal = valorFinal * 1.025;
            if (parcelas > 3) {
                double taxaJuros = 0.0199;
                valorFinal = valorFinal * Math.pow((1 + taxaJuros), parcelas);
            }
        }
        return round(valorFinal);
    }

    public Reserva criarReserva(Reserva reserva) {
        if (reserva.getCliente() == null) throw new IllegalArgumentException("Cliente obrigatório.");
        if (reserva.getPacote() == null) throw new IllegalArgumentException("Pacote obrigatório.");

        Optional<Pacote> op = pacoteRepo.findById(reserva.getPacote().getId());
        if (op.isEmpty()) throw new IllegalArgumentException("Pacote não encontrado.");

        if (reserva.getDataReserva() == null) {
            reserva.setDataReserva(LocalDateTime.now());
        }

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

        String tipoFactory = metodo.toLowerCase().contains("pix") ? "PIX" : "CARTAO";
        Pagavel estrategia = PagamentoFactory.criar(tipoFactory);

        double valorFinal = reserva.getValorTotal();
        double taxa = 0.0;

        repo.insertPagamentoAndConfirm(
                reserva.getId(), metodo, taxa, LocalDateTime.now(), valorFinal
        );

        int pontosGanhos = (int) (valorFinal / 10.0);
        if (pontosGanhos > 0) {
            new ClienteRepo().adicionarPontos(reserva.getCliente().getId(), pontosGanhos);
        }
    }

    public String cancelarReserva(Reserva reserva) {
        if (reserva == null) throw new IllegalArgumentException("Inválido");
        if ("Cancelada".equalsIgnoreCase(reserva.getStatus())) return "Já cancelada.";

        String msg = "Cancelamento realizado.";

        if ("Confirmada".equalsIgnoreCase(reserva.getStatus())) {
            LocalDate dataViagem = reserva.getDataReserva().toLocalDate();
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), dataViagem);

            double reembolso = reserva.getValorTotal();

            if (dias < 0) {
                return "Erro: Não é possível cancelar uma viagem que já aconteceu (" + dias + " dias atrás).";
            } else if (dias <= 7) {
                double multa = reembolso * 0.20;
                reembolso -= multa;
                msg = String.format("Cancelado com MULTA (Faltam %d dias).\nReembolso: R$ %.2f", dias, reembolso);
            } else {
                msg = String.format("Cancelamento Gratuito (Faltam %d dias).\nReembolso: R$ %.2f", dias, reembolso);
            }
            LogService.save(Sessao.getUsuarioNome(), "Estorno #" + reserva.getId(), "ReservaService", "REFUND", "localhost");
        }
        repo.updateStatus(reserva.getId(), "Cancelada");
        reserva.setStatus("Cancelada");
        return msg;
    }

    public String reagendarReserva(int reservaId, LocalDate novaData) {
        Optional<Reserva> op = repo.findById(reservaId);
        if (op.isEmpty()) throw new IllegalArgumentException("404 Reserva");
        Reserva r = op.get();

        double base = r.getPacote().calcularValorFinal();
        double novoValor = round(calcularSazonalidade(base, novaData));

        repo.updateDataEValor(reservaId, novaData.atStartOfDay(), novoValor);
        return "Reagendado para " + novaData + "\nNovo Valor: R$ " + novoValor;
    }

    public void verificarExpiracao() {
        List<Reserva> todas = repo.findAll();
        for(Reserva r : todas) {
            if("Pendente".equalsIgnoreCase(r.getStatus()) && r.getDataReserva().isBefore(LocalDateTime.now().minusDays(5))) {
                repo.updateStatus(r.getId(), "Expirada");
            }
        }
    }

    public List<Reserva> listarTodas() { return repo.findAll(); }
    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}