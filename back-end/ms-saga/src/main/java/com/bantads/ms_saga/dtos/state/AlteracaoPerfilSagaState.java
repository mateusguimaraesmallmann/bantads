package com.bantads.ms_saga.dtos.state;

import java.math.BigDecimal;

import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class AlteracaoPerfilSagaState {
    
    private String cpf; 
    private Long clienteId; 
    private BigDecimal novoSalario; 
    private BigDecimal salarioAntigo; 

    public boolean isSalarioAlterado() {
        return novoSalario != null && novoSalario.compareTo(BigDecimal.ZERO) > 0;
    }

    @JsonIgnore 
    public AlteracaoPerfilSagaState(String cpf, AlteracaoPerfilRequest request) {
        this.cpf = cpf;
        if (request != null) { 
            this.novoSalario = request.getSalario();
        }
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getNovoSalario() {
        return novoSalario;
    }

    public void setNovoSalario(BigDecimal novoSalario) {
        this.novoSalario = novoSalario;
    }

    public BigDecimal getSalarioAntigo() {
        return salarioAntigo;
    }

    public void setSalarioAntigo(BigDecimal salarioAntigo) {
        this.salarioAntigo = salarioAntigo;
    }

    
    
}