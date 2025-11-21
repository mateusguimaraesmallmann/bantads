package com.bantads.ms_saga.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.commands.CreateGerenteCommand;
import com.bantads.ms_saga.dtos.request.CadastroGerenteRequest;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.InsercaoGerenteSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.feign.GerenteFeign.GerenteClient;

import feign.FeignException;

@Service
public class InsercaoGerenteSagaService {

    private static final Logger logger = LoggerFactory.getLogger(InsercaoGerenteSagaService.class);

    @Autowired
    private SagaInstanceService sagaService;
    @Autowired
    private RabbitMQSender sender;
    @Autowired
    private GerenteClient gerenteClient;

    public ResponseEntity<?> cadastrarGerente(CadastroGerenteRequest request) {

        try {
            logger.info("Etapa 1 (Síncrona): Verificando CPF {}", request.getCpf());
            gerenteClient.checkCpfExists(request.getCpf()); //

            logger.warn("Falha na pré-validação: CPF {} já existe.", request.getCpf());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado no sistema.");

        } catch (FeignException.NotFound e) {
            logger.info("Etapa 1 (Síncrona): CPF {} não encontrado. Válido para cadastro.", request.getCpf());

            try {
                logger.info("Etapa 2 (Assíncrona): Iniciando SAGA de Cadastro do Gerente...");

                InsercaoGerenteSagaState state = new InsercaoGerenteSagaState(request);

                SagaInstance instance = sagaService.createSaga("CADASTRO_GERENTE", state);
                logger.info("SAGA de Cadastro do Gerente. CorrelationId: {}", instance.getCorrelationId());

                CreateGerenteCommand gerenteCmd = new CreateGerenteCommand(
                        request.getCpf(),
                        request.getNome(),
                        request.getEmail(),
                        request.getTelefone());

                sender.sendSagaCommand(
                        RabbitMQConfig.GERENTE_CREATE_KEY,
                        new SagaCommand<>(gerenteCmd),
                        instance.getCorrelationId());

                return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                        "message", "Cadastro do gerente está sendo processado.",
                        "correlationId", instance.getCorrelationId()));

            } catch (Exception sagaException) {
                logger.error("Erro ao INICIAR o SAGA de Cadastro do Gerente: {}", sagaException.getMessage(), sagaException);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro interno ao iniciar o processo de cadastro.");
            }

        } catch (FeignException e) {
            logger.error("Erro de comunicação com ms-gerente durante a pré-validação: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Serviço de validação indisponível. Tente novamente mais tarde.");

        }
    }

}