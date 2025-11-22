package com.bantads.ms_auth.saga;

import com.bantads.ms_auth.configurations.RabbitMQConfig;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.saga.dto.CreateAuthUserCommand;
import com.bantads.ms_auth.saga.dto.SagaCommand;
import com.bantads.ms_auth.saga.dto.SagaReply;
import com.bantads.ms_auth.services.CreateAuthService;
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
public class AuthSagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuthSagaConsumer.class);
    private final CreateAuthService createAuthService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.AUTH_COMMAND_QUEUE)
    public void handleAuthCommand(SagaCommand<CreateAuthUserCommand> commandWrapper, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (RabbitMQConfig.AUTH_CREATE_KEY.equals(routingKey)) {
            handleCreateAuth(commandWrapper, correlationId);
        }
    }

    private void handleCreateAuth(SagaCommand<?> commandWrapper, String correlationId) {
        try {
            Object payloadRaw = commandWrapper.getPayload();
            CreateAuthUserCommand command;

            if (payloadRaw instanceof Map) {
                command = objectMapper.convertValue(payloadRaw, CreateAuthUserCommand.class);
            } else if (payloadRaw instanceof CreateAuthUserCommand) {
                command = (CreateAuthUserCommand) payloadRaw;
            } else {
                throw new IllegalArgumentException("Payload inválido: " + payloadRaw.getClass());
            }

            logger.info("AuthSagaConsumer: Recebido comando para criar conta: {}", command.getEmail());

            User usuarioCriado = createAuthService.criarUsuarioInativo(command);

            Map<String, String> payload = Map.of(
                "id", usuarioCriado.getId(), 
                "email", usuarioCriado.getEmail()
            );
            
            SagaReply<Map<String, String>> reply = SagaReply.success(payload);
            enviarResposta(reply, correlationId);
            
            logger.info("AuthSagaConsumer: Conta criada/atualizada. ID: {}", usuarioCriado.getId());

        } catch (Exception e) {
            logger.error("AuthSagaConsumer: Erro ao processar criação: {}", e.getMessage());
            SagaReply<Object> reply = SagaReply.failure(e.getMessage());
            enviarResposta(reply, correlationId);
        }
    }

    private void enviarResposta(SagaReply<?> reply, String correlationId) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.REPLIES_EXCHANGE,
            RabbitMQConfig.SAGA_REPLY_KEY,
            reply,
            msg -> {
                msg.getMessageProperties().setCorrelationId(correlationId);
                return msg;
            }
        );
    }
}