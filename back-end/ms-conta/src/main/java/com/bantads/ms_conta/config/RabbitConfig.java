package com.bantads.ms_conta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //Queue interna para o CQRS
    public static final String QUEUE_NAME = "conta-queue";

    //Exchanges externas para sincronização com o ms-saga
    public static final String COMMANDS_EXCHANGE = "saga.exchange";
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";

    public static final String CONTA_COMMAND_QUEUE = "conta-command-queue";
    
    public static final String CONTA_CREATE_KEY = "conta.command.create";
    public static final String SAGA_REPLY_KEY = "saga.reply.key";   

    @Bean
    public Queue contaQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue contaCommandQueue() {
        return new Queue(CONTA_COMMAND_QUEUE, true);
    }

    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(COMMANDS_EXCHANGE);
    }

    @Bean
    public TopicExchange repliesExchange() {
        return new TopicExchange(REPLIES_EXCHANGE);
    }

    @Bean
    public Binding bindingContaCreate(Queue contaCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(contaCommandQueue)
                .to(commandsExchange)
                .with(CONTA_CREATE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper rabbitObjectMapper = new ObjectMapper();
        rabbitObjectMapper.registerModule(new JavaTimeModule()); 
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
