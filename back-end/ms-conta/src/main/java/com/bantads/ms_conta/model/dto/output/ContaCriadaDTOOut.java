package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ContaDTO", description = "Representa os dados de uma conta bancária")
public final class ContaCriadaDTOOut {

    @Schema(description = "Identificador único da conta", example = "1")
    private Long id;

    @Schema(description = "Número único da conta", example = "12345")
    private String numero;

    @Schema(description = "Data de criação da conta", example = "2025-08-25")
    private LocalDateTime dataCriacao;

    @Schema(description = "Saldo atual da conta", example = "1500.50")
    private BigDecimal saldo;

    @Schema(description = "Limite de crédito da conta", example = "2000.00")
    private BigDecimal limite;

    @Schema(description = "Identificador do cliente", example = "101")
    private String idCliente;

    @Schema(description = "Identificador do gerente", example = "5")
    private String idGerente;

}