package com.bantads.ms_conta.service.query;

import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import com.bantads.ms_conta.repository.read.ContaLeituraRepository;
import com.bantads.ms_conta.model.entity.read.ContaLeitura;
import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.ExtratoDTOOut;
import com.bantads.ms_conta.model.dto.output.MovimentacaoDTOOut;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.model.entity.mongo.Conta;
import com.bantads.ms_conta.model.entity.mongo.Movimentacao;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaQueryService {

    private final ContaLeituraRepository contaLeituraRepository;
    private final ModelMapper modelMapper;

    public SaldoDTOOut buscarSaldo(Long numeroConta) {
        ContaLeitura conta = contaLeituraRepository.findByNumero(String.valueOf(numeroConta))
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));


        return new SaldoDTOOut(
                conta.getNumero(),
                conta.getSaldo(),
                conta.getIdCliente());
    }

    //TODO: Revisar ContaLeitura para incluir movimentações
    // public ExtratoDTOOut buscarExtrato(Long numeroConta) {
    //     ContaLeitura conta = contaLeituraRepository.findByNumero(numeroConta)
    //             .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

    //     List<MovimentacaoDTOOut> movimentacoes = conta.getMovimentacoes()
    //             .stream()
    //             .map(this::mapToDTO)
    //             .collect(Collectors.toList());

    //     return new ExtratoDTOOut(conta.getNumero(), conta.getSaldo(), movimentacoes);

    // }

    public void salvarConta(ContaCqrsDTO contaDTOIn) {
        ContaLeitura conta = new ContaLeitura();
        conta.setIdCliente(contaDTOIn.getIdCliente());
        conta.setIdGerente(contaDTOIn.getIdGerente());
        conta.setNumero(contaDTOIn.getNumero());
        conta.setSaldo(contaDTOIn.getSaldo());
        conta.setLimite(contaDTOIn.getLimite());
        conta.setStatus(contaDTOIn.getStatus());
        conta.setDataCriacao(contaDTOIn.getDataCriacao());
        conta.setMotivoReprovacao(contaDTOIn.getMotivoReprovacao());
        contaLeituraRepository.save(conta);
    }

    //TODO: Ajustar repository para retornar as contas por gerente
    // public List<ContaPorGerenteDTOOut> contarContasPorGerente(){
    //     return contaLeituraRepository.countContasByGerente();
    // }

    private MovimentacaoDTOOut mapToDTO(Movimentacao movimentacao) {
        return new MovimentacaoDTOOut(
                movimentacao.getData(),
                movimentacao.getTipo().name(),
                movimentacao.getValor(),
                movimentacao.getContaOrigem(),
                movimentacao.getContaDestino()
        );
    }

    public ContaLeitura buscarInfosContaPorIdCliente(Long idCliente){
        return contaLeituraRepository.findByIdCliente(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("usuario não encontrado"));            
    }
}

