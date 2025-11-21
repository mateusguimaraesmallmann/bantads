package com.bantads.ms_conta.saga;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.saga.dto.CreateContaCommand;
import com.bantads.ms_conta.saga.dto.SagaCommand;
import com.bantads.ms_conta.saga.dto.SagaFailureReply;
import com.bantads.ms_conta.saga.dto.SagaReply;
import com.bantads.ms_conta.service.command.ContaCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContaSagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ContaSagaConsumer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ContaCommandService contaService;

    @RabbitListener(queues = RabbitConfig.CONTA_COMMAND_QUEUE)
    public void handleSagaCommand(SagaCommand<CreateContaCommand> command, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String correlationId = message.getMessageProperties().getCorrelationId();

        if (RabbitConfig.CONTA_CREATE_KEY.equals(routingKey)) {
            handleCreateConta(command.getPayload(), correlationId);
        }
    }

    private void handleCreateConta(CreateContaCommand payload, String correlationId) {
        SagaReply<?> reply;
        
        try {
            logger.info("ms-conta: Recebido comando CREATE_CONTA. Cliente ID: {}", payload.getIdCliente());
            
            ContaCriadaDTOOut contaCriada = contaService.criarConta(payload);
            
            reply = SagaReply.success(contaCriada);
            logger.info("ms-conta: Conta criada/atualizada com sucesso. Enviando reply.");

        } catch (Exception e) {
            logger.error("ms-conta: Erro ao processar criação de conta: {}", e.getMessage());
            reply = SagaReply.failure(new SagaFailureReply(
                "ms-conta", 
                e.getMessage(), 
                e.getClass().getSimpleName()
            ));
        }

        sendReply(reply, correlationId);
    }

    private void sendReply(SagaReply<?> reply, String correlationId) {
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