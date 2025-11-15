// package com.bantads.ms_saga.producer;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.core.MessageProperties;
// import org.springframework.amqp.core.TopicExchange;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.stereotype.Service;

// import java.nio.charset.StandardCharsets;

// @Service
// @RequiredArgsConstructor
// public class SagaProducer {
//     private final RabbitTemplate rabbitTemplate;
//     private final TopicExchange sagaExchange;

//     public void sendCommand(String routingKey, Object payload, String sagaId) {
//         MessageProperties props = new MessageProperties();
//         props.setHeader("sagaId", sagaId);
//         props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
//         Message msg = null;
//         try {
//             String json = new ObjectMapper().writeValueAsString(payload);
//             msg = new Message(json.getBytes(StandardCharsets.UTF_8), props);
//             rabbitTemplate.send(sagaExchange.getName(), routingKey, msg);
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException(e);
//         }
//     }
// }