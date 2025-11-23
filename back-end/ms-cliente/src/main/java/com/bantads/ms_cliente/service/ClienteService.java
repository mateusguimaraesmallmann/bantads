package com.bantads.ms_cliente.service;

import com.bantads.ms_cliente.feign.ContaClient;
import com.bantads.ms_cliente.feign.dto.ContaCriadaDTOOut;
import com.bantads.ms_cliente.feign.dto.CriarContaDTOIn;
import com.bantads.ms_cliente.model.dto.input.CriarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.output.ClienteAprovadoDTOOut;
import com.bantads.ms_cliente.model.dto.output.ClienteDTOOut;
import com.bantads.ms_cliente.model.entity.Cliente;
import com.bantads.ms_cliente.model.enums.StatusCliente;
import com.bantads.ms_cliente.repository.ClienteRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final ContaClient contaClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public ClienteDTOOut criarClientePorSaga(Cliente cliente) {
        boolean existe = clienteRepository.existsByCpf(cliente.getCpf());
        if (existe) {
            throw new EntityExistsException("Cliente já cadastrado no banco de dados com este CPF.");
        }

        cliente.setStatus(StatusCliente.EM_ANALISE);
        Cliente salvo = clienteRepository.save(cliente);

        return modelMapper.map(salvo, ClienteDTOOut.class);
    }

    public ClienteDTOOut buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return modelMapper.map(cliente, ClienteDTOOut.class);
    }

    public ClienteDTOOut buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado!"));
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

    @Transactional
    public ClienteAprovadoDTOOut aprovarCliente(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        cliente.setStatus(StatusCliente.APROVADO);
        clienteRepository.save(cliente);

        Map<String, Object> evento = new HashMap<>();
        evento.put("idCliente", cliente.getId());
        evento.put("nome", cliente.getNome());
        evento.put("email", cliente.getEmail());

        rabbitTemplate.convertAndSend("cliente-aprovado", evento);
        return modelMapper.map(cliente, ClienteAprovadoDTOOut.class);
    }

    private BigDecimal calcularLimite(BigDecimal salario) {
        return salario.compareTo(BigDecimal.valueOf(2000.00)) >= 0
                        ? salario.divide(BigDecimal.valueOf(2))
                        : BigDecimal.ZERO;
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

    public boolean cpfExists(String cpf) {
        return clienteRepository.existsByCpf(cpf);
    }
}

