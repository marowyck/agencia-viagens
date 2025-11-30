package br.edu.uemg.agencia.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.function.Function;

public class ComboSearchSupport<T> {
    private static boolean internalChange = false;

    public static <T> void enable(JComboBox<T> combo, List<T> listaOriginal, Function<T, String> fieldGetter) {
        combo.setEditable(true);
        JTextField editor = (JTextField) combo.getEditor().getEditorComponent();

        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void atualizar() {
                if (internalChange) return;
                SwingUtilities.invokeLater(() -> {
                    String texto = editor.getText().toLowerCase();
                    internalChange = true;
                    combo.removeAllItems();
                    listaOriginal.stream()
                            .filter(item -> fieldGetter.apply(item).toLowerCase().contains(texto))
                            .forEach(combo::addItem);
                    combo.setPopupVisible(true);
                    internalChange = false;
                });
            }
            public void insertUpdate(DocumentEvent e) { atualizar(); }
            public void removeUpdate(DocumentEvent e) { atualizar(); }
            public void changedUpdate(DocumentEvent e) { atualizar(); }
        });
    }
}