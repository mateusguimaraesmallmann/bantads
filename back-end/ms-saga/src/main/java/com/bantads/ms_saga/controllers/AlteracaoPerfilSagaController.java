package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.services.AlteracaoPerfilUsuarioSagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saga/alteracao-perfil")
public class AlteracaoPerfilSagaController {
    
    @Autowired
    private AlteracaoPerfilUsuarioSagaService alteracaoPerfilUsuarioSagaService;

    @PutMapping("/{cpf}")
    public ResponseEntity<?> alterarPerfil(@PathVariable String cpf, @RequestBody AlteracaoPerfilRequest request) {
        return alteracaoPerfilUsuarioSagaService.alterarPerfil(cpf,request);
    }
}