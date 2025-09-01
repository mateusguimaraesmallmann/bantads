package com.bantads.ms_cliente.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class ClienteAprovadoDTOOut {

    @Schema(type = "Long", description = "ID do cliente")
    private Long cliente;

    @Schema(description = "Número da conta criada", example = "1234")
    private String numero;

    @Schema(description = "Saldo da conta", example = "2000.00")
    private BigDecimal saldo;

    @Schema(description = "Limite da conta", example = "1000.00")
    private BigDecimal limite;

    @Schema(description = "ID do gerente da conta", example = "12345678901")
    private String gerente;

    @Schema(description = "Data da criação da conta", example = "2025-08-01T10:22:45-03:00")
    private LocalDateTime criacao;

}
