package com.bantads.ms_conta.queue.producer;

import com.bantads.ms_conta.model.dto.input.SalvarContaMongoDTOIn;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.bantads.ms_conta.config.RabbitConfig.QUEUE_NAME;

@Service
@RequiredArgsConstructor
public class ContaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarConta(SalvarContaMongoDTOIn contaDTOIn) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, contaDTOIn);
        System.out.println("Mensagem enviada para RabbitMQ: " + contaDTOIn);
    }
}
