package com.bantads.ms_saga.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLimiteContaCommand {
    private String cpfCliente;
    private BigDecimal novoSalario;
}