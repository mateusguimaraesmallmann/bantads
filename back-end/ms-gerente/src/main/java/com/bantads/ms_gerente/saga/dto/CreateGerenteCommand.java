package com.bantads.ms_gerente.saga.dto;

import lombok.Data;

@Data
public class CreateGerenteCommand {

    private String cpf;
    private String nome;
    private String email;
    private String telefone;

}