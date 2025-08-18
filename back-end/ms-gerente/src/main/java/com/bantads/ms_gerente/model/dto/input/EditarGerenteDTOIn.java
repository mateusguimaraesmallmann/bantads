package com.bantads.ms_gerente.model.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarGerenteDTOIn {
    @Schema(description = "Nome completo do gerente", example = "Geniéve da Silva")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "E-mail do gerente", example = "genieve.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    // @Schema(description = "Telefone do gerente com DDD", example = "41987654321")
    // @NotBlank(message = "O telefone é obrigatório")
    // private String telefone;
}
