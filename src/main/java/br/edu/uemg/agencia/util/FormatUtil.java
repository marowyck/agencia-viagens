package br.edu.uemg.agencia.util;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class FormatUtil {

    public static JFormattedTextField createFormattedField(String mask) {
        try {
            MaskFormatter mf = new MaskFormatter(mask);
            mf.setPlaceholderCharacter('_');
            mf.setValueContainsLiteralCharacters(false);
            return new JFormattedTextField(mf);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }
}