package com.bantads.ms_gerente.saga.dto;

import lombok.Data;

@Data
public class SagaCommand<T> { private T payload; }