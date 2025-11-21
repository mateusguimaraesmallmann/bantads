package com.bantads.ms_auth.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String AUTH_COMMAND_QUEUE = "auth-command-queue";
    public static final String AUTH_CREATE_KEY = "auth.command.create";
    
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";
    public static final String SAGA_REPLY_KEY = "saga.reply.key";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue authCommandQueue() {
        return new Queue(AUTH_COMMAND_QUEUE, true);
    }

    @Bean
    public Binding bindingAuthCreate(Queue authCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(authCommandQueue).to(sagaExchange).with(AUTH_CREATE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}