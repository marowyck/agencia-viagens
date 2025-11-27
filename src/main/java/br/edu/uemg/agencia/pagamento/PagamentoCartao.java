package br.edu.uemg.agencia.pagamento;

import java.time.LocalDateTime;

public class PagamentoCartao implements Pagamento {
    private LocalDateTime dataPagamento;
    private static final double TAXA = 0.025;

    @Override
    public boolean processarPagamento(double valor) {
        this.dataPagamento = LocalDateTime.now();
        return true;
    }

    public double calcularValorComTaxa(double valor) {
        return valor * (1 + TAXA);
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }
}
