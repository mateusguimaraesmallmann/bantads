package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(name = "TransferirDTOOut", description = "Representa os dados de uma trnasferência")
public final class TransferirDTOOut {

    @Schema(description = "Número único da conta", example = "1234")
    private String conta;

    @Schema(description = "Data da transferência", example = "2025-08-25")
    private LocalDateTime data;

    @Schema(description = "Número da conta de destino", example = "1233")
    private String destino;

    @Schema(description = "Saldo atual da conta", example = "2000.00")
    private BigDecimal saldo;

    @Schema(description = "Valor da transferência", example = "20.00")
    private BigDecimal valor;
}