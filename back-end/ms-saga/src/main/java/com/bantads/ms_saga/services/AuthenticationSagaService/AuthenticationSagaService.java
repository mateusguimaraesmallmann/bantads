package com.bantads.ms_saga.services.AuthenticationSagaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.dtos.request.LoginRequestDTO;

@Service
public class AuthenticationSagaService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSagaService.class);

    public ResponseEntity<Object> login(LoginRequestDTO reqLoginDTO) {

        logger.info("Iniciada implementação saga login");

        return new ResponseEntity<>(null);
    }
    
}