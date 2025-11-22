package com.bantads.ms_saga.orchestrators;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.commands.CreateAuthUserCommand;
import com.bantads.ms_saga.dtos.commands.CreateContaCommand;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.saga.SagaReply;
import com.bantads.ms_saga.dtos.saga.SagaStatus;
import com.bantads.ms_saga.dtos.state.AutocadastroSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.services.RabbitMQSender;
import com.bantads.ms_saga.services.SagaInstanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AutocadastroSaga implements ISagaStateMachine {

    private static final Logger logger = LoggerFactory.getLogger(AutocadastroSaga.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;
    @Autowired private ObjectMapper objectMapper;

    @Override
    public void handleReply(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        AutocadastroSagaState state = objectMapper.readValue(instance.getPayload(), AutocadastroSagaState.class);
        SagaStatus currentState = instance.getCurrentState();

        switch (currentState) {
            case STARTED:
                handleClienteCreated(instance, state, reply);
                break;
            
            case PROCESSING: 
                handleAuthUserCreated(instance, state, reply);
                break;
        }
    }

    // Tratativa das falhas
    @Override
    public void handleFailure(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        AutocadastroSagaState state = objectMapper.readValue(instance.getPayload(), AutocadastroSagaState.class);
        SagaStatus failedState = instance.getCurrentState();
        
        sagaService.updateSagaState(instance, SagaStatus.ROLLING_BACK, state);
        logger.info("Iniciando Rollback do SAGA {} que falhou no estado {}", instance.getCorrelationId(), failedState);

        switch (failedState) {
            case PROCESSING:
                logger.info("Compensando: Enviando comando de delete para ms-cliente (Cliente ID: {})", state.getClienteId());
                sender.sendSagaCommand(
                    RabbitMQConfig.CLIENTE_DELETE_KEY,
                    new SagaCommand<>(state.getClienteId()),
                    instance.getCorrelationId()
                );
                sagaService.updateSagaState(instance, SagaStatus.FAILED);
                break;
            
            case STARTED: 
                logger.info("Falha no primeiro passo. Nada a compensar.");
                sagaService.updateSagaState(instance, SagaStatus.FAILED);
                break;
        }
    }

    // Passos de Sucesso
    private void handleClienteCreated(SagaInstance instance, AutocadastroSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 1 (Cliente) OK.", instance.getCorrelationId());
        
        if (reply.getPayload() instanceof Map) {
             Integer id = (Integer) ((Map<?, ?>) reply.getPayload()).get("id");
             state.setClienteId(id.longValue());
        }
        sagaService.updateSagaState(instance, SagaStatus.PROCESSING, state);

        CreateAuthUserCommand authCmd = new CreateAuthUserCommand(
            state.getEmail(), 
            "",
            "CLIENTE",
            "PENDING"   
        );
        System.out.println("Auth Command: " + authCmd);
        logger.info("SAGA {}: Enviando Passo 2 (Auth).", instance.getCorrelationId());
        sender.sendSagaCommand(
            RabbitMQConfig.AUTH_CREATE_KEY,
            new SagaCommand<>(authCmd),
            instance.getCorrelationId()
        );
    }

    private void handleAuthUserCreated(SagaInstance instance, AutocadastroSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 2 (Auth) OK.", instance.getCorrelationId());

        if (reply.getPayload() instanceof Map) {
             String id = (String) ((Map<?, ?>) reply.getPayload()).get("id");
             state.setAuthUserId(id);
        }
        
        CreateContaCommand contaCmd = new CreateContaCommand(
            state.getClienteId(),
            state.getSalario()
        );
        
        logger.info("SAGA {}: Enviando Passo 3 (Conta).", instance.getCorrelationId());
        sender.sendSagaCommand(
            RabbitMQConfig.CONTA_CREATE_KEY, 
            new SagaCommand<>(contaCmd),
            instance.getCorrelationId()
        );
        sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);
        
    }
}