package com.bantads.ms_gerente.service;

import java.util.List;
import java.util.stream.Collectors;

import com.bantads.ms_gerente.model.dto.output.GerenteRemovidoEvent;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.bantads.ms_gerente.model.dto.input.CriarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.input.EditarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.output.GerenteDTOOut;
import com.bantads.ms_gerente.model.entity.Gerente;
import com.bantads.ms_gerente.repository.GerenteRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class GerenteService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final GerenteRepository gerenteRepository;
    private final ModelMapper modelMapper;

        public GerenteDTOOut criarGerente(CriarGerenteDTOIn gerenteDTO) {
        boolean existe = gerenteRepository.existsByCpf(gerenteDTO.getCpf());
        if (existe) {
            throw new EntityExistsException("Gerente já cadastrado no banco de dados");
        }

        Gerente gerente = modelMapper.map(gerenteDTO, Gerente.class);
        Gerente salvo = gerenteRepository.save(gerente);

        return modelMapper.map(salvo, GerenteDTOOut.class);
    }
    
    public List<GerenteDTOOut> listarTodos() {
        return gerenteRepository.findAll()
                .stream()
                .map(g -> modelMapper.map(g, GerenteDTOOut.class))
                .collect(Collectors.toList());
    }

    public GerenteDTOOut buscarPorCpf(String cpf) {
        Gerente gerente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Gerente não encontrado"));
        return modelMapper.map(gerente, GerenteDTOOut.class);
    }

    public GerenteDTOOut atualizarGerente(String cpf, EditarGerenteDTOIn gerenteDTO) {
        Gerente existente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Gerente não encontrado"));
        modelMapper.map(gerenteDTO, existente);

        Gerente atualizado = gerenteRepository.save(existente);
        return modelMapper.map(atualizado, GerenteDTOOut.class);
    }

    public void removerGerente(String cpf) {
        Gerente gerente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Gerente não encontrado"));

        gerenteRepository.delete(gerente);
        GerenteRemovidoEvent event = new GerenteRemovidoEvent(gerente.getId().toString(), gerente.getNome());
        rabbitTemplate.convertAndSend("GERENTE_REMOVIDO", event);

    }
}
