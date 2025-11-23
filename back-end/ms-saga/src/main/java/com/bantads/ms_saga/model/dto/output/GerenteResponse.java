package com.bantads.ms_saga.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteResponse {
    
    private Long id;
    
    private String nome;
    
    private String email;
    
    private String cpf;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    } 
    
}