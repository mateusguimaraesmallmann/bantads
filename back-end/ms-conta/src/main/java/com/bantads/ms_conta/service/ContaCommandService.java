package com.bantads.ms_conta.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.event.EventProducer;
import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_conta.model.dto.input.SalvarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaCreatedEvent;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.ContaUpdatedEvent;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.GerenteSaldoProjection;
import com.bantads.ms_conta.model.dto.output.MovimentacaoCreatedEvent;
import com.bantads.ms_conta.model.dto.output.RecalcularLimiteDTOOut;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.model.entity.command.Conta;
import com.bantads.ms_conta.model.entity.command.Movimentacao;
import com.bantads.ms_conta.model.entity.query.ContaRead;
import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import com.bantads.ms_conta.repository.command.ContaCommandRepository;
import com.bantads.ms_conta.repository.command.MovimentacaoRepository;
import com.bantads.ms_conta.repository.query.ContaReadRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaCommandService {

    private final ContaCommandRepository contaCommandRepository;
    private final ContaReadRepository contaReadRepository;
    private final MovimentacaoRepository movimentacaoJpaRepository;
    private final EventProducer eventProducer;
    private final RabbitConfig rabbitConfig;
    private final ModelMapper modelMapper;

    public ContaCriadaDTOOut criarConta(CriarContaDTOIn contaDTOIn) {
        boolean existe = contaCommandRepository.existsByIdCliente(contaDTOIn.getIdCliente());
        if (existe) {
            throw new EntityExistsException("Conta já cadastrada no banco de dados para esse cliente");
        }

        String numero;
        do {
            numero = gerarNumeroConta();
        } while (contaCommandRepository.existsByNumero(numero));

        Conta conta = new Conta(contaDTOIn);
        conta.setNumero(numero);

        Conta salvo = contaCommandRepository.save(conta);

        var event = new ContaCreatedEvent(salvo.getId(), 
            salvo.getNumero(), 
            salvo.getIdCliente(), 
            salvo.getIdGerente(), 
            salvo.getSaldo(), 
            salvo.getLimite(), 
            salvo.getDataCriacao());
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "conta.created", event);

        return modelMapper.map(salvo, ContaCriadaDTOOut.class);
    }

    @Transactional
    public DepositarSacarDTOOut depositar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaCommandRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        conta.setSaldo(conta.getSaldo().add(depositarSacarDTOIn.getValor()));

        Movimentacao movimentacao = criarMovimentacao(
            conta,
            null,
            conta.getNumero(),
            depositarSacarDTOIn.getValor(),
            TipoMovimentacao.DEPOSITO);

        contaCommandRepository.save(conta);

        var event = new MovimentacaoCreatedEvent(
            movimentacao.getId(),
            conta.getNumero(),
            movimentacao.getData(),
            "DEPOSITO",
            movimentacao.getValor(),
            movimentacao.getContaOrigem(),
            movimentacao.getContaDestino()
        );
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "movimentacao.created", event);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public DepositarSacarDTOOut sacar(String numeroConta, DepositarSacarDTOIn depositarSacarDTOIn) {
        Conta conta = contaCommandRepository.findByNumero(numeroConta)
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

        contaCommandRepository.save(conta);

        var event = new MovimentacaoCreatedEvent(
            movimentacao.getId(),
            conta.getNumero(),
            movimentacao.getData(),
            "SAQUE",
            movimentacao.getValor(),
            movimentacao.getContaOrigem(),
            movimentacao.getContaDestino()
        );
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "movimentacao.created", event);

        return new DepositarSacarDTOOut(
                conta.getNumero(),
                movimentacao.getData(),
                conta.getSaldo()
        );
    }

    @Transactional
    public TransferirDTOOut transferir(String numeroConta, TransferirDTOIn transferirDTOIn) {
        Conta origem = contaCommandRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new EntityNotFoundException("Conta de origem não encontrada"));

        Conta destino = contaCommandRepository.findByNumero(transferirDTOIn.getDestino())
                .orElseThrow(() -> new EntityNotFoundException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(transferirDTOIn.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem");
        }

        origem.setSaldo(origem.getSaldo().subtract(transferirDTOIn.getValor()));
        destino.setSaldo(destino.getSaldo().add(transferirDTOIn.getValor()));

        Movimentacao movOrigem = criarMovimentacao(origem, origem.getNumero(), destino.getNumero(), transferirDTOIn.getValor(), TipoMovimentacao.TRANSFERENCIA);
        Movimentacao movDestino = criarMovimentacao(destino, origem.getNumero(), destino.getNumero(), transferirDTOIn.getValor(), TipoMovimentacao.TRANSFERENCIA);

        contaCommandRepository.save(origem);
        contaCommandRepository.save(destino);

        var event = new MovimentacaoCreatedEvent(movOrigem.getId(), origem.getNumero(), movOrigem.getData(), "TRANSFERENCIA", movOrigem.getValor(), movOrigem.getContaOrigem(), movOrigem.getContaDestino());
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "movimentacao.created", event);

        return new TransferirDTOOut(
                movOrigem.getContaOrigem(),
                movOrigem.getData(),
                movOrigem.getContaDestino(),
                origem.getSaldo(),
                movOrigem.getValor()
        );
    }

    public void mudarGerente(MudarGerenteDTOIn command) {
        Optional<Conta> conta = contaCommandRepository.findByNumero(command.getNumeroConta());
        if (conta.isPresent() && !conta.get().getIdGerente().equals(command.getGerenteId())) {
            conta.get().setIdGerente(command.getGerenteId());
            contaCommandRepository.save(conta.get());

            var event = new ContaUpdatedEvent(
                conta.get().getId(),
                conta.get().getNumero(),
                conta.get().getIdGerente()
            );
            eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "conta.updated", event);
        }
    }

    private Movimentacao criarMovimentacao(Conta conta, String numeroContaOrigem, String numeroContaDestino, BigDecimal valor, TipoMovimentacao tipo) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta(conta);
        movimentacao.setData(LocalDateTime.now());
        movimentacao.setTipo(tipo);
        movimentacao.setValor(valor);
        movimentacao.setContaOrigem(numeroContaOrigem);
        movimentacao.setContaDestino(numeroContaDestino);

        movimentacaoJpaRepository.save(movimentacao);
        conta.getMovimentacoes().add(movimentacao);
        contaCommandRepository.save(conta);

        return movimentacao;
    }

    public RecalcularLimiteDTOOut recalcularLimite(RecalcularLimiteDTOIn dto){
        final BigDecimal salarioMinimoParaLimite = new BigDecimal("2000.00");

        Conta conta = contaCommandRepository.findByIdCliente(dto.getIdDoCliente())
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
        contaCommandRepository.save(conta);

        var event = new ContaUpdatedEvent(conta.getId(), conta.getNumero(), conta.getIdGerente());
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "conta.updated", event);

        return new RecalcularLimiteDTOOut(conta.getIdGerente(), conta.getLimite(), conta.getSaldo());
    }

    @Transactional
    public Optional<Conta> reatribuirContaParaNovoGerente(Long novoGerenteId) {

        List<ContaPorGerenteDTOOut> contagem = contaReadRepository.countContasByGerente();

        if (contagem == null || contagem.isEmpty()) {
            return Optional.empty();
        }

        int totalGerentesComConta = contagem.size();
        if (totalGerentesComConta == 1) {

            ContaPorGerenteDTOOut unico = contagem.get(0);
            if (unico.getGerenteId().equals(novoGerenteId)) {
                return Optional.empty();
            }

            if (unico.getTotalContas() <= 1L) {
                return Optional.empty();
            }
        }

        long maxContas = contagem.stream()
            .mapToLong(ContaPorGerenteDTOOut::getTotalContas)
            .max()
            .orElse(0L);

        List<Long> gerentesComMaisContas = contagem.stream()
                .filter(c -> c.getTotalContas() == maxContas)
                .map(ContaPorGerenteDTOOut::getGerenteId)
                .collect(Collectors.toList());

        gerentesComMaisContas.remove(novoGerenteId);

        if (gerentesComMaisContas.isEmpty()) {
            return Optional.empty();
        }

        List<GerenteSaldoProjection> gerentesOrdenados =
                contaReadRepository.findGerentesOrderBySaldoPositivo(gerentesComMaisContas);

        if (gerentesOrdenados == null || gerentesOrdenados.isEmpty()) {
            return Optional.empty();
        }

        Long gerenteOrigemId = gerentesOrdenados.get(0).getGerenteId();

        ContaRead contaReadParaMover = contaReadRepository.findFirstByGerenteIdAndSaldoGreaterThanOrderBySaldoAsc(gerenteOrigemId, 0.0)
            .orElseGet(() -> contaReadRepository.findFirstByGerenteIdOrderBySaldoAsc(gerenteOrigemId).orElse(null));

        if (contaReadParaMover == null) {
            return Optional.empty();
        }

        Optional<Conta> contaCmdOpt = contaCommandRepository.findByNumero(contaReadParaMover.getNumero());
        if (!contaCmdOpt.isPresent()) { 
            return Optional.empty();
        }

        Conta contaCmd = contaCmdOpt.get();
        contaCmd.setIdGerente(novoGerenteId);
        contaCommandRepository.save(contaCmd);

        var event = new ContaUpdatedEvent(contaCmd.getId(), contaCmd.getNumero(), contaCmd.getIdGerente());
        eventProducer.publishEvent(rabbitConfig.EVENTS_EXCHANGE, "conta.updated", event);

        return Optional.of(contaCmd);
    }

    public String gerarNumeroConta() {
        int numero = 1000 + new Random().nextInt(9000);
        return String.valueOf(numero);
    }

}