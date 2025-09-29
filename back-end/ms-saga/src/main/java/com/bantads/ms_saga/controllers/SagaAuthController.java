package com.bantads.ms_saga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.empresa_aerea.saga.dtos.LoginRequestDTO;
import br.com.empresa_aerea.saga.services.SagaAuthService;

@Component
@RestController
@RequestMapping("/saga/auth")
@CrossOrigin(origins = "*")
public class SagaAuthController {

    @Autowired
    private SagaAuthService sagaAuthService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Validated @RequestBody LoginRequestDTO body) {
        return sagsagaAuthServiceaAuthService.login(body);
    }
    
}