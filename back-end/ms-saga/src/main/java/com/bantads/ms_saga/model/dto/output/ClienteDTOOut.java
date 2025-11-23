package com.bantads.ms_saga.model.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
public final class ClienteDTOOut {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private BigDecimal salario;

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
    public String getTelefone() {
        return telefone;
    }
    public BigDecimal getSalario() {
        return salario;
    }

    @Schema(exampleClasses = {EnderecoDTOOut.class}, description = "Endereço do cliente")
    private EnderecoDTOOut endereco;

    public EnderecoDTOOut getEndereco(){
        return this.endereco;
    }
}
