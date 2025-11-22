package com.bantads.ms_conta.saga;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.saga.dto.CreateContaCommand;
import com.bantads.ms_conta.saga.dto.SagaCommand;
import com.bantads.ms_conta.saga.dto.SagaFailureReply;
import com.bantads.ms_conta.saga.dto.SagaReply;
import com.bantads.ms_conta.service.command.ContaCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SagaConsumer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ContaCommandService contaService;
    private final ObjectMapper objectMapper; 

    @RabbitListener(queues = RabbitConfig.CONTA_COMMAND_QUEUE)
    public void handleSagaCommand(Message message) {
        try {
            // Extração manual de headers
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            String correlationId = message.getMessageProperties().getCorrelationId();

            // Conversão manual do JSON para SagaCommand
            SagaCommand<?> commandWrapper = objectMapper.readValue(message.getBody(), SagaCommand.class);

            if (RabbitConfig.CONTA_CREATE_KEY.equals(routingKey)) {
                handleCreateConta(commandWrapper, correlationId);
            }
        } catch (Exception e) {
            logger.error("Erro fatal ao ler mensagem da Saga: {}", e.getMessage());
            // Não conseguimos nem ler o correlationId com segurança se o JSON estiver quebrado,
            // então apenas logamos o erro.
        }
    }

    private void handleCreateConta(SagaCommand<?> commandWrapper, String correlationId) {
        SagaReply<?> reply;
        try {
            Object payloadRaw = commandWrapper.getPayload();
            CreateContaCommand command;

            if (payloadRaw instanceof Map) {
                command = objectMapper.convertValue(payloadRaw, CreateContaCommand.class);
            } else if (payloadRaw instanceof CreateContaCommand) {
                command = (CreateContaCommand) payloadRaw;
            } else {
                throw new IllegalArgumentException("Tipo de payload inesperado: " + payloadRaw.getClass().getName());
            }

            logger.info("ms-conta: Recebido comando CREATE_CONTA. Cliente ID: {}", command.getIdCliente());
            
            ContaCriadaDTOOut contaCriada = contaService.criarConta(command);
            
            reply = SagaReply.success(contaCriada);
            logger.info("ms-conta: Conta criada com sucesso.");

        } catch (Exception e) {
            logger.error("ms-conta: Erro ao processar conta: {}", e.getMessage());
            reply = SagaReply.failure(new SagaFailureReply("ms-conta", e.getMessage(), e.getClass().getSimpleName()));
        }

        enviarResposta(reply, correlationId);
    }

    private void enviarResposta(SagaReply<?> reply, String correlationId) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.REPLIES_EXCHANGE,
            RabbitConfig.SAGA_REPLY_KEY,
            reply,
            msg -> {
                msg.getMessageProperties().setCorrelationId(correlationId);
                return msg;
            }
        );
    }
}