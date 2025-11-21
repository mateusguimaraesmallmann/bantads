package com.bantads.ms_conta.saga;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.entity.command.Conta;
import com.bantads.ms_conta.saga.dto.ReassingContaGerenteCommand;
import com.bantads.ms_conta.saga.dto.SagaCommand;
import com.bantads.ms_conta.saga.dto.SagaFailureReply;
import com.bantads.ms_conta.saga.dto.SagaReply;
import com.bantads.ms_conta.service.ContaCommandService;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SagaConsumer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ContaCommandService contaCommandService;

    @RabbitListener(queues = RabbitConfig.CONTA_COMMAND_QUEUE)
    public void handleSagaCommand(SagaCommand<ReassingContaGerenteCommand> command, Message message) {
        
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        
        if (RabbitConfig.CONTA_REASSIGN_GERENTE_KEY.equals(routingKey)) {
            handleReassingContaGerente(command, message);
        }
    
    }

    private void handleReassingContaGerente(SagaCommand<ReassingContaGerenteCommand> command, Message message) {
        
        SagaReply<?> reply;
        String correlationId = message.getMessageProperties().getCorrelationId();
        
        try {
            logger.info("ms-conta: Comando 'ReassingContaGerente' recebido. CorrelationId: {}", correlationId);
            
            ReassingContaGerenteCommand payload = command.getPayload();

            Optional<Conta> contaReatribuida = contaCommandService.reatribuirContaParaNovoGerente(payload.getGerenteId());

            reply = SagaReply.success(contaReatribuida);
            logger.info("ms-conta: Conta atribuida com SUCESSO. Enviando resposta. CorrelationId: {}", correlationId);

        } catch (Exception e) {
            
            logger.error("ms-conta: FALHA ao atribuir conta para gerente. Enviando resposta. CorrelationId: {}. Erro: {}", correlationId, e.getMessage());
            SagaFailureReply failure = new SagaFailureReply(
                "ms-conta",
                e.getMessage(),
                e.getClass().getSimpleName()
            );
            reply = SagaReply.failure(failure);
            
        }

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