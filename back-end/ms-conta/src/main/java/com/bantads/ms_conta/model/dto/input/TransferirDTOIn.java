package com.bantads.ms_conta.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(name = "TransferirDTOIn", description = "Representa os dados da transferência")
public final class TransferirDTOIn {

    @Schema(description = "Número da conta de destino", example = "1500")
    private String destino;

    @Schema(description = "Valor a ser depositado ou sacado", example = "1500.50")
    private BigDecimal valor;

}