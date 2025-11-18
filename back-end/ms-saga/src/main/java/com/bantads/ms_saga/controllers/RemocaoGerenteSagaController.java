package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.services.RemocaoGerenteSagaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga/remocao-gerente")
public class RemocaoGerenteSagaController {

    @Autowired
    private RemocaoGerenteSagaService remocaoGerenteSagaService;

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> removerGerente(@PathVariable String cpf) {
        return remocaoGerenteSagaService.removerGerente(cpf);
    }
    
}