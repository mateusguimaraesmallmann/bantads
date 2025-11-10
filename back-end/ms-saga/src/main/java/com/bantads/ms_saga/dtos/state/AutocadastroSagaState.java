package com.bantads.ms_saga.dtos.state;

import com.bantads.ms_saga.dtos.request.AutocadastroRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AutocadastroSagaState {
    
    private String cpf;
    private String nome;
    private String email;
    private String senha; 
    private BigDecimal salario;

    private Long clienteId;     
    private String authUserId;  
    private Long gerenteId;   

    public AutocadastroSagaState(AutocadastroRequest request) {
        this.cpf = request.getCpf();
        this.nome = request.getNome();
        this.email = request.getEmail();
        this.senha = request.getSenha();
        this.salario = request.getSalario();
    }
}