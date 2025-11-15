package com.bantads.ms_cliente.saga.dto;
import lombok.Data;
@Data
public class SagaCommand<T> { private T payload; }