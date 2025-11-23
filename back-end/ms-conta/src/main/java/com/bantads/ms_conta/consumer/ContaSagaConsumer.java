package com.bantads.ms_conta.consumer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bantads.ms_conta.saga.dto.ContaSyncDTO;
import com.bantads.ms_conta.saga.dto.SagaFailureReply;
import com.bantads.ms_conta.saga.dto.SagaReply;
import com.bantads.ms_conta.saga.dto.UpdateLimiteCommand;
import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.model.entity.read.ContaLeitura;
import com.bantads.ms_conta.repository.read.ContaLeituraRepository;
import com.bantads.ms_conta.service.command.ContaCommandService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ContaSagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ContaSagaConsumer.class);
    
    private final ContaCommandService contaService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired private ContaLeituraRepository leituraRepository;

    public ContaSagaConsumer(ContaCommandService contaService, 
                             ObjectMapper objectMapper,
                             RabbitTemplate rabbitTemplate) {
        this.contaService = contaService; 
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "ms-conta.query.queue")
    public void handleQuery(Message message) {
        try {
            String sagaId = (String) message.getMessageProperties().getHeaders().get("sagaId");
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            
            MudarGerenteDTOIn dtoIn = objectMapper.readValue(messageBody, MudarGerenteDTOIn.class);
            
            contaService.mudarGerente(dtoIn);
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitConfig.CONTA_UPDATE_LIMITE_QUEUE)
    public void receberPedidoAtualizacao(
            Message message, 
            @Header(name = AmqpHeaders.CORRELATION_ID, required = false) String correlationIdStr) {
        
        logger.info("CONSUMER CONTA: Recebido comando de update limite. ID: {}", correlationIdStr);
        SagaReply<String> reply;

        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode rootNode = objectMapper.readTree(body);
            System.err.println(body);
            System.err.println(rootNode);
            JsonNode dadosNode = rootNode;
            if (rootNode.has("payload")) {
                dadosNode = rootNode.get("payload");
                if (dadosNode.isTextual()) {
                    dadosNode = objectMapper.readTree(dadosNode.asText());
                }
            }
            UpdateLimiteCommand request = new UpdateLimiteCommand();
            if (dadosNode.has("idCliente")) request.setIdCLiente(dadosNode.get("idCliente").asLong());
            if (dadosNode.has("novoSalario") && !dadosNode.get("novoSalario").isNull()) {
                request.setNovoSalario(new BigDecimal(dadosNode.get("novoSalario").asText()));
            }

            contaService.atualizarLimitePorSalario(request.getIdCLiente(), request.getNovoSalario());

            reply = SagaReply.success("Limite atualizado com sucesso");
            logger.info("Limite atualizado com sucesso.");
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar limite: {}", e.getMessage(), e);
            reply = SagaReply.failure(new SagaFailureReply("ms-conta", "erro", e.getMessage()));
        }

        if (correlationIdStr != null) {
            enviarResposta(reply, correlationIdStr);
        } else {
            logger.warn("Sem correlation_id, não é possível responder.");
        }
    }

    private void enviarResposta(SagaReply<?> reply, String correlationId) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitConfig.SAGA_REPLY_QUEUE, 
                reply, 
                m -> {
                    m.getMessageProperties().setCorrelationId(correlationId);
                    return m;
                }
            );
        } catch (Exception e) {
            logger.error("Erro ao enviar resposta", e);
        }
    }

    @RabbitListener(queues = RabbitConfig.CONTA_SYNC_QUEUE)
    public void sincronizarConta(Message message) { 
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            ContaSyncDTO dto = objectMapper.readValue(body, ContaSyncDTO.class);
            logger.info("SYNC: Recebida atualização para conta do Cliente ID: {}", dto.getIdCliente());
            ContaLeitura contaLeitura = leituraRepository.findByIdCliente(dto.getIdCliente())
                    .orElse(new ContaLeitura()); 
            contaLeitura.setIdCliente(dto.getIdCliente());
            contaLeitura.setIdGerente(dto.getIdGerente());
            contaLeitura.setNumero(dto.getNumero());
            contaLeitura.setDataCriacao(dto.getDataCriacao());
            contaLeitura.setSaldo(dto.getSaldo());
            contaLeitura.setLimite(dto.getLimite()); 
            contaLeitura.setStatus(dto.getStatus().toString()); 
            
            leituraRepository.save(contaLeitura);
            
            logger.info("SYNC: Conta de leitura sincronizada com sucesso.");
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }    
}