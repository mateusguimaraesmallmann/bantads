package com.bantads.ms_cliente.saga.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
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