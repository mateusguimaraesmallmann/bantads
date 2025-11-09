package com.bantads.ms_conta.service.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_conta.model.dto.input.SalvarContaMongoDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.RecalcularLimiteDTOOut;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.model.entity.jpa.Conta;
import com.bantads.ms_conta.model.entity.jpa.Movimentacao;
import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import com.bantads.ms_conta.queue.producer.ContaProducer;
import com.bantads.ms_conta.repository.jpa.ContaJpaRepository;
import com.bantads.ms_conta.repository.jpa.MovimentacaoJpaRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaCommandService {

    private final ContaJpaRepository contaJpaRepository;
    private final MovimentacaoJpaRepository movimentacaoJpaRepository;
    private final ContaProducer contaProducer;
    private final ModelMapper modelMapper;

    public ContaCriadaDTOOut criarConta(CriarContaDTOIn contaDTOIn) {
        boolean existe = contaJpaRepository.existsByIdCliente(contaDTOIn.getIdCliente());
        if (existe) {
            throw new EntityExistsException("Conta já cadastrada no banco de dados para esse cliente");
        }

        String numero;
        do {
            numero = gerarNumeroConta();
        } while (contaJpaRepository.existsByNumero(numero));

        Conta conta = new Conta(contaDTOIn);
        conta.setNumero(numero);

        Conta salvo = contaJpaRepository.save(conta);

        enviarContaParaFilaMongo(contaDTOIn, numero);

        return modelMapper.map(salvo, ContaCriadaDTOOut.class);
    }

    private void enviarContaParaFilaMongo(CriarContaDTOIn contaDTOIn, String numero) {
        SalvarContaMongoDTOIn salvarContaMongoDTOIn = new SalvarContaMongoDTOIn(
                contaDTOIn.getSaldoInicial(),
                Long.parseLong(numero),
                contaDTOIn.getIdCliente());
        contaProducer.enviarConta(salvarContaMongoDTOIn);
    }

    public String gerarNumeroConta() {
        int numero = 1000 + new Random().nextInt(9000);
        return String.valueOf(numero);
    }

    @Transactional
    public DepositarSacarDTOOut depositar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaJpaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        conta.setSaldo(conta.getSaldo().add(depositarSacarDTOIn.getValor()));

        Movimentacao movimentacao = criarMovimentacao(
                conta,
                null,
                conta.getNumero(),
                depositarSacarDTOIn.getValor(),
                TipoMovimentacao.DEPOSITO);

        contaJpaRepository.save(conta);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public DepositarSacarDTOOut sacar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaJpaRepository.findByNumero(numeroConta)
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

        contaJpaRepository.save(conta);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public TransferirDTOOut transferir(String numeroConta, TransferirDTOIn transferirDTOIn) {
        Conta origem = contaJpaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta de origem não encontrada"));

        Conta destino = contaJpaRepository.findByNumero(numeroConta)
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

    public void mudarGerente(MudarGerenteDTOIn command) {
        Optional<Conta> conta = contaJpaRepository.findByNumero(command.getNumeroConta());
        if (conta.isPresent() && !conta.get().getIdGerente().equals(command.getGerenteId())) {
            conta.get().setIdGerente(command.getGerenteId());
            contaJpaRepository.save(conta.get());
        }
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

        movimentacaoJpaRepository.save(movimentacao);
        conta.getMovimentacoes().add(movimentacao);
        contaJpaRepository.save(conta);

        return movimentacao;
    }

    public RecalcularLimiteDTOOut recalcularLimite(RecalcularLimiteDTOIn dto){
        final BigDecimal salarioMinimoParaLimite = new BigDecimal("2000.00");

        Conta conta = contaJpaRepository.findByIdCliente(dto.getIdDoCliente())
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
            
        BigDecimal novoLimiteCalculado;
        if (dto.getNovoSalario().compareTo(salarioMinimoParaLimite) > -1) { 
            novoLimiteCalculado = dto.getNovoSalario().divide(new BigDecimal("2"));
        } else {
            novoLimiteCalculado = BigDecimal.ZERO;
        }

        BigDecimal limiteFinal = novoLimiteCalculado; 
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) == -1) { 
            BigDecimal saldoNegativo = conta.getSaldo().abs(); 
            if (novoLimiteCalculado.compareTo(conta.getSaldo()) < 0) {
                limiteFinal = saldoNegativo;
            }
        }
        conta.setLimite(limiteFinal);
        return new RecalcularLimiteDTOOut(conta.getIdGerente(), conta.getLimite(), conta.getSaldo());
    }

}

