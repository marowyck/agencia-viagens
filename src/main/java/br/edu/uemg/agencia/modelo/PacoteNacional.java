package br.edu.uemg.agencia.modelo;

public class PacoteNacional extends Pacote {
    private double impostoTurismo = 0.05;

    public PacoteNacional() {
    }

    public PacoteNacional(Integer id, String destino, int duracao, double valorBase) {
        super(id, destino, duracao, valorBase);
    }

    public double getImpostoTurismo() {
        return impostoTurismo;
    }

    public void setImpostoTurismo(double impostoTurismo) {
        this.impostoTurismo = impostoTurismo;
    }

    @Override
    public double calcularValorFinal() {
        return valorBase * (1 + impostoTurismo);
    }
}
