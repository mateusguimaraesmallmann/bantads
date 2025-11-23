package com.bantads.ms_saga.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.dtos.saga.DadosClienteResponseDTO;
import com.bantads.ms_saga.feign.cliente.ClienteClient;
import com.bantads.ms_saga.feign.conta.ContaClient;
import com.bantads.ms_saga.feign.gerente.GerenteClient;
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.ContaResponse;
import com.bantads.ms_saga.model.dto.output.GerenteResponse;

import feign.FeignException;

@Service
public class ClienteSagaService {

    @Autowired private ClienteClient clienteClient;
    @Autowired private ContaClient contaClient;
    @Autowired private GerenteClient gerenteClient;

    public DadosClienteResponseDTO montarClienteCompleto(String cpf) {
        DadosClienteResponseDTO dto = new DadosClienteResponseDTO();
        ClienteDTOOut cliente = clienteClient.buscarPorCpf(cpf);
        
        dto.setNome(cliente.getNome());
        dto.setCpf(cliente.getCpf());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setSalario(cliente.getSalario());
        if (cliente.getEndereco() != null) {
            dto.setCep(cliente.getEndereco().getCep());
            dto.setLogradouro(cliente.getEndereco().getLogradouro());
            dto.setNumero(cliente.getEndereco().getNumero());
            dto.setComplemento(cliente.getEndereco().getComplemento());
            dto.setCidade(cliente.getEndereco().getCidade());
            dto.setEstado(cliente.getEndereco().getEstado());
            dto.setBairro(cliente.getEndereco().getBairro());
        }
        try {
            ContaResponse conta = contaClient.buscarPorIdCliente(cliente.getId());
            
            dto.setNumeroConta(conta.getNumero());
            dto.setSaldo(conta.getSaldo());
            dto.setLimite(conta.getLimite());

                try {
                    GerenteResponse gerente = gerenteClient.buscarPorId(conta.getIdGerente());
                    dto.setNomeGerente(gerente.getNome());
                    dto.setEmailGerente(gerente.getEmail());
                    dto.setCpfGerente(gerente.getCpf());
                } catch (FeignException e) {
                    dto.setNomeGerente("Gerente não encontrado ou serviço indisponível");
                }
        } catch (FeignException.NotFound e) {
            dto.setNumeroConta("Conta não localizada");
        }
        return dto;
    }
}