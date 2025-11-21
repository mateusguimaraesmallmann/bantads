package com.bantads.ms_gerente.consumer;

import com.bantads.ms_gerente.config.RabbitConfig;
import com.bantads.ms_gerente.model.dto.GerenteCargaDTO;
import com.bantads.ms_gerente.model.entity.Gerente;
import com.bantads.ms_gerente.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GerenteAssignConsumer {

    private final GerenteRepository gerenteRepository;

    @RabbitListener(queues = RabbitConfig.GERENTE_ASSIGNMENT_QUEUE)
    public Long selecionarMelhorGerente(List<GerenteCargaDTO> cargaAtual) {
        
        List<Gerente> todosGerentes = gerenteRepository.findAll();
        
        // Se ainda não houverem gerentes, retorna null
        if (todosGerentes.isEmpty()) {
            return null;
        }

        // Bate os dados recebidos do ms-conta com os gerentes cadastrados
        Map<Long, Long> mapaCarga = cargaAtual.stream()
                .collect(Collectors.toMap(GerenteCargaDTO::getIdGerente, GerenteCargaDTO::getQuantidadeContas));

        Gerente idGerente = todosGerentes.stream()
                .sorted(Comparator
                        .comparingLong((Gerente g) -> mapaCarga.getOrDefault(g.getId(), 0L))
                        .thenComparing(Gerente::getNome))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erro ao selecionar gerente"));
        return idGerente.getId();
    }
}