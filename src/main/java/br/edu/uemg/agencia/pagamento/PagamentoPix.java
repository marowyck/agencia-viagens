package br.edu.uemg.agencia.pagamento;

public class PagamentoPix implements Pagavel {
    @Override
    public double calcularValorFinal(double valorBase) {
        return valorBase * 0.95;
    }

    @Override
    public String processar(double valor) {
        return String.format("Gerando código PIX para R$ %.2f...", valor);
    }
}