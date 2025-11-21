package com.bantads.ms_conta.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContaCommand {
    private Long idCliente;
    private BigDecimal salario;
}