package com.bantads.ms_conta.model.dto.output;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class RecalcularLimiteDTOOut {
    private Long idGerente;
    private BigDecimal novoLimite;
    private BigDecimal saldoAtual;
}