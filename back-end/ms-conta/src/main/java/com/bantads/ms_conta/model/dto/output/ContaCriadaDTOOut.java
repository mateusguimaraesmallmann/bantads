package com.bantads.ms_conta.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class ContaCriadaDTOOut {

    private Long id;
    private String numero;
    private java.time.LocalDateTime dataCriacao;
    private java.math.BigDecimal saldo;
    private java.math.BigDecimal limite;
    private Long idCliente;
    private Long idGerente;

}