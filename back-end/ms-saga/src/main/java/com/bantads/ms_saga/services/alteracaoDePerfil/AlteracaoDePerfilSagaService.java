package com.bantads.ms_saga.services.alteracaoDePerfil;

import org.springframework.stereotype.Service;

import com.bantads.ms_saga.client.ClienteClient;
import com.bantads.ms_saga.client.ContaClient;
import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.RecalcularLimiteDTOOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlteracaoDePerfilSagaService {
    
    private final ClienteClient clienteClient;
    private final ContaClient contaClient;
    
    public EditarClienteDTOOut atualizarCliente(String cpf, EditarClienteDTOIn editarCliente) {

        try {
            ClienteDTOOut dadosClienteAntigo = clienteClient.buscarPorCpf(cpf);
            EditarClienteDTOOut clienteAtualizadoDTO = clienteClient.atualizarCliente(cpf, editarCliente);


            if (editarCliente.getSalario() != null && 
                (editarCliente.getSalario().compareTo(dadosClienteAntigo.getSalario()) != 0)) {
                RecalcularLimiteDTOOut dtoRecalcularLimite = new RecalcularLimiteDTOOut(
                    dadosClienteAntigo.getId(), 
                    editarCliente.getSalario()
                );
                contaClient.recalcularLimite(dtoRecalcularLimite); 
            }
            return clienteAtualizadoDTO;

        } catch (Exception e) {
            return null;
        } 
    }

}
