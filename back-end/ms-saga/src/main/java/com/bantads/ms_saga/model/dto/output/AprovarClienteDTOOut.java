package com.bantads.ms_saga.model.dto.output;

import lombok.Data;

@Data
public class AprovarClienteDTOOut {
    private String cpf;
    private boolean aprovado;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }
}
