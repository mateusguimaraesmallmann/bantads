package com.bantads.ms_saga.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.controllers.AutocadastroSagaController;
import com.bantads.ms_saga.dtos.commands.CreateClientCommand;
import com.bantads.ms_saga.dtos.request.AutocadastroRequest;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.AutocadastroSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.feign.cliente.ClienteClient;

import feign.FeignException;

@Service
public class AutocadastroSagaService {

    private static final Logger logger = LoggerFactory.getLogger(AutocadastroSagaController.class);

    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;
    @Autowired private ClienteClient clienteClient;

    public ResponseEntity<?> autoCadastro(AutocadastroRequest request) {
        
        try {
            logger.info("Etapa 1 (Síncrona): Verificando CPF {}", request.getCpf());
            clienteClient.checkCpfExists(request.getCpf()); //

            logger.warn("Falha na pré-validação: CPF {} já existe.", request.getCpf());
            return ResponseEntity
                .status(HttpStatus.CONFLICT) 
                .body("CPF já cadastrado no sistema."); 

        } catch (FeignException.NotFound e) {
            logger.info("Etapa 1 (Síncrona): CPF {} não encontrado. Válido para cadastro.", request.getCpf());
            
            try {
                logger.info("Etapa 2 (Assíncrona): Iniciando SAGA de Autocadastro...");
                // 1. Criar o estado inicial do SAGA 
                AutocadastroSagaState state = new AutocadastroSagaState(request);
                
                // 2. Persistir a instância do SAGA no banco 
                SagaInstance instance = sagaService.createSaga("AUTOCADASTRO", state);
                logger.info("SAGA de Autocadastro. CorrelationId: {}", instance.getCorrelationId());

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

                // 5. Retornar "Created" 
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Sua solicitação de cadastro foi recebida e está sendo processada.",
                    "correlationId", instance.getCorrelationId()
                ));

            } catch (Exception sagaException) {
                // Erro ao TENTAR INICIAR o saga (ex: RabbitMQ down, DB do Saga down)
                logger.error("Erro ao INICIAR o SAGA de Autocadastro: {}", sagaException.getMessage(), sagaException);
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao iniciar o processo de cadastro.");
            }

        } catch (FeignException e) {
            // Outro erro do Feign (ex: ms-cliente está offline)
            logger.error("Erro de comunicação com ms-cliente durante a pré-validação: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE) // 503
                .body("Serviço de validação indisponível. Tente novamente mais tarde.");
        }
    }
   
}