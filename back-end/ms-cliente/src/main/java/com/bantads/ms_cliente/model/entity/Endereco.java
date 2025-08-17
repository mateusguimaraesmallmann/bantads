package com.bantads.ms_cliente.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Endereco {

    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String estado;

}