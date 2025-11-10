package com.bantads.ms_conta.consumer;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.service.command.ContaCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ContaSagaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ContaSagaConsumer.class);
    private final ContaCommandService contaService;
    private final ObjectMapper objectMapper;

    public ContaSagaConsumer(ObjectMapper objectMapper) {
        this.contaService = null;
        this.objectMapper = objectMapper;
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
            throw new RuntimeException("Erro ao processar mensagem", e);
        }
    }
}