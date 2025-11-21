package com.bantads.ms_saga.config;

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
public class RabbitMQConfig {
    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String SAGA_REPLY = "saga.reply.exchange";
    public static final String SAGA_REPLY_QUEUE = "saga.reply.queue";

    //#region Routing Keys
    //Clientes
    public static final String CLIENTE_CREATE_KEY = "cliente.command.create";
    public static final String CLIENTE_UPDATE_KEY = "cliente.command.update";
    public static final String CLIENTE_SET_GERENTE_KEY = "cliente.command.set-gerente";
    public static final String CLIENTE_DELETE_KEY = "cliente.command.delete";

    //Auth
    public static final String AUTH_CREATE_KEY = "auth.command.create";
    public static final String AUTH_DELETE_KEY = "auth.command.delete";

    //Conta
    public static final String CONTA_UPDATE_LIMITE_KEY = "conta.command.update-limite";
    public static final String CONTA_REASSIGN_GERENTE_KEY = "conta.command.reassign-gerente";

    //Gerente
    public static final String GERENTE_CREATE_KEY = "gerente.command.create";
    public static final String GERENTE_DELETE_KEY = "gerente.command.delete";
    public static final String GERENTE_FIND_MIN_CLIENTS_KEY = "gerente.command.find-min-clients";

    public static final String SAGA_REPLY_KEY = "saga.reply.key";

    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(SAGA_EXCHANGE);
    }

    @Bean
    public TopicExchange repliesExchange() {
        return new TopicExchange(SAGA_REPLY);
    }

    @Bean
    public Queue sagaReplyQueue() {
        return new Queue(SAGA_REPLY_QUEUE, true);
    }

    @Bean
    public Binding sagaReplyBinding() {
        return BindingBuilder
            .bind(sagaReplyQueue())
            .to(repliesExchange())
            .with(SAGA_REPLY);
    }

    @Bean
    Binding bindContaQuery(Queue msContaQueryQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(msContaQueryQueue)
            .to(sagaExchange)
            .with("ms.conta.query.*");
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