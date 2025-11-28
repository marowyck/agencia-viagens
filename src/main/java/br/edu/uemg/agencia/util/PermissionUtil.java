package br.edu.uemg.agencia.auth;

import br.edu.uemg.agencia.log.LogService;

import javax.swing.*;
import java.awt.*;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean isAdmin() {
        return Sessao.getPerfil() != null &&
                Sessao.getPerfil().equalsIgnoreCase("admin");
    }

    public static boolean requireAdmin(Component parent, String acao, String tela) {

        if (isAdmin()) {
            return true;
        }

        JOptionPane.showMessageDialog(parent,
                "Ação restrita: apenas administradores podem executar esta operação.",
                "Permissão Negada",
                JOptionPane.WARNING_MESSAGE
        );

        String usuario = Sessao.getUsuarioNome() != null ? Sessao.getUsuarioNome() : "N/A";

        LogService.save(
                usuario,
                "Tentativa não autorizada: " + acao,
                tela,
                "SECURITY",
                "localhost"
        );

        return false;
    }
}
