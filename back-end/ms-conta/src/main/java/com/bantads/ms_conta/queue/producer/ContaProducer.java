package com.bantads.ms_conta.queue.producer;

import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.bantads.ms_conta.config.RabbitConfig.QUEUE_NAME;

//Produtor para sincronização entre os schemas de leitura e comandos do ms-conta
@Service
@RequiredArgsConstructor
public class ContaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarConta(ContaCqrsDTO contaDTOIn) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, contaDTOIn);
        System.out.println("Mensagem enviada para RabbitMQ: " + contaDTOIn);
    }
}
