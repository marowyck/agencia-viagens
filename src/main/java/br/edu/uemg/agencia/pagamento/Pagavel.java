package br.edu.uemg.agencia.pagamento;

public interface Pagavel {
    double calcularValorFinal(double valorBase);

    String processar(double valor);
}