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

import java.util.Map;

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

        if (reply.isSuccess()) {
            switch (currentState) {
                case STARTED:
                    handleClienteUpdated(instance, state, reply);
                    break;
                
                case PROCESSING: 
                    handleLimiteUpdated(instance, state);
                    break;
                    
                default:
                    logger.warn("SAGA PERFIL [{}]: Resposta ignorada para estado {}", instance.getCorrelationId(), currentState);
            }
        } else {
            handleFailure(instance, reply);
        }
    }

    @Override
    public void handleFailure(SagaInstance instance, SagaReply<?> reply) {
        String errorMsg = reply.getFailureReply() != null ? reply.getFailureReply().getMessage() : "Erro desconhecido";
        
        logger.error("SAGA PERFIL [{}] FALHOU no estado {}. Motivo: {}", 
            instance.getCorrelationId(), instance.getCurrentState(), errorMsg);
    
        sagaService.updateSagaState(instance, SagaStatus.FAILED);
    }

    private void handleClienteUpdated(SagaInstance instance, AlteracaoPerfilSagaState state, SagaReply<?> reply) throws JsonProcessingException {
        logger.info("SAGA PERFIL [{}]: Passo 1 (Cliente Update) OK.", instance.getCorrelationId());
        
        if (reply.getPayload() instanceof Map) {
            Map<?, ?> payloadMap = (Map<?, ?>) reply.getPayload();
            if (payloadMap.containsKey("id")) {
                Number idNum = (Number) payloadMap.get("id");
                state.setClienteId(idNum.longValue());
            }
        }
        if (state.isSalarioAlterado() && state.getClienteId() != null) {
            
            logger.info("SAGA PERFIL [{}]: Salário alterado. ID Cliente: {}. Enviando para Conta.", 
                instance.getCorrelationId(), state.getClienteId());

            sagaService.updateSagaState(instance, SagaStatus.PROCESSING, state);
            

            UpdateLimiteContaCommand contaCmd = new UpdateLimiteContaCommand(
                state.getClienteId(),
                state.getNovoSalario()
            );

            sender.sendSagaCommand(
                RabbitMQConfig.CONTA_UPDATE_LIMITE_KEY,
                new SagaCommand<>(contaCmd),
                instance.getCorrelationId()
            );
            
        } else if (state.isSalarioAlterado() && state.getClienteId()== null) {
            logger.error("SAGA PERFIL: Impossível atualizar conta. ID do cliente não retornado pelo ms-cliente.");
            sagaService.updateSagaState(instance, SagaStatus.FAILED);
        } else {
            logger.info("SAGA PERFIL [{}]: Salário não alterado. Saga Finalizada.", instance.getCorrelationId());
            sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);            
        }
    }

    private void handleLimiteUpdated(SagaInstance instance, AlteracaoPerfilSagaState state) throws JsonProcessingException {
        logger.info("SAGA PERFIL [{}]: Passo 2 (Conta Update Limite) OK. Saga Finalizada com Sucesso.", instance.getCorrelationId());
        sagaService.updateSagaState(instance, SagaStatus.SUCCEEDED, state);
    }
}