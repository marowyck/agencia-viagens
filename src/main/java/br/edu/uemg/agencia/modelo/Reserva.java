package br.edu.uemg.agencia.modelo;

import java.time.LocalDateTime;

public class Reserva {
    private Integer id;
    private Cliente cliente;
    private Pacote pacote;
    private String status;
    private LocalDateTime dataReserva;
    private double valorTotal;

    public Reserva() {
    }

    public Reserva(Integer id, Cliente cliente, Pacote pacote) {
        this.id = id;
        this.cliente = cliente;
        this.pacote = pacote;
        this.status = "Criada";
        this.dataReserva = LocalDateTime.now();
        this.valorTotal = pacote != null ? pacote.calcularValorFinal() : 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Pacote getPacote() {
        return pacote;
    }

    public void setPacote(Pacote pacote) {
        this.pacote = pacote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void confirmar() {
        this.status = "Confirmada";
    }

    public void cancelar() {
        this.status = "Cancelada";
    }
}
