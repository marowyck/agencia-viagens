package br.edu.uemg.agencia.modelo;

public abstract class Pacote {
    protected Integer id;
    protected String destino;
    protected int duracao;
    protected double valorBase;

    public Pacote() {
    }

    public Pacote(Integer id, String destino, int duracao, double valorBase) {
        this.id = id;
        this.destino = destino;
        this.duracao = duracao;
        this.valorBase = valorBase;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public double getValorBase() {
        return valorBase;
    }

    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    public abstract double calcularValorFinal();
}
