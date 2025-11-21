package com.bantads.ms_conta.service;

import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.ExtratoDTOOut;
import com.bantads.ms_conta.model.dto.output.MovimentacaoDTOOut;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.model.entity.query.ContaRead;
import com.bantads.ms_conta.model.entity.query.MovimentacaoRead;
import com.bantads.ms_conta.repository.query.ContaReadRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaQueryService {

    private final ContaReadRepository contaReadRepository;

    public Optional<ContaRead> findByNumero(String numero) {
        return contaReadRepository.findByNumero(numero);
    }

    public SaldoDTOOut buscarSaldo(Long numeroConta) {
        ContaRead conta = contaReadRepository.findByNumero(String.valueOf(numeroConta))
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        return new SaldoDTOOut(
            Long.parseLong(conta.getNumero()),
            conta.getSaldo(),
            conta.getClienteId().longValue());
    }

    public ExtratoDTOOut buscarExtrato(Long numeroConta) {
        ContaRead conta = contaReadRepository.findByNumero(String.valueOf(numeroConta))
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        List<MovimentacaoDTOOut> movimentacoes = conta.getMovimentacoes()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());

        return new ExtratoDTOOut(
            Long.parseLong(conta.getNumero()), 
            conta.getSaldo(), 
            movimentacoes);
    }

    public void salvarConta(ContaRead contaRead) {
        contaReadRepository.save(contaRead);
    }

    public List<ContaPorGerenteDTOOut> contarContasPorGerente(){
        return contaReadRepository.countContasByGerente();
    }

    private MovimentacaoDTOOut mapToDTO(MovimentacaoRead movimentacao) {
        return new MovimentacaoDTOOut(
                movimentacao.getData().toLocalDateTime(),
                movimentacao.getTipo(),
                movimentacao.getValor(),
                String.valueOf(movimentacao.getContaOrigem()),
                String.valueOf(movimentacao.getContaDestino())
        );
    }

}