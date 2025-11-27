package br.edu.uemg.agencia.servico;

import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.modelo.PacoteNacional;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.pagamento.Pagamento;
import br.edu.uemg.agencia.pagamento.PagamentoCartao;
import br.edu.uemg.agencia.pagamento.PagamentoPix;
import br.edu.uemg.agencia.repos.ReservaRepo;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaService {

    private final ReservaRepo repo = new ReservaRepo();
    private static final double TAXA_CARTAO = 0.025;

    public double simularValorFinal(Pacote pacote, boolean pagamentoCartao) {
        if (pacote == null) return 0.0;
        double base = pacote.calcularValorFinal();
        if (pagamentoCartao) base = base * (1 + TAXA_CARTAO);
        return round(base);
    }

    public Reserva criarReserva(Reserva reserva) {
        if (reserva.getCliente() == null) throw new IllegalArgumentException("Reserva precisa de cliente.");
        if (reserva.getPacote() == null) throw new IllegalArgumentException("Reserva precisa de pacote.");

        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatus("Pendente");
        double valor = reserva.getPacote().calcularValorFinal();
        reserva.setValorTotal(round(valor));

        return repo.insert(reserva);
    }

    public void confirmarPagamento(Reserva reserva, String metodo) {
        if (reserva == null || reserva.getId() == null) throw new IllegalArgumentException("Reserva inválida.");
        double taxa = 0.0;
        double valorParaProcessar = reserva.getValorTotal();

        if ("cartao".equalsIgnoreCase(metodo)) {
            PagamentoCartao pc = new PagamentoCartao();
            boolean ok = pc.processarPagamento(valorParaProcessar);
            if (!ok) throw new RuntimeException("Pagamento por cartão falhou.");
            taxa = TAXA_CARTAO * valorParaProcessar;
            double novoValor = round(valorParaProcessar * (1 + TAXA_CARTAO));
            reserva.setValorTotal(novoValor);
        } else {
            PagamentoPix pp = new PagamentoPix();
            boolean ok = pp.processarPagamento(valorParaProcessar);
            if (!ok) throw new RuntimeException("Pagamento PIX falhou.");
            taxa = 0.0;
        }

        int pagamentoId = repo.insertPagamento(reserva.getId(), metodo.toLowerCase(), taxa, LocalDateTime.now());
        repo.updateStatus(reserva.getId(), "Confirmada");
        updateValorTotal(reserva.getId(), reserva.getValorTotal());
    }

    private void updateValorTotal(int reservaId, double novoValor) {
        String sql = "UPDATE reserva SET valor_total = ? WHERE id = ?";
        try (var conn = br.edu.uemg.agencia.repos.ConnectionFactory.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, novoValor);
            ps.setInt(2, reservaId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar valor da reserva: " + e.getMessage(), e);
        }
    }

    public void cancelarReserva(Reserva reserva) {
        if (reserva == null || reserva.getId() == null) throw new IllegalArgumentException("Reserva inválida.");
        repo.updateStatus(reserva.getId(), "Cancelada");
    }

    public List<Reserva> listarTodas() {
        return repo.findAll();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
