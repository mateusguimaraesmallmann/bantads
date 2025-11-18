package com.bantads.ms_saga.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.RemocaoGerenteSagaState;
import com.bantads.ms_saga.entity.SagaInstance;

@Service
public class RemocaoGerenteSagaService {

    private static final Logger logger = LoggerFactory.getLogger(RemocaoGerenteSagaService.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;

    public ResponseEntity<?> removerGerente(String cpf) {

        try {
            
			// 1. Criar o estado
            RemocaoGerenteSagaState state = new RemocaoGerenteSagaState(cpf);
            
            // 2. Persistir a instância SAGA
            SagaInstance instance = sagaService.createSaga("REMOCAO_GERENTE", state);
            logger.info("Iniciando SAGA de Remoção do Gerente. CorrelationId: {}", instance.getCorrelationId());

            // 3. Enviar o primeiro comando (Remover Gerente)
            sender.sendSagaCommand(
                RabbitMQConfig.GERENTE_DELETE_KEY,
                new SagaCommand<>(cpf),
                instance.getCorrelationId()
            );

            // 4. Retornar "Accepted"
            return ResponseEntity.accepted().body(Map.of(
                "message", "Sua solicitação de remoção está sendo processada.",
                "correlationId", instance.getCorrelationId()
            ));

        } catch (Exception e) {
            logger.error("Erro ao INICIAR o SAGA de Remoção do Gerente: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro interno ao processar a solicitação.");
        }
    
	}
    
}