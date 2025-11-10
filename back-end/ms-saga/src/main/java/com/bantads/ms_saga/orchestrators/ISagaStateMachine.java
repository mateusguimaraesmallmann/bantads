package com.bantads.ms_saga.orchestrators;

import com.bantads.ms_saga.dtos.saga.SagaReply;
import com.bantads.ms_saga.entity.SagaInstance;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ISagaStateMachine {

    void handleReply(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException;

    void handleFailure(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException;
}