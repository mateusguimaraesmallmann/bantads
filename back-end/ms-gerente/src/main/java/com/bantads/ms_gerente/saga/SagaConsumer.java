package com.bantads.ms_gerente.saga;

import com.bantads.ms_gerente.config.RabbitConfig;
import com.bantads.ms_gerente.model.dto.input.CriarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.output.GerenteDTOOut;
import com.bantads.ms_gerente.saga.dto.CreateGerenteCommand;
import com.bantads.ms_gerente.saga.dto.SagaCommand;
import com.bantads.ms_gerente.saga.dto.SagaFailureReply;
import com.bantads.ms_gerente.saga.dto.SagaReply;
import com.bantads.ms_gerente.service.GerenteService;

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
    private GerenteService gerenteService;

    @RabbitListener(queues = RabbitConfig.GERENTE_COMMAND_QUEUE)
    public void handleSagaCommand(SagaCommand<CreateGerenteCommand> command, Message message) {
        
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        
        if (RabbitConfig.GERENTE_CREATE_KEY.equals(routingKey)) {
            handleCreateGerente(command, message);
        } else if (RabbitConfig.GERENTE_DELETE_KEY.equals(routingKey)) {
            logger.warn("Comando de rollback (DELETE) recebido, mas ainda não implementado.");
        }
    }
    
    private void handleCreateGerente(SagaCommand<CreateGerenteCommand> command, Message message) {
        
        SagaReply<?> reply;
        String correlationId = message.getMessageProperties().getCorrelationId();
        
        try {
            logger.info("ms-gerente: Comando 'CreateGerente' recebido. CorrelationId: {}", correlationId);
            
            CreateGerenteCommand payload = command.getPayload();

            CriarGerenteDTOIn gerente = new CriarGerenteDTOIn();
            gerente.setCpf(payload.getCpf());
            gerente.setNome(payload.getNome());
            gerente.setEmail(payload.getEmail());
            gerente.setTelefone(payload.getTelefone());

            GerenteDTOOut gerenteCriado = gerenteService.criarGerente(gerente);

            reply = SagaReply.success(gerenteCriado);
            logger.info("ms-gerente: Gerente criado com SUCESSO. Enviando resposta. CorrelationId: {}", correlationId);

        } catch (Exception e) {
            
            logger.error("ms-gerente: FALHA ao criar gerente. Enviando resposta. CorrelationId: {}. Erro: {}", correlationId, e.getMessage());
            SagaFailureReply failure = new SagaFailureReply(
                "ms-gerente",
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