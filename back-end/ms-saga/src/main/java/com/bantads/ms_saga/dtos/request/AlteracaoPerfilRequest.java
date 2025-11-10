package com.bantads.ms_saga.dtos.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AlteracaoPerfilRequest {
    private String nome;
    private String email;
    private String telefone;
    private BigDecimal salario;
}