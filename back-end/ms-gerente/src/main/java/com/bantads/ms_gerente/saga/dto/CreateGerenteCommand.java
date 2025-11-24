package com.bantads.ms_gerente.saga.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

@Data
public class CreateGerenteCommand {
    private String cpf;
    private String nome;
    private String email;
    private String telefone;

    public CreateGerenteCommand() {}

    @JsonCreator
    public static CreateGerenteCommand fromString(String cpf) {
        CreateGerenteCommand command = new CreateGerenteCommand();
        command.setCpf(cpf);
        return command;
    }

    @JsonCreator
    public CreateGerenteCommand(
            @JsonProperty("cpf") String cpf,
            @JsonProperty("nome") String nome,
            @JsonProperty("email") String email,
            @JsonProperty("telefone") String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}