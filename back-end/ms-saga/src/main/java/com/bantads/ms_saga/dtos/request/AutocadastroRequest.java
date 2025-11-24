package com.bantads.ms_saga.dtos.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AutocadastroRequest {
    private String cpf;
    private String nome;
    private String email;
    private BigDecimal salario;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
}