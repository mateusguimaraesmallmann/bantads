package com.bantads.ms_conta.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(name = "SalvarContaMongoDTOIn", description = "Representa os dados de criação de uma conta bancária")
public final class SalvarContaMongoDTOIn {

    @Schema(description = "Saldo inicial da conta", example = "1000.00")
    private BigDecimal saldoInicial;

    @Schema(description = "Número da conta", example = "1234")
    private Long numero;

    @Schema(description = "Identificador do cliente que terá a conta", example = "101")
    private Long idCliente;

}