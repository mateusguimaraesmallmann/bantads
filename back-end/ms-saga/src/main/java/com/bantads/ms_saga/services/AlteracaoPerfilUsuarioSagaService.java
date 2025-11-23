package com.bantads.ms_saga.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.AlteracaoPerfilSagaState;
import com.bantads.ms_saga.entity.SagaInstance;

@Service
public class AlteracaoPerfilUsuarioSagaService {

    private static final Logger logger = LoggerFactory.getLogger(AlteracaoPerfilUsuarioSagaService.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;

    public ResponseEntity<?> alterarPerfil(String cpf, AlteracaoPerfilRequest request) {
    
        try {
            AlteracaoPerfilSagaState state = new AlteracaoPerfilSagaState(cpf, request);
            SagaInstance instance = sagaService.createSaga("ALTERACAO_PERFIL", state);
            request.setCpf(cpf);
            logger.info("Iniciando SAGA de Alteração de Perfil. CorrelationId: {}", instance.getCorrelationId());
            
            sender.sendSagaCommand(
                RabbitMQConfig.CLIENTE_UPDATE_KEY,
                new SagaCommand<>(request),
                instance.getCorrelationId()
            );

            return ResponseEntity.accepted().body(Map.of(
                "message", "Sua solicitação de alteração está sendo processada.",
                "correlationId", instance.getCorrelationId()
            ));

        }catch (Exception e) {
            logger.error("Erro ao INICIAR o SAGA de Alteração de Perfil: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro interno ao processar a solicitação.");
        }
    }

}