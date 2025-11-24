package com.bantads.ms_auth.dtos;

public record TokenDTO(
    String token,
    String role, 
    String status,
    String cpf,
    String email
) {}
