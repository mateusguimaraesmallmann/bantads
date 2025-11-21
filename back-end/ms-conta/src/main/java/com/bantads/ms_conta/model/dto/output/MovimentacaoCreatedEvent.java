package com.bantads.ms_conta.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoCreatedEvent {
    private Long id;
    private String numeroConta;
    private LocalDateTime data;
    private String tipo;
    private BigDecimal valor;
    private String contaOrigem;
    private String contaDestino;

}