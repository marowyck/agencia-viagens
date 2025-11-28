package br.edu.uemg.agencia.modelo;

public class PacoteInternacional extends Pacote {
    private String moeda;
    private double taxaCambio;
    private double taxaEmbarque = 250.0;

    public PacoteInternacional() {
    }

    public PacoteInternacional(Integer id, String destino, int duracao, double valorBase, String moeda, double taxaCambio) {
        super(id, destino, duracao, valorBase);
        this.moeda = moeda;
        this.taxaCambio = taxaCambio;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public double getTaxaCambio() {
        return taxaCambio;
    }

    public void setTaxaCambio(double taxaCambio) {
        this.taxaCambio = taxaCambio;
    }

    public double getTaxaEmbarque() {
        return taxaEmbarque;
    }

    public void setTaxaEmbarque(double taxaEmbarque) {
        this.taxaEmbarque = taxaEmbarque;
    }

    @Override
    public double calcularValorFinal() {
        double valorConvertido = valorBase * taxaCambio;
        return valorConvertido + taxaEmbarque;
    }

    @Override
    public String toString() {
        return destino;
    }
}
