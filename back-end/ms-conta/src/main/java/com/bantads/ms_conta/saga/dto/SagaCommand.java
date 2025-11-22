package com.bantads.ms_conta.saga.dto;

import lombok.Data;

@Data
public class SagaCommand<T> { private T payload; }
