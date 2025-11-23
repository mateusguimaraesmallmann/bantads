package com.bantads.ms_conta.consumer;

import com.bantads.ms_conta.service.command.ContaCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClienteAprovadoConsumer {

    private final ContaCommandService contaCommandService;

    @RabbitListener(queues = "cliente-aprovado")
    public void receberAprovacao(@Payload Map<String, Object> payload) {
        Long idCliente = ((Number) payload.get("idCliente")).longValue();
        String nome = (String) payload.get("nome");
        String email = (String) payload.get("email");

        contaCommandService.ativarConta(idCliente, nome, email);
    }
}