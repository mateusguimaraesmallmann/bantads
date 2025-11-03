package com.bantads.ms_saga.services.alteracaoDePerfil;

import org.springframework.stereotype.Service;

import com.bantads.ms_saga.client.ClienteClient;
import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.GerenteDTOOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlteracaoDePerfilSagaService {
    
    private final ClienteClient clienteClient;
    //private final ContaClient contaClient;
    
    public EditarClienteDTOOut atualizarCliente(String cpf, EditarClienteDTOIn editarCliente) {
        EditarClienteDTOOut editarClienteDTO = new EditarClienteDTOOut();
        try {
            //GerenteDTOOut dadosAtuaisCliente =  clienteClient.buscarPorCpf(cpf);
            editarClienteDTO = clienteClient.atualizarCliente(cpf, editarCliente);
            // if (editarCliente.getSalario() != null && editarCliente.getSalario() != dadosAtuaisCliente.getSalario()) {

            //     RecalcularLimiteDTOIn recalculoDTO = new RecalcularLimiteDTOIn(cpf, editarCliente.getSalario());
            //     contaClient.recalcularLimite(recalculoDTO); 
                
            // }
            return editarClienteDTO;
        } catch (Exception e) {
            return editarClienteDTO;
        }
    }
 
}
