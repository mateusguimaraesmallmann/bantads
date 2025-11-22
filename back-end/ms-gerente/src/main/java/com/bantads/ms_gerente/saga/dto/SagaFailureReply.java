package com.bantads.ms_gerente.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaFailureReply {
    
    private String microservice = "ms-gerente";
    private String message;
    private String details;
    
}