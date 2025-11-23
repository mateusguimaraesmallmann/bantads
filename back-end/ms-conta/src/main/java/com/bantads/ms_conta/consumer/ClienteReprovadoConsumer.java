package com.bantads.ms_conta.consumer;

import com.bantads.ms_conta.service.command.ContaCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClienteReprovadoConsumer {
    
    private final ContaCommandService contaCommandService;

    @RabbitListener(queues = "cliente-rejeitado")
    public void clienteRejeitado(@Payload Map<String, Object> payload) {
        Long idCliente = ((Number) payload.get("idCliente")).longValue();
        String nome = (String) payload.get("nome");
        String email = (String) payload.get("email");
        String motivoReprovacao = (String) payload.get("motivoReprovacao");

        contaCommandService.desativarConta(idCliente, nome, email, motivoReprovacao);
    }
    
}
