package br.edu.uemg.agencia.modelo;

public class Usuario {
    private Integer id;
    private String username;
    private String passwordHash;
    private String perfil;
    private String nome;

    public Usuario(Integer id, String username, String passwordHash, String perfil, String nome) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getNome() {
        return nome;
    }
}
