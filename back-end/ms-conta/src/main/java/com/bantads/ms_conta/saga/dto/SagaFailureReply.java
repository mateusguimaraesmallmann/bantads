package com.bantads.ms_conta.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaFailureReply {
    private String microservice;
    private String message;
    private String details;
}