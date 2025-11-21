package com.bantads.ms_conta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

//Retorna a contabilização de contas por gerente
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteCargaDTO implements Serializable {
    private Long idGerente;
    private Long quantidadeContas;
}