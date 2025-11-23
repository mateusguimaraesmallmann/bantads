package com.bantads.ms_saga.dtos.state;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteSagaState {
    
    private String cpf;

    public ClienteSagaState(String cpf) {
        this.cpf = cpf;
    }
}