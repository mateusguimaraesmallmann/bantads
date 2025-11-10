package com.bantads.ms_saga.orchestrators;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.commands.UpdateLimiteContaCommand;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.saga.SagaReply;
import com.bantads.ms_saga.dtos.saga.SagaStatus;
import com.bantads.ms_saga.dtos.state.AlteracaoPerfilSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.services.RabbitMQSender;
import com.bantads.ms_saga.services.SagaInstanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlteracaoPerfilSaga implements ISagaStateMachine {

    private static final Logger logger = LoggerFactory.getLogger(AlteracaoPerfilSaga.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;
    @Autowired private ObjectMapper objectMapper;

    @Override
    public void handleReply(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        AlteracaoPerfilSagaState state = objectMapper.readValue(instance.getPayload(), AlteracaoPerfilSagaState.class);
        SagaStatus currentState = instance.getCurrentState();

        switch (currentState) {
            case STARTED:
                handleClienteUpdated(instance, state, reply);
                break;
            
            case PROCESSING: 
                handleLimiteUpdated(instance, state, reply);
                break;
        }
    }

    @Override
    public void handleFailure(SagaInstance instance, SagaReply<?> reply) throws JsonProcessingException {
        logger.error("SAGA {} FALHOU: {}", instance.getCorrelationId(), reply.getFailureReply().getMessage());
        sagaService.updateSagaState(instance, SagaStatus.FAILED);
    }


    private void handleClienteUpdated(SagaInstance instance, AlteracaoPerfilSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 1 (Cliente Update) OK.", instance.getCorrelationId());
        
        if (state.isSalarioAlterado()) {
            sagaService.updateSagaState(instance, SagaStatus.PROCESSING, state);
            
            UpdateLimiteContaCommand contaCmd = new UpdateLimiteContaCommand(
                state.getCpf(),
                state.getNovoSalario()
            );

            logger.info("SAGA {}: Salário alterado. Enviando Passo 2 (Conta Update Limite).", instance.getCorrelationId());
            sender.sendSagaCommand(
                RabbitMQConfig.CONTA_UPDATE_LIMITE_KEY,
                new SagaCommand<>(contaCmd),
                instance.getCorrelationId()
            );
            
        } else {
            logger.info("SAGA {}: Salário não alterado. SAGA CONCLUÍDO.", instance.getCorrelationId());
            sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);
        }
    }

    private void handleLimiteUpdated(SagaInstance instance, AlteracaoPerfilSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA {}: Passo 2 (Conta Update Limite) OK.", instance.getCorrelationId());
        sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);
        logger.info("SAGA {} CONCLUÍDO COM SUCESSO.", instance.getCorrelationId());
    }
}