package com.bantads.ms_conta.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "conta-queue";
    public static final String CONTA_COMMAND_QUEUE = "conta-command-queue"; 
    public static final String GERENTE_ASSIGNMENT_QUEUE = "gerente-assignment-queue"; 
    public static final String CONTA_QUERY_QUEUE = "ms-conta.query.queue";

    public static final String COMMANDS_EXCHANGE = "saga.exchange";
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";
    
    public static final String CONTA_CREATE_KEY = "conta.command.create";
    public static final String SAGA_REPLY_KEY = "saga.reply.key";
    
    public static final String CONTA_UPDATE_LIMITE_QUEUE = "ms-conta.update-limite";
    public static final String CONTA_UPDATE_LIMITE_KEY = "ms-conta.update-limite";
    public static final String SAGA_REPLY_QUEUE = "saga.reply.queue";

    @Bean
    public Queue contaQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue contaCommandQueue() {
        return new Queue(CONTA_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue gerenteAssignmentQueue() {
        return new Queue(GERENTE_ASSIGNMENT_QUEUE, true); 
    }

    @Bean
    public Queue contaQueryQueue() {
        return new Queue(CONTA_QUERY_QUEUE, true);
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
        rabbitObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(rabbitObjectMapper);

        converter.setClassMapper(new DefaultJackson2JavaTypeMapper() {
            @Override
            public Class<?> toClass(MessageProperties properties) {
                properties.getHeaders().remove("__TypeId__");
                return Object.class;
            }
        });

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setReplyTimeout(5000);
        return rabbitTemplate;
    }
    @Bean
    public Queue contaUpdateLimiteQueue() {
        return new Queue(CONTA_UPDATE_LIMITE_QUEUE, true);
    }
    @Bean
    public Binding bindingContaUpdateLimite(Queue contaUpdateLimiteQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(contaUpdateLimiteQueue)
                .to(commandsExchange)
                .with(CONTA_UPDATE_LIMITE_KEY);
    }    
}