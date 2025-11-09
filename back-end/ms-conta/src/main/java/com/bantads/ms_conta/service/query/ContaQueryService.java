package com.bantads.ms_conta.service.query;

import com.bantads.ms_conta.model.dto.input.SalvarContaMongoDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.ExtratoDTOOut;
import com.bantads.ms_conta.model.dto.output.MovimentacaoDTOOut;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.model.entity.mongo.Conta;
import com.bantads.ms_conta.model.entity.mongo.Movimentacao;
import com.bantads.ms_conta.repository.mongo.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaQueryService {

    private final ContaRepository contaRepository;
    private final ModelMapper modelMapper;

    public SaldoDTOOut buscarSaldo(Long numeroConta) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));


        return new SaldoDTOOut(
                conta.getNumero(),
                conta.getSaldo(),
                conta.getIdCliente());
    }

    public ExtratoDTOOut buscarExtrato(Long numeroConta) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        List<MovimentacaoDTOOut> movimentacoes = conta.getMovimentacoes()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ExtratoDTOOut(conta.getNumero(), conta.getSaldo(), movimentacoes);

    }

    public void salvarConta(SalvarContaMongoDTOIn contaDTOIn) {
        Conta conta = new Conta(contaDTOIn);
        contaRepository.save(conta);
    }

    public List<ContaPorGerenteDTOOut> contarContasPorGerente(){
        return contaRepository.countContasByGerente();
    }

    private MovimentacaoDTOOut mapToDTO(Movimentacao movimentacao) {
        return new MovimentacaoDTOOut(
                movimentacao.getData(),
                movimentacao.getTipo().name(),
                movimentacao.getValor(),
                movimentacao.getContaOrigem(),
                movimentacao.getContaDestino()
        );
    }

}

