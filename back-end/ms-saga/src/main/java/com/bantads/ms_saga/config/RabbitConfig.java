package com.bantads.ms_saga.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String QUEUE_RPL_AUTH_LOGIN = "ms-auth.reply.login"; 
    public static final String QUEUE_RPL_AUTH_CLIENTE = "ms-cliente.reply.login"; 

    @Bean
    TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    Queue msContaQueryQueue() {
        return QueueBuilder.durable("ms-conta.query.queue")
                .withArgument("x-dead-letter-exchange", "saga.dlq")
                .build();
    }

    @Bean
    Binding bindContaQuery(Queue msContaQueryQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(msContaQueryQueue)
                .to(sagaExchange)
                .with("ms.conta.query.*");
    }
}