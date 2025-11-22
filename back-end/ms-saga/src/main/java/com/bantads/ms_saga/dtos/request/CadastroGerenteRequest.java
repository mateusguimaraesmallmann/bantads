package com.bantads.ms_saga.dtos.request;

import lombok.Data;

@Data
public class CadastroGerenteRequest {
    
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String senha;

}