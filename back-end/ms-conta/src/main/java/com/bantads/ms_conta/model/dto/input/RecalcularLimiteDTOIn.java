package com.bantads.ms_conta.model.dto.input;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class RecalcularLimiteDTOIn {
    private Long idDoCliente;
    private BigDecimal novoSalario;
}