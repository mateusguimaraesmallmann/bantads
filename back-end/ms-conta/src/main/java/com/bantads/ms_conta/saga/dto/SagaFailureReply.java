package com.bantads.ms_conta.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaFailureReply {
    
    private String microservice = "ms-conta";
    private String message;
    private String details;
    
}