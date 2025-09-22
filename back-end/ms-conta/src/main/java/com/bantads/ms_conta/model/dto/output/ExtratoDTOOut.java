package com.bantads.ms_conta.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(name = "ExtratoDTOOut", description = "Representa os dados das movimentações de uma conta")
public final class ExtratoDTOOut {

    @Schema(description = "Número único da conta", example = "12345")
    private Long numero;

    @Schema(description = "Saldo atual da conta", example = "1500.50")
    private BigDecimal saldo;

    @Schema(description = "Histórico de movimentações da conta")
    private List<MovimentacaoDTOOut> movimentacoes;
}