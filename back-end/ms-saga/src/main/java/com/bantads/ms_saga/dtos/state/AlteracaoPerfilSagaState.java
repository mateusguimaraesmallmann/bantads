package com.bantads.ms_saga.dtos.state;

import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AlteracaoPerfilSagaState {
    
    private String cpf;
    private BigDecimal novoSalario;
    private boolean salarioAlterado; 

    public AlteracaoPerfilSagaState(String cpf, AlteracaoPerfilRequest request, BigDecimal salarioAntigo) {
        this.cpf = cpf;
        this.novoSalario = request.getSalario();
        this.salarioAlterado = salarioAntigo == null || request.getSalario().compareTo(salarioAntigo) != 0;
    }
}