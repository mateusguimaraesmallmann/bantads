package com.bantads.ms_cliente.service;

import com.bantads.ms_cliente.model.dto.input.CriarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.output.ClienteDTOOut;
import com.bantads.ms_cliente.model.entity.Cliente;
import com.bantads.ms_cliente.model.enums.StatusCliente;
import com.bantads.ms_cliente.repository.ClienteRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteDTOOut criarCliente(CriarClienteDTOIn clienteDTO) {
        boolean existe = clienteRepository.existsByCpf(clienteDTO.getCpf());
        if (existe) {
            throw new EntityExistsException("Cliente já cadastrado no banco de dados");
        }

        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);
        cliente.setStatus(StatusCliente.EM_ANALISE);
        Cliente salvo = clienteRepository.save(cliente);

        return modelMapper.map(salvo, ClienteDTOOut.class);
    }

    public ClienteDTOOut buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return modelMapper.map(cliente, ClienteDTOOut.class);
    }

    public List<ClienteDTOOut> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, ClienteDTOOut.class))
                .collect(Collectors.toList());
    }

    public ClienteDTOOut atualizarCliente(String cpf, EditarClienteDTOIn clienteDTO) {
        Cliente existente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        modelMapper.map(clienteDTO, existente);

        Cliente atualizado = clienteRepository.save(existente);
        return modelMapper.map(atualizado, ClienteDTOOut.class);
    }

    public Object aprovarCliente(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.getStatus().equals(StatusCliente.EM_ANALISE)) {
            throw new RuntimeException("Cliente não está em análise de aprovação");
        }

        // TODO: Adicionar criação de conta (MS Conta)

        cliente.setStatus(StatusCliente.APROVADO);
        return modelMapper.map(cliente, ClienteDTOOut.class);
    }

    public Object rejeitarCliente(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.getStatus().equals(StatusCliente.EM_ANALISE)) {
            throw new RuntimeException("Cliente não está em análise de aprovação");
        }

        cliente.setStatus(StatusCliente.REJEITADO);

        // TODO: Adicionar motivo da reprovação (MS Conta)
        return new Object();
    }
}

