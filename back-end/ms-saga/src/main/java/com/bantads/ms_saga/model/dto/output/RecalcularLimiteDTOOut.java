package com.bantads.ms_saga.model.dto.output;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class RecalcularLimiteDTOOut {
    private Long idUsuario;
    private BigDecimal novoSalario;
}