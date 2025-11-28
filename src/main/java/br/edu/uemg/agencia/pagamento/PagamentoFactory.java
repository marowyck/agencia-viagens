package br.edu.uemg.agencia.pagamento;

public class PagamentoFactory {

    public static Pagavel criar(String tipo) {
        if (tipo == null) return null;

        switch (tipo.toUpperCase()) {
            case "PIX":
                return new PagamentoPix();
            case "CARTAO":
            case "CARTÃO":
                return new PagamentoCartao();
            default:
                throw new IllegalArgumentException("Método de pagamento desconhecido: " + tipo);
        }
    }
}