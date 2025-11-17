package com.bantads.ms_saga.controllers.AuthenticationSagaController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.ms_saga.dtos.request.LoginRequestDTO;
import com.bantads.ms_saga.services.AuthenticationSagaService.AuthenticationSagaService;

@RestController
@RequestMapping("/saga/auth")
@CrossOrigin(origins = "*")
public class AuthenticationSagaController {
    
    @Autowired
    private AuthenticationSagaService authenticationSagaService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO reqLoginDTO) {
        return authenticationSagaService.login(reqLoginDTO);
    }

}