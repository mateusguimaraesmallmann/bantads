package com.bantads.ms_cliente.service;

import org.springframework.stereotype.Component;

@Component
public class CpfValidator {

    public boolean isCpfValido(String cpf) {
        if (cpf == null) return false;
        
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstVerifier = 11 - (sum % 11);
            if (firstVerifier > 9) firstVerifier = 0;

            if (firstVerifier != (cpf.charAt(9) - '0')) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondVerifier = 11 - (sum % 11);
            if (secondVerifier > 9) secondVerifier = 0;

            if (secondVerifier != (cpf.charAt(10) - '0')) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
