package com.bantads.ms_conta.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(name = "CriarContaDTOIn", description = "Representa os dados de criação de uma conta bancária")
public final class CriarContaDTOIn {

    @Schema(description = "Saldo inicial da conta", example = "1000.00")
    private BigDecimal saldoInicial;

    @Schema(description = "Limite de crédito da conta", example = "2000.00")
    private BigDecimal limite;

    @Schema(description = "Identificador do cliente que terá a conta", example = "101")
    private Long idCliente;

    @Schema(description = "Identificador do gerente responsável pela conta", example = "5")
    private Long idGerente;

}