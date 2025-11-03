package com.bantads.ms_saga.model.dto.input;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class RecalcularLimiteDTOIn {
    private String cpf;
    private BigDecimal novoSalario;
}