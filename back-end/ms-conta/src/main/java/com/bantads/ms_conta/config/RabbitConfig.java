package com.bantads.ms_conta.config;

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
    public static final String CONTA_COMMAND_QUEUE = "conta-command-queue";
    public static final String CONTA_REASSIGN_GERENTE_KEY = "conta.command.reassign";
    public static final String SAGA_REPLY_KEY = "saga.reply.key";

    public static final String EVENTS_EXCHANGE = "conta.events.exchange";
    public static final String EVENTS_QUEUE = "conta.events.queue";
    
    public static final String EVENT_CONTA_CREATED = "conta.events.created";
    public static final String EVENT_CONTA_UPDATED = "conta.events.updated";
    public static final String EVENT_MOVIMENTACAO_CREATED = "conta.events.movimentacao.created";

    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(COMMANDS_EXCHANGE, true, false);
    }

    @Bean
    public Queue contaCommandQueue() {
        return QueueBuilder.durable(CONTA_COMMAND_QUEUE)
                .deadLetterRoutingKey("conta.command.dlq")
                .build();
    }

    @Bean
    public Binding contaReassingBinding(Queue contaCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(contaCommandQueue)
                .to(commandsExchange)
                .with(CONTA_REASSIGN_GERENTE_KEY);
    }

    @Bean
    public DirectExchange repliesExchange() {
        return new DirectExchange(REPLIES_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue eventsQueue() {
        return QueueBuilder.durable(EVENTS_QUEUE)
                .deadLetterRoutingKey("conta.events.dlq")
                .build();
    }

    @Bean
    public Binding bindEventsQueue(Queue eventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("conta.*");
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