package com.bantads.ms_saga.dtos.request;

import java.math.BigDecimal;

public class AlteracaoPerfilRequest {
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
    private String cpf;

    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }
    public BigDecimal getSalario() {
        return salario;
    }
    public String getCep() {
        return cep;
    }
    public String getLogradouro() {
        return logradouro;
    }
    public String getNumero() {
        return numero;
    }
    public String getComplemento() {
        return complemento;
    }
    public String getBairro() {
        return bairro;
    }
    public String getCidade() {
        return cidade;
    }
    public String getEstado() {
        return estado;
    }  
    public void setCpf(String cpf){
        this.cpf = cpf;
    }
    public String getCpf(){
        return cpf;
    }
}