package com.bantads.ms_saga.services.cliente;

import com.bantads.ms_saga.client.GerenteClient;
import com.bantads.ms_saga.model.dto.input.RemoverGerenteDTOIn;
import com.bantads.ms_saga.model.dto.output.RemoverGerenteDTOOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteSagaService {

    private final ClienteClient ClienteClient;

    public AprovarClienteDTOOut aprovarCliente(String cpf) {
        
        AprovarClienteDTOOut response = new AprovarClienteDTOOut();
        
        try {
            ClienteClient.buscarPorCpf(dto.getCpf());

            response.setCpf(cpf);        

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public CadastroClienteDTOIn cadastrarCliente(CadastroClienteDTOIn dto) {
        
        CadastroClienteDTOIn response = new CadastroClienteDTOIn();
        
        try {
            ClienteClient.buscarPorCpf(dto.getCpf());

            response.setCpf(cpf);      

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    
}