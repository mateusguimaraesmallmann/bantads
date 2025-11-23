package com.bantads.ms_conta.saga.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.ms_conta.model.enums.Status;

public class ContaSyncDTO {
    private Long id;
    private Long idCliente;
    private Long idGerente;
    private String numero;
    private BigDecimal saldo;
    private BigDecimal limite;
    private LocalDateTime dataCriacao;
    private Status status;

    
    public ContaSyncDTO(Long id, Long idCliente, Long idGerente, String numero, BigDecimal saldo, BigDecimal limite,
            LocalDateTime dataCriacao, Status status) {
        this.id = id;
        this.idCliente = idCliente;
        this.idGerente = idGerente;
        this.numero = numero;
        this.saldo = saldo;
        this.limite = limite;
        this.dataCriacao = dataCriacao;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    public Long getIdGerente() {
        return idGerente;
    }
    public void setIdGerente(Long idGerente) {
        this.idGerente = idGerente;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
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
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    
}