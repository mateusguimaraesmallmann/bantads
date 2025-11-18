package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.services.AlteracaoPerfilUsuarioSagaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga/alteracao-perfil")
public class AlteracaoPerfilUsuarioSagaController {

    @Autowired
    private AlteracaoPerfilUsuarioSagaService alteracaoPerfilUsuarioSagaService;

    @PutMapping("/{cpf}")
    public ResponseEntity<?> alterarPerfil(@PathVariable String cpf, @RequestBody AlteracaoPerfilRequest request) {
        return alteracaoPerfilUsuarioSagaService.alterarPerfil(cpf, request);
    }

}