package com.bantads.ms_gerente.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String GERENTE_ASSIGNMENT_QUEUE = "gerente-assignment-queue";
    public static final String GERENTE_COMMAND_QUEUE = "gerente-command-queue";

    public static final String COMMANDS_EXCHANGE = "saga.exchange";
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";

    public static final String GERENTE_CREATE_KEY = "gerente.command.create";
    public static final String GERENTE_DELETE_KEY = "gerente.command.delete";
    public static final String SAGA_REPLY_KEY = "saga.reply.key";

    @Bean
    public Queue gerenteAssignmentQueue() {
        return new Queue(GERENTE_ASSIGNMENT_QUEUE, true);
    }

    @Bean
    public Queue gerenteCommandQueue() {
        return new Queue(GERENTE_COMMAND_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper rabbitObjectMapper = new ObjectMapper();
        // Ignora propriedades desconhecidas
        rabbitObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
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
    public Binding bindingGerenteCreate(Queue gerenteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
            .bind(gerenteCommandQueue)
            .to(commandsExchange)
            .with(GERENTE_CREATE_KEY);
    }

    @Bean
    public Binding bindingGerenteDelete(Queue gerenteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
            .bind(gerenteCommandQueue)
            .to(commandsExchange)
            .with(GERENTE_DELETE_KEY);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}