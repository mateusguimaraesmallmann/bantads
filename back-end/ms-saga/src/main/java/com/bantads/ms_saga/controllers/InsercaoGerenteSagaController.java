package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.dtos.request.CadastroGerenteRequest;
import com.bantads.ms_saga.services.InsercaoGerenteSagaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga/insercao-gerente")
public class InsercaoGerenteSagaController {

    @Autowired
    private InsercaoGerenteSagaService insercaoGerenteSagaService;

    @PostMapping("/{cpf}")
    public ResponseEntity<?> cadastrarGerente(@RequestBody CadastroGerenteRequest request) {
        return insercaoGerenteSagaService.cadastrarGerente(request);
    }
    
}