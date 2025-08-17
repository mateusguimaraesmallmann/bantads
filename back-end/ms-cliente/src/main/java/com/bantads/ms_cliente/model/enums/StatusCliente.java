package com.bantads.ms_cliente.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCliente {

    EM_ANALISE("Análise não realizada pelo gerente"),
    APROVADO("Aprovado pelo gerente"),
    REJEITADO("Reprovado pelo gerente");

    private final String description;
}
