package com.bantads.ms_cliente.consumer;

import com.bantads.ms_cliente.config.RabbitConfig;
import com.bantads.ms_cliente.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.output.EnderecoDTOOut;
import com.bantads.ms_cliente.model.dto.saga.SagaReply;
import com.bantads.ms_cliente.saga.dto.SagaFailureReply;
import com.bantads.ms_cliente.service.ClienteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClienteSagaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClienteSagaConsumer.class);

    @Autowired private ClienteService clienteService;
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.CLIENTE_UPDATE_QUEUE)
    public void handleUpdateProfile(
            Message message, 
            @Header(AmqpHeaders.CORRELATION_ID) String correlationIdStr) {
        
        logger.info("CONSUMER: Recebido comando de update. CorrelationID: {}", correlationIdStr);
        SagaReply<Map<String, Object>> resposta;

        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode rootNode = objectMapper.readTree(body);
            JsonNode dadosNode = rootNode; 

            if (rootNode.has("payload")) {
                logger.info("Payload detectado. Desembrulhando...");
                dadosNode = rootNode.get("payload");
                
                if (dadosNode.isTextual()) {
                    dadosNode = objectMapper.readTree(dadosNode.asText());
                }
            }

            EditarClienteDTOIn request = new EditarClienteDTOIn();
            if (dadosNode.has("cpf")) request.setCpf(dadosNode.get("cpf").asText());
            if (dadosNode.has("nome")) request.setNome(dadosNode.get("nome").asText());
            if (dadosNode.has("email")) request.setEmail(dadosNode.get("email").asText());
            if (dadosNode.has("telefone")) request.setTelefone(dadosNode.get("telefone").asText());
            
            if (dadosNode.has("salario") && !dadosNode.get("salario").isNull()) {
                request.setSalario(new BigDecimal(dadosNode.get("salario").asText()));
            }

            if (dadosNode.has("logradouro") || dadosNode.has("cep")) {
                EnderecoDTOOut endereco = new EnderecoDTOOut();
                if (dadosNode.has("logradouro")) endereco.setLogradouro(dadosNode.get("logradouro").asText());
                if (dadosNode.has("numero")) endereco.setNumero(dadosNode.get("numero").asText());
                if (dadosNode.has("complemento")) endereco.setComplemento(dadosNode.get("complemento").asText());
                if (dadosNode.has("bairro")) endereco.setBairro(dadosNode.get("bairro").asText());
                if (dadosNode.has("cidade")) endereco.setCidade(dadosNode.get("cidade").asText());
                if (dadosNode.has("estado")) endereco.setEstado(dadosNode.get("estado").asText());
                if (dadosNode.has("cep")) endereco.setCep(dadosNode.get("cep").asText());
                if (dadosNode.has("tipoLogradouro")) endereco.setTipoLogradouro(dadosNode.get("tipoLogradouro").asText());

                request.setEndereco(endereco);
            }

            logger.info("Atualizando CPF: {}", request.getCpf());
            Long idCliente = clienteService.atualizarPerfil(request.getCpf(), request);
            Map<String, Object> payloadResposta = new HashMap<>();
            payloadResposta.put("message", "Perfil atualizado com sucesso");
            payloadResposta.put("id", idCliente); // <--- O ID QUE O SAGA QUER
            resposta = SagaReply.success(payloadResposta);
            
            logger.info("Cliente {} atualizado. Retornando ID: {}", request.getCpf(), idCliente);
            enviarResposta(resposta, correlationIdStr);

        } catch (Exception e) {
            logger.error("Erro ao atualizar cliente: {}", e.getMessage(), e);
            resposta = SagaReply.failure(new SagaFailureReply("ms-cliente", "erro saga", e.getMessage()));
        }
        enviarResposta(resposta, correlationIdStr);
    }

    private void enviarResposta(SagaReply<?> reply, String correlationId) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitConfig.SAGA_REPLY_QUEUE, 
                reply, 
                m -> {
                    m.getMessageProperties().setCorrelationId(correlationId);
                    return m;
                }
            );
        } catch (Exception e) {
            logger.error("Erro ao enviar resposta", e);
        }
    }
}