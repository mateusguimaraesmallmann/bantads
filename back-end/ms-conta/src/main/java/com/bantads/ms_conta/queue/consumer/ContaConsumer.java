package com.bantads.ms_conta.queue.consumer;

import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import com.bantads.ms_conta.service.query.ContaQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import java.util.Map;

import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ContaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ContaConsumer.class);
    private final ContaQueryService contaQueryService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "conta-queue")
    public void consumirConta(Message message) {
        try{
            ContaCqrsDTO contaDto = objectMapper.readValue(message.getBody(), ContaCqrsDTO.class);

            System.out.println("Consumindo conta criada: " + contaDto.getIdComando());
            contaQueryService.salvarConta(contaDto);
        } catch (Exception e){
            System.err.println("Erro ao desserializar mensagem CQRS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
