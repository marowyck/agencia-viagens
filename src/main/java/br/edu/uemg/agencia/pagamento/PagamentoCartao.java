package br.edu.uemg.agencia.pagamento;

public class PagamentoCartao implements Pagavel {
    @Override
    public double calcularValorFinal(double valorBase) {
        return valorBase * 1.025;
    }

    @Override
    public String processar(double valor) {
        return String.format("Conectando operadora de cartão. Valor: R$ %.2f...", valor);
    }
}