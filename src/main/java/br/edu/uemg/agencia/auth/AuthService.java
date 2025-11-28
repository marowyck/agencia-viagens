package br.edu.uemg.agencia.auth;

import br.edu.uemg.agencia.modelo.Usuario;
import br.edu.uemg.agencia.repos.UsuarioRepo;

import java.util.Optional;

public class AuthService {

    private static final UsuarioRepo repo = new UsuarioRepo();

    public static Usuario login(String username, String password) {

        String hash = br.edu.uemg.agencia.auth.HashUtil.sha256(password);

        boolean ok = repo.authenticate(username, hash);

        if (!ok) return null;

        Optional<Usuario> user = repo.findByUsername(username);
        return user.orElse(null);
    }
}
