package com.bantads.ms_cliente.saga.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaReply<T> {
    private boolean success;
    private T payload;
    private SagaFailureReply failureReply;

    public static <T> SagaReply<T> success(T payload) {
        return new SagaReply<>(true, payload, null);
    }
    public static <T> SagaReply<T> failure(SagaFailureReply failure) {
        return new SagaReply<>(false, null, failure);
    }
}