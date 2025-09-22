package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(name = "SaldoDTOOut", description = "Representa os dados do saldo uma conta bancária")
public final class SaldoDTOOut {

    @Schema(description = "Número único da conta", example = "12345")
    private Long conta;

    @Schema(description = "Saldo atual da conta", example = "1500.50")
    private BigDecimal saldo;

    @Schema(description = "Identificador do cliente", example = "101")
    private Long cliente;

}