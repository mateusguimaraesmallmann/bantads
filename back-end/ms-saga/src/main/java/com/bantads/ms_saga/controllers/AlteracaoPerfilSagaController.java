package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.AlteracaoPerfilSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.services.RabbitMQSender;
import com.bantads.ms_saga.services.SagaInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/saga/alteracao-perfil")
public class AlteracaoPerfilSagaController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlteracaoPerfilSagaController.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;


    @PutMapping("/{cpf}")
    public ResponseEntity<?> alterarPerfil(@PathVariable String cpf, 
                                           @RequestBody AlteracaoPerfilRequest request) {
        
        BigDecimal salarioAntigo = null; 

        try {
            // 1. Criar o estado
            AlteracaoPerfilSagaState state = new AlteracaoPerfilSagaState(cpf, request, salarioAntigo);
            
            // 2. Persistir a instância SAGA
            SagaInstance instance = sagaService.createSaga("ALTERACAO_PERFIL", state);
            logger.info("Iniciando SAGA de Alteração de Perfil. CorrelationId: {}", instance.getCorrelationId());

            // 3. Enviar o primeiro comando (Atualizar Cliente)
            sender.sendSagaCommand(
                RabbitMQConfig.CLIENTE_UPDATE_KEY,
                new SagaCommand<>(request),
                instance.getCorrelationId()
            );

            // 4. Retornar "Accepted"
            return ResponseEntity.accepted().body(Map.of(
                "message", "Sua solicitação de alteração está sendo processada.",
                "correlationId", instance.getCorrelationId()
            ));

        } catch (Exception e) {
            logger.error("Erro ao INICIAR o SAGA de Alteração de Perfil: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro interno ao processar a solicitação.");
        }
    }
}