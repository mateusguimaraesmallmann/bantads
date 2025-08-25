package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(name = "DepositarSacarDTOOut", description = "Representa os dados de depósito e saque")
public final class DepositarSacarDTOOut {

    @Schema(description = "Número único da conta", example = "12345")
    private String conta;

    @Schema(description = "Data de criação da conta", example = "2025-08-25")
    private LocalDateTime dataCriacao;

    @Schema(description = "Saldo atual da conta", example = "1500.50")
    private BigDecimal saldo;

}