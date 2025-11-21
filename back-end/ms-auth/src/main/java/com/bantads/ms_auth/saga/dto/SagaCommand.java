package com.bantads.ms_auth.saga.dto;

import lombok.Data;

@Data
public class SagaCommand<T> { private T payload; }
