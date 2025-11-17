package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.dtos.request.AutocadastroRequest;
import com.bantads.ms_saga.dtos.state.AutocadastroSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.services.RabbitMQSender;
import com.bantads.ms_saga.services.SagaInstanceService;
import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.commands.CreateClientCommand;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/saga/autocadastro")
public class AutocadastroSagaController {

    private static final Logger logger = LoggerFactory.getLogger(AutocadastroSagaController.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;

    @PostMapping
    public ResponseEntity<?> autoCadastro(@RequestBody AutocadastroRequest request) {
        
        try {
            // 1. Criar o estado inicial do SAGA 
            AutocadastroSagaState state = new AutocadastroSagaState(request);
            
            // 2. Persistir a instância do SAGA no banco 
            SagaInstance instance = sagaService.createSaga("AUTOCADASTRO", state);
            logger.info("Iniciando SAGA de Autocadastro. CorrelationId: {}", instance.getCorrelationId());

            // 3. Criar o primeiro comando (Criar Cliente)
            CreateClientCommand clienteCmd = new CreateClientCommand(
                request.getCpf(),
                request.getNome(),
                request.getEmail(),
                //request.getTelefone(),
                request.getSalario(),
                request.getCep(),
                request.getLogradouro(),
                request.getNumero(),
                request.getComplemento(),
                request.getBairro(),
                request.getCidade(),
                request.getEstado()
            );

            // 4. Enviar o primeiro comando ao RabbitMQ
            sender.sendSagaCommand(
                RabbitMQConfig.CLIENTE_CREATE_KEY,
                new SagaCommand<>(clienteCmd),
                instance.getCorrelationId()
            );

            // 5. Retornar "Accepted" imediatamente 
            return ResponseEntity.accepted().body(Map.of(
                "message", "Sua solicitação de cadastro foi recebida e está sendo processada.",
                "correlationId", instance.getCorrelationId()
            ));

        } catch (Exception e) {
            logger.error("Erro ao INICIAR o SAGA de Autocadastro: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro interno ao processar a solicitação.");
        }
    }
}