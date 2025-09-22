package com.bantads.ms_auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    
    @NotBlank(message = "Email é obrigatorio")
    String email,

    @NotBlank(message = "Senha é obrigatorio")
    String senha
) {}