package com.bantads.ms_gerente.consumer;

import com.bantads.ms_gerente.config.RabbitConfig;
import com.bantads.ms_gerente.model.dto.GerenteCargaDTO;
import com.bantads.ms_gerente.model.entity.Gerente;
import com.bantads.ms_gerente.repository.GerenteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GerenteAssignConsumer {

    private final Logger logger = Logger.getLogger(GerenteAssignConsumer.class.getName());
    private final GerenteRepository gerenteRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.GERENTE_ASSIGNMENT_QUEUE)
    public Long selecionarMelhorGerente(List<?>cargaAtualRaw) {
        try{
            // Se ainda não houverem gerentes, retorna null
            List<Gerente> todosGerentes = gerenteRepository.findAll();
            logger.info("Recebida solicitação de atribuição de gerente.");
            if (todosGerentes.isEmpty()) {
                logger.warning("Nenhum gerente cadastrado no banco!");
                return null;
            }

            Map<Long, Long> mapaCarga = cargaAtualRaw.stream()
                .map(item -> objectMapper.convertValue(item, GerenteCargaDTO.class))
                .collect(Collectors.toMap(
                    GerenteCargaDTO::getIdGerente, 
                    GerenteCargaDTO::getQuantidadeContas
                ));

            // Bate os dados recebidos do ms-conta com os gerentes cadastrados
            // Atribui o gerente com menos clientes, se houver empate, escolhe por ordem alfabética
            Gerente idGerente = todosGerentes.stream()
                    .sorted(Comparator
                            .comparingLong((Gerente g) -> mapaCarga.getOrDefault(g.getId(), 0L))
                            .thenComparing(Gerente::getNome))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Erro ao selecionar gerente"));
            return idGerente.getId();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar atribuição de gerente: " + e.getMessage());
        }

    }
}