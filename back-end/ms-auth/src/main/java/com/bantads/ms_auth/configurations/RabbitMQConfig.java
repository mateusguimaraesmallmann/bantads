package com.bantads.ms_auth.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
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
    public Queue updateAuthPasswordQueue() {
        return new Queue("auth-update-password");
    }

    @Bean
    public Binding bindingAuthCreate(Queue authCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(authCommandQueue).to(sagaExchange).with(AUTH_CREATE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper rabbitObjectMapper = new ObjectMapper();
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(rabbitObjectMapper);
        converter.setClassMapper(new DefaultJackson2JavaTypeMapper() {
            @Override
            public Class<?> toClass(MessageProperties properties) {
                properties.getHeaders().remove("__TypeId__");
                return com.bantads.ms_auth.saga.dto.SagaCommand.class; 
            }
        });
        
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}