package com.bantads.ms_saga.orchestrators;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.commands.ReassingContaGerenteCommand;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.saga.SagaReply;
import com.bantads.ms_saga.dtos.saga.SagaStatus;
import com.bantads.ms_saga.dtos.state.InsercaoGerenteSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.services.RabbitMQSender;
import com.bantads.ms_saga.services.SagaInstanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InsercaoGerenteSaga implements ISagaStateMachine {

    private static final Logger logger = LoggerFactory.getLogger(InsercaoGerenteSaga.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;
    @Autowired private ObjectMapper objectMapper;
    
    @Override
    public void handleReply(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        InsercaoGerenteSagaState state = objectMapper.readValue(instance.getPayload(), InsercaoGerenteSagaState.class);
        SagaStatus currentState = instance.getCurrentState();

        switch (currentState) {
            case STARTED:
                handleGerenteCreated(instance, state, reply);
                break;
            
            case PROCESSING: 
                handleAtribuirConta(instance, state, reply);
                break;
        }
    }

    @Override
    public void handleFailure(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        InsercaoGerenteSagaState state = objectMapper.readValue(instance.getPayload(), InsercaoGerenteSagaState.class);
        SagaStatus failedState = instance.getCurrentState();
        
        sagaService.updateSagaState(instance, SagaStatus.ROLLING_BACK, state);
        logger.info("Iniciando Rollback do SAGA {} que falhou no estado {}", instance.getCorrelationId(), failedState);

        switch (failedState) {
            case PROCESSING:
                logger.info("Compensando: Enviando comando de delete para ms-gerente (Gerente ID: {})", state.getGerenteId());
                sender.sendSagaCommand(
                    RabbitMQConfig.GERENTE_DELETE_KEY,
                    new SagaCommand<>(state.getGerenteId()),
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

    private void handleGerenteCreated(SagaInstance instance, InsercaoGerenteSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 1 (Gerente) OK.", instance.getCorrelationId());
        
        if (reply.getPayload() instanceof Map) {
             Integer id = (Integer) ((Map<?, ?>) reply.getPayload()).get("id");
             state.setGerenteId(id.longValue());
        }
        sagaService.updateSagaState(instance, SagaStatus.PROCESSING, state);

        ReassingContaGerenteCommand cmd = new ReassingContaGerenteCommand( state.getGerenteId() );
        
        logger.info("SAGA {}: Enviando Passo 2 (Reatribuição de Conta).", instance.getCorrelationId());
        sender.sendSagaCommand(
            RabbitMQConfig.CONTA_REASSIGN_GERENTE_KEY,
            new SagaCommand<>(cmd),
            instance.getCorrelationId()
        );
    }

    private void handleAtribuirConta(SagaInstance instance, InsercaoGerenteSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 2 (Atribuir Conta) OK.", instance.getCorrelationId());

        if (reply.getPayload() instanceof Map) {
             Integer id = (Integer) ((Map<?, ?>) reply.getPayload()).get("id");
             state.setContaId(id.longValue());
        }
        
        sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);
        logger.info("SAGA {} CONCLUÍDO COM SUCESSO.", instance.getCorrelationId());
        
    }
    
}