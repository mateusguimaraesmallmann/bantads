package com.bantads.ms_cliente.saga;

import com.bantads.ms_cliente.config.RabbitConfig;
import com.bantads.ms_cliente.model.dto.output.ClienteDTOOut;
import com.bantads.ms_cliente.model.entity.Cliente;
import com.bantads.ms_cliente.saga.dto.CreateClientCommand;
import com.bantads.ms_cliente.saga.dto.SagaCommand;
import com.bantads.ms_cliente.saga.dto.SagaFailureReply;
import com.bantads.ms_cliente.saga.dto.SagaReply;
import com.bantads.ms_cliente.service.ClienteService;
import org.modelmapper.ModelMapper;
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
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;


    @RabbitListener(queues = RabbitConfig.CLIENTE_COMMAND_QUEUE)
    public void handleSagaCommand(SagaCommand<CreateClientCommand> command, Message message) {
        
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        
        if (RabbitConfig.CLIENTE_CREATE_KEY.equals(routingKey)) {
            handleCreateCliente(command, message);
        } else if (RabbitConfig.CLIENTE_DELETE_KEY.equals(routingKey)) {
            // TODO: handleRollbackDeleteCliente(command, message)
            logger.warn("Comando de rollback (DELETE) recebido, mas ainda não implementado.");
        }
    }
    
    private void handleCreateCliente(SagaCommand<CreateClientCommand> command, Message message) {
        
        SagaReply<?> reply;
        String correlationId = message.getMessageProperties().getCorrelationId();
        
        try {
            logger.info("ms-cliente: Comando 'CreateCliente' recebido. CorrelationId: {}", correlationId);
            
            CreateClientCommand payload = command.getPayload();
            
            Cliente cliente = modelMapper.map(payload, Cliente.class);
            
            ClienteDTOOut clienteCriado = clienteService.criarClientePorSaga(cliente);

            reply = SagaReply.success(clienteCriado);
            logger.info("ms-cliente: Cliente criado com SUCESSO. Enviando resposta. CorrelationId: {}", correlationId);

        } catch (Exception e) {
            logger.error("ms-cliente: FALHA ao criar cliente. Enviando resposta. CorrelationId: {}. Erro: {}", correlationId, e.getMessage());
            SagaFailureReply failure = new SagaFailureReply(
                "ms-cliente",
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