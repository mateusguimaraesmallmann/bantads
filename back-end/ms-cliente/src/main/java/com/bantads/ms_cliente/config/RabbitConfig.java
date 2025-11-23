package com.bantads.ms_cliente.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    public static final String COMMANDS_EXCHANGE = "saga.exchange";
    public static final String REPLIES_EXCHANGE = "saga.reply.exchange";

    public static final String CLIENTE_COMMAND_QUEUE = "cliente-command-queue";
    
    public static final String CLIENTE_CREATE_KEY = "cliente.command.create";

    public static final String CLIENTE_DELETE_KEY = "cliente.command.delete";
    
    public static final String SAGA_REPLY_KEY = "saga.reply.key";

    public static final String CLIENTE_UPDATE_KEY = "ms-cliente.update-profile"; 
    public static final String CLIENTE_UPDATE_QUEUE = "ms-cliente.update-profile";
    public static final String SAGA_REPLY_QUEUE = "saga.reply.queue";

    @Bean
    public Queue clienteUpdateQueue() {
        return new Queue(CLIENTE_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding bindingClienteUpdate(Queue clienteUpdateQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(clienteUpdateQueue)
                .to(commandsExchange)
                .with(CLIENTE_UPDATE_KEY);
    }
    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(COMMANDS_EXCHANGE);
    }
    
    @Bean
    public Queue clienteCommandQueue() {
        return new Queue(CLIENTE_COMMAND_QUEUE, true);
    }
    
    @Bean
    public Binding clienteCreateBinding(Queue clienteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(clienteCommandQueue)
                .to(commandsExchange)
                .with(CLIENTE_CREATE_KEY);
    }
    
    @Bean
    public Binding clienteDeleteBinding(Queue clienteCommandQueue, DirectExchange commandsExchange) {
        return BindingBuilder
                .bind(clienteCommandQueue)
                .to(commandsExchange)
                .with(CLIENTE_DELETE_KEY);
    }

    @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
            final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(jsonMessageConverter);
            return rabbitTemplate;
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
}