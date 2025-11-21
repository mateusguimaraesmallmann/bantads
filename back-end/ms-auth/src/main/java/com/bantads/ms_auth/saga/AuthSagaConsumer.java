package com.bantads.ms_auth.saga;

import com.bantads.ms_auth.configurations.RabbitMQConfig;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.saga.dto.CreateAuthUserCommand;
import com.bantads.ms_auth.saga.dto.SagaCommand;
import com.bantads.ms_auth.saga.dto.SagaReply;
import com.bantads.ms_auth.services.CreateAuthService;
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
    private final CreateAuthService authSagaService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.AUTH_COMMAND_QUEUE)
    public void handleAuthCommand(SagaCommand<CreateAuthUserCommand> commandWrapper, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (RabbitMQConfig.AUTH_CREATE_KEY.equals(routingKey)) {
            handleCreateAuth(commandWrapper.getPayload(), correlationId);
        }
    }

    private void handleCreateAuth(CreateAuthUserCommand command, String correlationId) {
        try {
            logger.info("Recebido comando de criação de Auth para: {}", command.getEmail());

            User usuarioCriado = authSagaService.criarUsuarioInativo(command);

            Map<String, String> payload = Map.of("id", usuarioCriado.getId(), "email", usuarioCriado.getEmail());
            
            SagaReply<Map<String, String>> reply = SagaReply.success(payload);

            enviarResposta(reply, correlationId);
            logger.info("Usuário Auth criado com sucesso. Resposta enviada.");

        } catch (Exception e) {
            logger.error("Erro ao criar usuário Auth: {}", e.getMessage());
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