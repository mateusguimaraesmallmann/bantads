package com.bantads.ms_conta.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.output.ContaCreatedEvent;
import com.bantads.ms_conta.model.dto.output.ContaUpdatedEvent;
import com.bantads.ms_conta.model.dto.output.MovimentacaoCreatedEvent;
import com.bantads.ms_conta.model.entity.query.ContaRead;
import com.bantads.ms_conta.model.entity.query.MovimentacaoRead;
import com.bantads.ms_conta.saga.dto.SagaCommand;
import com.bantads.ms_conta.service.ContaQueryService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.amqp.core.Message;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class ContaEventsListener {

    private static final Logger logger = LoggerFactory.getLogger(ContaEventsListener.class);
    private final ContaQueryService contaQueryService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.EVENTS_QUEUE)
    public void handleEvent(String body, Message message) throws Exception {
        
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (RabbitConfig.EVENT_CONTA_CREATED.equals(routingKey)) {
            handleContaCreatedEvent(body);
        } else if(RabbitConfig.EVENT_CONTA_UPDATED.equals(routingKey)) {
            handleContaUpdatedEvent(body);
        } else if(RabbitConfig.EVENT_MOVIMENTACAO_CREATED.equals(routingKey)) {
            handleMovimentacaoCreatedEvent(body);
        }

    }

    private void handleContaCreatedEvent(String body) throws Exception {
        logger.info("Processando evento: conta.created");
        var event = objectMapper.readValue(body, ContaCreatedEvent.class);

        ContaRead contaRead = new ContaRead();
        contaRead.setId(event.getId());
        contaRead.setNumero(event.getNumero());
        contaRead.setClienteId(event.getIdCliente());
        contaRead.setGerenteId(event.getIdGerente());
        contaRead.setSaldo(event.getSaldo());
        contaRead.setLimite(event.getLimite());

        if (event instanceof ContaCreatedEvent && ((ContaCreatedEvent) event).getDataCriacao() != null) {
            LocalDateTime ldt = ((ContaCreatedEvent) event).getDataCriacao();
            contaRead.setDataCriacao(java.time.OffsetDateTime.of(ldt, java.time.ZoneOffset.UTC));
        } else {
            contaRead.setDataCriacao(java.time.OffsetDateTime.now());
        }

        contaQueryService.salvarConta(contaRead);
        logger.info("Conta criada na base de leitura: {}", event.getNumero());
    }

    private void handleContaUpdatedEvent(String body) throws Exception {
        logger.info("Processando evento: conta.updated");
        var event = objectMapper.readValue(body, ContaUpdatedEvent.class);

        var opt = contaQueryService.findByNumero(event.getNumero());
        if (opt.isPresent()) {
            var conta = opt.get();
            conta.setGerenteId(event.getGerenteId());
            contaQueryService.salvarConta(conta);
        }
        logger.info("Conta atualizada na base de leitura: {}", event.getNumero());
    }

    private void handleMovimentacaoCreatedEvent(String body) throws Exception {
        logger.info("Processando evento: movimentacao.created");
        var event = objectMapper.readValue(body, MovimentacaoCreatedEvent.class);

        OffsetDateTime odt = event.getData() != null ? OffsetDateTime.of(event.getData(), ZoneOffset.UTC) : OffsetDateTime.now();

        var opt = contaQueryService.findByNumero(event.getNumeroConta());
        if (opt.isPresent()) {
            ContaRead conta = opt.get();
            
            MovimentacaoRead mov = new MovimentacaoRead();
            mov.setId(event.getId());
            mov.setData(odt);
            mov.setTipo(event.getTipo());
            mov.setValor(event.getValor());
            mov.setContaOrigem(event.getContaOrigem());
            mov.setContaDestino(event.getContaDestino());
            mov.setContaRead(conta);
            
            conta.getMovimentacoes().add(mov);

            contaQueryService.salvarConta(conta);
        }
        logger.info("Movimentação criada na base de leitura: {}", event.getTipo());
    }
    
}