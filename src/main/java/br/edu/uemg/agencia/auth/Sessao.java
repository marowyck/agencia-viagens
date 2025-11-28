package br.edu.uemg.agencia.auth;

import br.edu.uemg.agencia.modelo.Usuario;

public class Sessao {

    private static Usuario usuarioLogado;

    public static void setUsuario(Usuario u) {
        usuarioLogado = u;
    }

    public static Usuario getUsuario() {
        return usuarioLogado;
    }

    public static String getUsuarioNome() {
        return usuarioLogado != null ? usuarioLogado.getNome() : null;
    }

    public static String getPerfil() {
        return usuarioLogado != null ? usuarioLogado.getPerfil() : null;
    }

    public static void logout() {
        usuarioLogado = null;
    }
}
