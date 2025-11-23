package com.bantads.ms_conta.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Schema(name = "DepositarSacarDTOIn", description = "Representa o valor que será depositado ou sacado")
public final class DepositarSacarDTOIn {

    @Schema(description = "Valor a ser depositado ou sacado", example = "1500.50")
    private BigDecimal valor;

    public BigDecimal getValor() {
        return valor;
    }

}