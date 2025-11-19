package com.bantads.ms_gerente.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    public static final String COMMANDS_EXCHANGE = "saga.exchange";
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";

    public static final String GERENTE_COMMAND_QUEUE = "gerente-command-queue";
    
    public static final String GERENTE_CREATE_KEY = "gerente.command.create";

    public static final String GERENTE_DELETE_KEY = "gerente.command.delete";
    
    public static final String SAGA_REPLY_KEY = "saga.reply.key";


    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(COMMANDS_EXCHANGE);
    }
    
    @Bean
    public Queue gerenteCommandQueue() {
        return new Queue(GERENTE_COMMAND_QUEUE, true);
    }
    
    @Bean
    public Binding gerenteCreateBinding(Queue gerenteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(gerenteCommandQueue)
                .to(commandsExchange)
                .with(GERENTE_CREATE_KEY);
    }
    
    @Bean
    public Binding gerenteDeleteBinding(Queue gerenteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(gerenteCommandQueue)
                .to(commandsExchange)
                .with(GERENTE_DELETE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper rabbitObjectMapper = new ObjectMapper();
        rabbitObjectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
    
}