package com.bantads.ms_cliente.model.dto.input;

import com.bantads.ms_cliente.model.dto.output.EnderecoDTOOut;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public final class CriarClienteDTOIn {

    @Schema(description = "CPF do cliente (somente números)", example = "12345678901")
    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @Schema(description = "Nome completo do cliente", example = "Maria da Silva")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "E-mail do cliente", example = "maria.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    @Schema(description = "Telefone do cliente com DDD", example = "41987654321")
    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    @Schema(description = "Salário do cliente", example = "3500.50")
    @NotNull(message = "O salário é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O salário deve ser maior que zero")
    private BigDecimal salario;

    @Schema(exampleClasses = {EnderecoDTOOut.class}, description = "Endereço do cliente")
    private EnderecoDTOOut endereco;

}