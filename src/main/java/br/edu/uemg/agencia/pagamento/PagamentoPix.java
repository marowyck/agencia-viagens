package br.edu.uemg.agencia.pagamento;

import java.time.LocalDateTime;

public class PagamentoPix implements Pagamento {
    private LocalDateTime dataPagamento;

    @Override
    public boolean processarPagamento(double valor) {
        this.dataPagamento = LocalDateTime.now();
        return true;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }
}
