package com.bantads.ms_gerente.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteCargaDTO implements Serializable {
    private Long idGerente;
    private Long quantidadeContas;
}