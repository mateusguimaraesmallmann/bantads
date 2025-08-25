package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@Schema(name = "MovimentacaoDTOOut", description = "Representa uma movimentação financeira da conta")
public final class MovimentacaoDTOOut {

    @Schema(description = "Data e hora da movimentação", example = "2025-08-25T14:30:00")
    private LocalDateTime data;

    @Schema(description = "Tipo de movimentação (DEPOSITO, SAQUE, TRANSFERENCIA)", example = "DEPOSITO")
    private String tipo;

    @Schema(description = "Valor movimentado", example = "500.00")
    private BigDecimal valor;

    @Schema(description = "Conta do cliente de origem (apenas para transferências)", example = "1010")
    private String origem;

    @Schema(description = "Conta do cliente de destino (apenas para transferências)", example = "2020")
    private String destino;

}
