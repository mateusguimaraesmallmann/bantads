package com.bantads.ms_saga.controllers; 

import com.bantads.ms_saga.dtos.request.AutocadastroRequest;
import com.bantads.ms_saga.services.AutocadastroSagaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga/autocadastro")
public class AutocadastroSagaController {

    @Autowired
    private AutocadastroSagaService autocadastroSagaService;

    @PostMapping
    public ResponseEntity<?> autoCadastro(@RequestBody AutocadastroRequest request) {
        return autocadastroSagaService.autoCadastro(request);
    }

}