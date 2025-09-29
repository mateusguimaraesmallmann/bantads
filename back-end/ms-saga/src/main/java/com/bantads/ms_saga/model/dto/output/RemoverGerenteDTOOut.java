package com.bantads.ms_saga.model.dto.output;

import lombok.Data;

@Data
public class RemoverGerenteDTOOut {
    private String cpf;
    private String status;
    private String mensagem;
}
