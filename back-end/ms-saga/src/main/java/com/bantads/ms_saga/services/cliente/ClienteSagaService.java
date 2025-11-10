package com.bantads.ms_saga.services.cliente;

import com.bantads.ms_saga.client.ClienteClient;
import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.AprovarClienteDTOOut;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.controllers.autocadastro.ClienteCadastroRequestDTO;
import com.bantads.ms_saga.model.dto.output.GerenteDTOOut;

@Service
@RequiredArgsConstructor
public class ClienteSagaService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteSagaService.class);
    private final ClienteClient clienteClient;

    public AprovarClienteDTOOut aprovarCliente(String cpf) {
        AprovarClienteDTOOut response = new AprovarClienteDTOOut();
        
        try {
            GerenteDTOOut cliente = clienteClient.buscarPorCpf(cpf);
            if (cliente != null) {
                response.setCpf(cpf);
                response.setAprovado(true);
            }
        } catch (Exception e) {
            logger.error("Erro ao aprovar cliente: {}", e.getMessage(), e);
            response.setAprovado(false);
        }

        return response;
    }

    public CadastroClienteDTOIn cadastrarCliente(CadastroClienteDTOIn dto) {
        try {
            return clienteClient.cadastrar(dto);
        } catch (Exception e) {
            logger.error("Erro ao cadastrar cliente: {}", e.getMessage(), e);
            return null;
        }
    }

    public ResponseEntity<?> autoCadastro(ClienteCadastroRequestDTO body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}