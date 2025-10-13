package com.bantads.ms_gerente.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GerenteRemovidoEvent implements Serializable {
    private String gerenteId;
    private String nome;

}