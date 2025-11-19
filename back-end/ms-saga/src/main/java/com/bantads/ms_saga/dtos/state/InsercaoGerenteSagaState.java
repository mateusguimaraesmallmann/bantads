package com.bantads.ms_saga.dtos.state;

import com.bantads.ms_saga.dtos.request.CadastroGerenteRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InsercaoGerenteSagaState {

    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String senha;
 
    public InsercaoGerenteSagaState(CadastroGerenteRequest request) {
        this.cpf = request.getCpf();
        this.nome = request.getNome();
        this.email = request.getEmail();
        this.telefone = request.getTelefone();
        this.senha = request.getSenha();
    }

}