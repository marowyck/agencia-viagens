package br.edu.uemg.agencia.util;

import java.util.regex.Pattern;

public final class Validator {

    private Validator() {
    }

    private static final Pattern CPF_ONLY_DIGITS = Pattern.compile("\\d{11}");
    private static final Pattern EMAIL = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9\\s()-]{8,20}$");

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;
        String digits = cpf.replaceAll("\\D", "");
        if (!CPF_ONLY_DIGITS.matcher(digits).matches()) return false;
        int[] nums = new int[11];
        for (int i = 0; i < 11; i++) nums[i] = digits.charAt(i) - '0';
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += nums[i] * (10 - i);
        int r = sum % 11;
        int d1 = (r < 2) ? 0 : 11 - r;
        if (d1 != nums[9]) return false;
        sum = 0;
        for (int i = 0; i < 10; i++) sum += nums[i] * (11 - i);
        r = sum % 11;
        int d2 = (r < 2) ? 0 : 11 - r;
        return d2 == nums[10];
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return PHONE.matcher(phone).matches();
    }
}
