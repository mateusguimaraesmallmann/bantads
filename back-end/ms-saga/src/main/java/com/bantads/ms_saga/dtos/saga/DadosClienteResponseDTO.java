package com.bantads.ms_saga.dtos.saga;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DadosClienteResponseDTO {

    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private BigDecimal salario;
    private String endereco;
    private String cidade;
    private String estado;
    
    @JsonProperty("conta")
    private String numeroConta;

    private BigDecimal saldo;
    private BigDecimal limite;

    @JsonProperty("gerente")
    private String cpfGerente;

    @JsonProperty("gerente_nome")
    private String nomeGerente;

    @JsonProperty("gerente_email")
    private String emailGerente;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public String getCpfGerente() {
        return cpfGerente;
    }

    public void setCpfGerente(String cpfGerente) {
        this.cpfGerente = cpfGerente;
    }

    public String getNomeGerente() {
        return nomeGerente;
    }

    public void setNomeGerente(String nomeGerente) {
        this.nomeGerente = nomeGerente;
    }

    public String getEmailGerente() {
        return emailGerente;
    }

    public void setEmailGerente(String emailGerente) {
        this.emailGerente = emailGerente;
    }


}