package com.bantads.ms_saga.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGerenteCommand {

    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    
}