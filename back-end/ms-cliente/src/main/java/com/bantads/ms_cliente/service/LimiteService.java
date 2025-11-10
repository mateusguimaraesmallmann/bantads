package com.bantads.ms_cliente.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class LimiteService {

    private static final BigDecimal LIMITE_MULTIPLIER = new BigDecimal("0.30");
    public static final BigDecimal RENDA_MINIMA_EXIGIDA = new BigDecimal("1000.00"); 
    private static final BigDecimal LIMITE_MINIMO = new BigDecimal("500.00");

    public BigDecimal calcularLimiteSugerido(BigDecimal renda) {
        if (renda == null || renda.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (renda.compareTo(RENDA_MINIMA_EXIGIDA) < 0) {
            return BigDecimal.ZERO; 
        }
        
        BigDecimal limiteSugerido = renda.multiply(LIMITE_MULTIPLIER);
        
        limiteSugerido = limiteSugerido.setScale(2, RoundingMode.HALF_UP);

        if (limiteSugerido.compareTo(LIMITE_MINIMO) < 0) {
            return LIMITE_MINIMO;
        }

        return limiteSugerido;
    }

    public boolean isRendaSuficiente(BigDecimal renda) {
        return renda != null && renda.compareTo(RENDA_MINIMA_EXIGIDA) >= 0;
    }
}
