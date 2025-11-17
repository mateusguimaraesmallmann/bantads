package com.bantads.ms_saga.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientCommand {
    private String cpf;
    private String nome;
    private String email;
    //private String telefone;
    private BigDecimal salario;

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
}