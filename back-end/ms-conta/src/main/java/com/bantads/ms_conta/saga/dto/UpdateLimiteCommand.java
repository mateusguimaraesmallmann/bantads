package com.bantads.ms_conta.saga.dto;

import java.math.BigDecimal;


public class UpdateLimiteCommand {
    private Long idCLiente;
    private BigDecimal novoSalario;
    
    public Long getIdCLiente() {
        return idCLiente;
    }
    public void setIdCLiente(Long idCLiente) {
        this.idCLiente = idCLiente;
    }
    public BigDecimal getNovoSalario() {
        return novoSalario;
    }
    public void setNovoSalario(BigDecimal novoSalario) {
        this.novoSalario = novoSalario;
    }
}

