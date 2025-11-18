package com.bantads.ms_saga.services;

import com.bantads.ms_saga.dtos.saga.SagaStatus;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.repository.SagaInstanceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SagaInstanceService {

    @Autowired
    private SagaInstanceRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public SagaInstance createSaga(String sagaType, Object statePayload) throws JsonProcessingException {
        UUID correlationId = UUID.randomUUID();
        String payload = objectMapper.writeValueAsString(statePayload);
        SagaInstance instance = new SagaInstance(correlationId, sagaType, payload);
        return repository.save(instance);
    }

    @Transactional
    public void updateSagaState(SagaInstance instance, SagaStatus newState, Object statePayload) throws JsonProcessingException {
        instance.setCurrentState(newState);
        instance.setPayload(objectMapper.writeValueAsString(statePayload));
        repository.save(instance);
    }

    @Transactional
    public void updateSagaState(SagaInstance instance, SagaStatus newState) {
        instance.setCurrentState(newState);
        repository.save(instance);
    }

}