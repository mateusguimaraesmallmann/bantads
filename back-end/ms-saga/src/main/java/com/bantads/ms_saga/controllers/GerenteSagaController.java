package com.bantads.ms_saga.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saga/gerentes")
public class GerenteSagaController {
    
    private static final Logger logger = LoggerFactory.getLogger(GerenteSagaController.class);

    @PostMapping
    public ResponseEntity<?> inserirGerente() {

        logger.info("SAGA de Inserção de Gerente iniciado.");
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> removerGerente(@PathVariable String cpf) {
        logger.info("SAGA de Remoção de Gerente iniciado para o CPF: {}", cpf);
        return ResponseEntity.accepted().build();
    }
}