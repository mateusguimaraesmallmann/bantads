package com.bantads.ms_conta.model.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaCreatedEvent {

    Long id;
    String numero;
    Long idCliente;
    Long idGerente;
    BigDecimal saldo;
    BigDecimal limite;
    LocalDateTime dataCriacao;
    
}