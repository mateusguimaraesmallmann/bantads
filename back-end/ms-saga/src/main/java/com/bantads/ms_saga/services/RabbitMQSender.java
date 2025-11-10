package com.bantads.ms_saga.services;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSagaCommand(String routingKey, SagaCommand<?> command, UUID correlationId) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.SAGA_EXCHANGE, 
            routingKey, 
            command,
            message -> {
                // Define o ID de Correlação para rastrear a transação
                message.getMessageProperties().setCorrelationId(correlationId.toString());
                
                // Define para onde o microsserviço deve enviar a RESPOSTA
                message.getMessageProperties().setReplyTo(RabbitMQConfig.SAGA_REPLY_QUEUE);
                return message;
            }
        );
    }
}