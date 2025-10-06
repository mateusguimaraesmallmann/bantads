package com.bantads.ms_saga.model.dto.output;

import lombok.Data;

@Data
public class AprovarClienteDTOOut {
    private String cliente;
    private String numero;
    private double saldo;
    private double limite;
    private String gerente;
    private String criacao;
    
}
