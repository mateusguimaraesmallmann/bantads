package com.bantads.ms_cliente.saga.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaFailureReply {
    private String microservice = "ms-cliente"; 
    private String message;
    private String details;
}