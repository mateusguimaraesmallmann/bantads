package com.bantads.ms_saga.services.gerente;

import com.bantads.ms_saga.client.GerenteClient;
import com.bantads.ms_saga.model.dto.input.RemoverGerenteDTOIn;
import com.bantads.ms_saga.model.dto.output.RemoverGerenteDTOOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GerenteSagaService {

    private final GerenteClient gerenteClient;
    // private final KafkaProducerService producer;

    public RemoverGerenteDTOOut removerGerente(RemoverGerenteDTOIn dto) {
        RemoverGerenteDTOOut resultado = new RemoverGerenteDTOOut();
        resultado.setCpf(dto.getCpf());

        try {
            gerenteClient.buscarPorCpf(dto.getCpf());

            // producer.enviarEventoRemocaoGerente(dto.getCpf());

            gerenteClient.removerPorCpf(dto.getCpf());

            resultado.setStatus("SUCESSO");
            resultado.setMensagem("Gerente removido com sucesso");
        } catch (Exception e) {
            resultado.setStatus("FALHA");
            resultado.setMensagem("Erro ao remover gerente: " + e.getMessage());
        }

        return resultado;
    }
}

