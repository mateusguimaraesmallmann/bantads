package com.bantads.ms_conta.service;

import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.*;
import com.bantads.ms_conta.model.entity.Conta;
import com.bantads.ms_conta.model.entity.Movimentacao;
import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import com.bantads.ms_conta.repository.ContaRepository;
import com.bantads.ms_conta.repository.MovimentacaoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ModelMapper modelMapper;

    public ContaDTOOut criarConta(CriarContaDTOIn contaDTOIn) {
        boolean existe = contaRepository.existsByIdCliente(contaDTOIn.getIdCliente());
        if (existe) {
            throw new EntityExistsException("Conta já cadastrada no banco de dados para esse cliente");
        }

        String numero;
        do {
            numero = gerarNumeroConta();
        } while (contaRepository.existsByNumero(numero));

        Conta conta = new Conta();
        conta.setLimite(contaDTOIn.getLimite());
        conta.setIdGerente(contaDTOIn.getIdGerente());
        conta.setIdCliente(contaDTOIn.getIdCliente());
        conta.setSaldo(contaDTOIn.getSaldoInicial());
        conta.setNumero(numero);
        conta.setDataCriacao(LocalDateTime.now());
        Conta salvo = contaRepository.save(conta);

        return modelMapper.map(salvo, ContaDTOOut.class);
    }

    public String gerarNumeroConta() {
        int numero = 1000 + new Random().nextInt(9000);
        return String.valueOf(numero);
    }

    public SaldoDTOOut buscarSaldo(String numeroConta) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        return new SaldoDTOOut(
                conta.getNumero(),
                conta.getSaldo(),
                conta.getIdCliente());
    }

    @Transactional
    public DepositarSacarDTOOut depositar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        conta.setSaldo(conta.getSaldo().add(depositarSacarDTOIn.getValor()));

        Movimentacao movimentacao = criarMovimentacao(
                conta,
                null,
                conta.getNumero(),
                depositarSacarDTOIn.getValor(),
                TipoMovimentacao.DEPOSITO);

        contaRepository.save(conta);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public DepositarSacarDTOOut sacar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        if (conta.getSaldo().compareTo(depositarSacarDTOIn.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(depositarSacarDTOIn.getValor()));

        Movimentacao movimentacao = criarMovimentacao(
                conta,
                conta.getNumero(),
                null,
                depositarSacarDTOIn.getValor(),
                TipoMovimentacao.SAQUE);

        contaRepository.save(conta);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public TransferirDTOOut transferir(String numeroConta, TransferirDTOIn transferirDTOIn) {
        Conta origem = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta de origem não encontrada"));

        Conta destino = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(transferirDTOIn.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem");
        }

        criarMovimentacao(
                destino,
                origem.getNumero(),
                destino.getNumero(),
                transferirDTOIn.getValor(),
                TipoMovimentacao.TRANSFERENCIA);

        Movimentacao movimentacaoOrigem = criarMovimentacao(
                origem,
                origem.getNumero(),
                destino.getNumero(),
                transferirDTOIn.getValor(),
                TipoMovimentacao.TRANSFERENCIA);

        DepositarSacarDTOIn depositarSacarDTOIn = new DepositarSacarDTOIn(transferirDTOIn.getValor());

        DepositarSacarDTOOut depositarSacarDTOOut = sacar(origem.getNumero(), depositarSacarDTOIn);
        depositar(destino.getNumero(), depositarSacarDTOIn);

        return new TransferirDTOOut(
                movimentacaoOrigem.getContaOrigem(),
                movimentacaoOrigem.getData(),
                movimentacaoOrigem.getContaDestino(),
                depositarSacarDTOOut.getSaldo(),
                movimentacaoOrigem.getValor()
        );
    }

    private Movimentacao criarMovimentacao(
            Conta conta,
            String numeroContaOrigem,
            String numeroContaDestino,
            BigDecimal valor,
            TipoMovimentacao tipo)
    {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta(conta);
        movimentacao.setData(LocalDateTime.now());
        movimentacao.setTipo(tipo);
        movimentacao.setValor(valor);
        movimentacao.setContaOrigem(numeroContaOrigem);
        movimentacao.setContaDestino(numeroContaDestino);

        movimentacaoRepository.save(movimentacao);
        conta.getMovimentacoes().add(movimentacao);
        contaRepository.save(conta);

        return movimentacao;
    }

    public ExtratoDTOOut gerarExtrato(String numeroConta) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        List<MovimentacaoDTOOut> movimentacoes = movimentacaoRepository
                .findByContaId(conta.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ExtratoDTOOut(conta.getNumero(), conta.getSaldo(), movimentacoes);

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

