package com.bantads.ms_conta.consumer;

import com.bantads.ms_conta.service.command.ContaCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ContaSagaConsumer {
    private final ContaCommandService contaService;

    @RabbitListener(queues = "ms-conta.query.queue")
    public void handleQuery(Message message) {
        String sagaId = (String) message.getMessageProperties().getHeaders().get("sagaId");
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        contaService.mudarGerente(body);
    }
}
