package com.bantads.ms_saga.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTOIn {
    @Schema(type = "String", example = "Rua", description = "Tipo do logradouro (Rua, Avenida, etc.)")
    @NotEmpty(message = "O tipo do logradouro é obrigatório.")
    @Size(min = 3, max = 20, message = "O tipo do logradouro deve ter entre 3 e 20 caracteres.")
    private String tipoLogradouro;

    @Schema(type = "String", example = "Paulista", description = "Nome do logradouro")
    @NotEmpty(message = "O logradouro é obrigatório.")
    @Size(min = 3, max = 100, message = "O logradouro deve ter entre 3 e 100 caracteres.")
    private String logradouro;

    @Schema(type = "String", example = "1000", description = "Número do endereço")
    @NotEmpty(message = "O número é obrigatório.")
    @Size(min = 1, max = 10, message = "O número deve ter entre 1 e 10 caracteres.")
    private String numero;

    @Schema(type = "String", example = "Bloco B, Apto 45", description = "Complemento do endereço")
    private String complemento;

    @Schema(type = "String", example = "01311200", description = "CEP")
    @NotEmpty(message = "O CEP é obrigatório.")
    @Size(min = 8, max = 8, message = "O CEP deve ter exatamente 8 dígitos numéricos.")
    private String cep;

    @Schema(type = "String", example = "São Paulo", description = "Nome da cidade")
    @NotEmpty(message = "A cidade é obrigatória.")
    @Size(min = 3, max = 100, message = "A cidade deve ter entre 3 e 100 caracteres.")
    private String cidade;

    @Schema(type = "String", example = "SP", description = "Sigla do estado")
    @NotEmpty(message = "O estado é obrigatório.")
    @Size(min = 2, max = 2, message = "A sigla do estado deve ter exatamente 2 caracteres.")
    private String estado;
}
