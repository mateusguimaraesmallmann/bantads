package com.bantads.ms_saga.orchestrators;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.saga.SagaReply;
import com.bantads.ms_saga.dtos.saga.SagaStatus;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.repository.SagaInstanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestrator.class);

    @Autowired private SagaInstanceRepository repository;
    @Autowired private ObjectMapper objectMapper;
    
    @Autowired private AutocadastroSaga autocadastroSaga;
    @Autowired private AlteracaoPerfilSaga alteracaoPerfilSaga;

    @RabbitListener(queues = RabbitMQConfig.SAGA_REPLY_QUEUE)
    public void handleReply(Message message) {
        String correlationIdStr = message.getMessageProperties().getCorrelationId();
        SagaReply<?> reply = null;
        SagaInstance instance = null;

        try {
            reply = objectMapper.readValue(message.getBody(), SagaReply.class);
            
            if (correlationIdStr == null) {
                logger.error("ERRO SAGA: CorrelationId nulo recebido. Payload: {}", new String(message.getBody()));
                return;
            }

            UUID correlationId = UUID.fromString(correlationIdStr);
            
            // 1. Encontrar a instância SAGA no banco de dados
            instance = repository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new RuntimeException("SAGA não encontrado para o CorrelationId: " + correlationId));

            // 2. Verificar se o SAGA já foi concluído (resposta atrasada)
            if (instance.getCurrentState() == SagaStatus.SUCCEEDED ||
                instance.getCurrentState() == SagaStatus.FAILED) {
                logger.warn("SAGA {} já finalizado recebeu uma resposta tardia.", correlationId);
                return;
            }

            // 3. Obter a máquina de estado correta (Autocadastro, Remoção, etc.)
            ISagaStateMachine sagaHandler = getSagaHandler(instance.getSagaType());

            // 4. Delegar a lógica (Sucesso ou Falha/Rollback)
            if (!reply.isSuccess()) {
                logger.warn("SAGA {} falhou no estado {}. Iniciando rollback. Motivo: {}", 
                    correlationId, instance.getCurrentState(), reply.getFailureReply().getMessage());
                sagaHandler.handleFailure(instance, reply);
            } else {
                logger.info("SAGA {} progrediu. Estado anterior: {}", correlationId, instance.getCurrentState());
                sagaHandler.handleReply(instance, reply);
            }

        } catch (Exception e) {
            logger.error("Erro fatal ao processar resposta SAGA [{}]: {}", correlationIdStr, e.getMessage(), e);
            
            // Se algo der muito errado, tentamos reverter o SAGA
            if (instance != null) {
                try {
                    ISagaStateMachine sagaHandler = getSagaHandler(instance.getSagaType());
                    sagaHandler.handleFailure(instance, reply); // Tenta o rollback
                } catch (Exception ex) {
                    logger.error("Não foi possível nem mesmo reverter o SAGA {}: {}", instance.getCorrelationId(), ex.getMessage());
                    instance.setCurrentState(SagaStatus.FAILED); // Marca como falha terminal
                    repository.save(instance);
                }
            }
        }
    }

    private ISagaStateMachine getSagaHandler(String sagaType) {
        switch (sagaType) {
            case "AUTOCADASTRO":
                return autocadastroSaga;
            case "ALTERACAO_PERFIL":
                 return alteracaoPerfilSaga;
            default:
                throw new IllegalArgumentException("Nenhum SAGA handler definido para o tipo: " + sagaType);
        }
    }
}