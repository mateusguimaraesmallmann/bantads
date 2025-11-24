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
            clienteClient.checkCpfExists(request.getCpf()); 

            logger.warn("Falha na pré-validação: CPF {} já existe.", request.getCpf());
            return ResponseEntity
                .status(HttpStatus.CONFLICT) 
                .body(Map.of("message", "CPF já cadastrado no sistema.", "cpf", request.getCpf())); 

        } catch (FeignException.NotFound e) {
            logger.info("Etapa 1 (Síncrona): CPF {} não encontrado. Válido para cadastro.", request.getCpf());
            
            try {
                logger.info("Etapa 2 (Assíncrona): Iniciando SAGA de Autocadastro...");
                
                AutocadastroSagaState state = new AutocadastroSagaState(request);
                
                SagaInstance instance = sagaService.createSaga("AUTOCADASTRO", state);

                CreateClientCommand clienteCmd = new CreateClientCommand(
                    request.getCpf(),
                    request.getNome(),
                    request.getEmail(),
                    request.getSalario(),
                    request.getCep(),
                    request.getLogradouro(),
                    request.getNumero(),
                    request.getComplemento(),
                    request.getBairro(),
                    request.getCidade(),
                    request.getEstado()
                );

                sender.sendSagaCommand(
                    RabbitMQConfig.CLIENTE_CREATE_KEY,
                    new SagaCommand<>(clienteCmd),
                    instance.getCorrelationId()
                );

                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "cpf", request.getCpf(), 
                    "email", request.getEmail() 
                ));

            } catch (Exception sagaException) {
                logger.error("Erro ao INICIAR o SAGA de Autocadastro: {}", sagaException.getMessage(), sagaException);
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao iniciar o processo de cadastro."));
            }

        } catch (FeignException e) {
            logger.error("Erro de comunicação com ms-cliente durante a pré-validação: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE) // 503
                .body(Map.of("message", "Serviço de validação indisponível. Tente novamente mais tarde."));
        }
    }
   
}