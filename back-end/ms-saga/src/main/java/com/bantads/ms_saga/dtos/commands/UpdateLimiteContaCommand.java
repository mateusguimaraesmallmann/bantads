package com.bantads.ms_saga.dtos.commands;
import java.math.BigDecimal;


public class UpdateLimiteContaCommand {
    private Long idCliente;
    private BigDecimal novoSalario;

    public UpdateLimiteContaCommand(long id, BigDecimal salario) {
        this.idCliente = id;
        this.novoSalario = salario;
    
    }
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    public BigDecimal getNovoSalario() {
        return novoSalario;
    }
    public void setNovoSalario(BigDecimal novoSalario) {
        this.novoSalario = novoSalario;
    }

    
}
