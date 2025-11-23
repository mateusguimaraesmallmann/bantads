package com.bantads.ms_saga.model.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContaResponse {
    
    private String numero;
    private BigDecimal saldo;
    private BigDecimal limite;
    
    @JsonProperty("idGerente") 
    private Long idGerente; 
    
    private LocalDateTime dataCriacao;

    public String getNumero() {
        return numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public Long getIdGerente() {
        return idGerente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
}