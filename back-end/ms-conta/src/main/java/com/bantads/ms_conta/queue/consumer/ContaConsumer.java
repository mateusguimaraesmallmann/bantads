package com.bantads.ms_conta.queue.consumer;

import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import com.bantads.ms_conta.service.query.ContaQueryService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ContaConsumer {

    private final ContaQueryService contaQueryService;

    @RabbitListener(queues = "conta-queue")
    public void consumirConta(ContaCqrsDTO contaDTOIn) {
        System.out.println("Mensagem recebida do RabbitMQ: " + contaDTOIn);

        contaQueryService.salvarConta(contaDTOIn);
    }
}
