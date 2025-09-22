package com.bantads.ms_conta.model.entity.mongo;

import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {

    @Id
    private String id;

    private LocalDateTime data;

    private TipoMovimentacao tipo;

    private BigDecimal valor;

    private String contaOrigem;

    private String contaDestino;
}
