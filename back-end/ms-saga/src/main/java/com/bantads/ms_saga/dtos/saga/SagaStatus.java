package com.bantads.ms_saga.dtos.saga;

public enum SagaStatus {
    STARTED,        
    PROCESSING,     
    SUCCEEDED,      
    ROLLING_BACK,   
    FAILED         
}