package com.bantads.ms_conta.service.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.GerenteCargaDTO;
import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.model.entity.jpa.Conta;
import com.bantads.ms_conta.model.entity.jpa.Movimentacao;
import com.bantads.ms_conta.model.enums.Status;
import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import com.bantads.ms_conta.queue.producer.ContaProducer;
import com.bantads.ms_conta.repository.jpa.ContaJpaRepository;
import com.bantads.ms_conta.repository.jpa.MovimentacaoJpaRepository;
import com.bantads.ms_conta.saga.dto.CreateContaCommand;

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
    private final RabbitTemplate rabbitTemplate;

    //region Criar Conta
    @Transactional
    public ContaCriadaDTOOut criarConta(CreateContaCommand command) {
        BigDecimal limite = BigDecimal.ZERO;
        if (command.getSalario() != null && command.getSalario().compareTo(new BigDecimal("2000.00")) >= 0){
            limite = command.getSalario().divide(new BigDecimal(2));
        }

        //Verifica se já existe uma conta para o cliente
        Optional<Conta> contaExistenteOpt = contaJpaRepository.findByIdCliente(command.getIdCliente());
        Conta contaSalva;
        if (contaExistenteOpt.isPresent()){
            Conta contaExistente = contaExistenteOpt.get();
            contaExistente.setLimite(limite);

            //Altera o status da conta para pendente e limpa o motivo antigo da reprovação
            if (contaExistente.getStatus() == Status.REJECTED || contaExistente.getStatus() == Status.INACTIVE){
                contaExistente.setStatus(Status.PENDING);
                contaExistente.setMotivoReprovacao(null);
            }

            if (contaExistente.getNumero() == null){
                contaExistente.setNumero(gerarNumeroConta());
            }

            contaSalva = contaJpaRepository.save(contaExistente);

        } else {
           Long idGerente = atribuirGerente();

           Conta novaConta = new Conta();
           novaConta.setIdCliente(command.getIdCliente());
           novaConta.setIdGerente(idGerente);
           novaConta.setLimite(limite);
           novaConta.setSaldo(BigDecimal.ZERO);
           novaConta.setDataCriacao(LocalDateTime.now());
           novaConta.setStatus(Status.PENDING);
           novaConta.setNumero(null);

           contaSalva = contaJpaRepository.save(novaConta);
        }

        ContaCqrsDTO cqrsDto = new ContaCqrsDTO(contaSalva);
        contaProducer.enviarConta(cqrsDto);

        return modelMapper.map(contaSalva, ContaCriadaDTOOut.class);
    }


    //region Gerar Número
    public String gerarNumeroConta() {
        int numero = 1000 + new Random().nextInt(9000);
        return String.valueOf(numero);
    }

    //region Depositar
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

    //region Sacar
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

    //region Transferir
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

    //region Atribuir Gerente
    private Long atribuirGerente(){
        try{
            List<GerenteCargaDTO> cargaAtual = contaJpaRepository.findCargaGerentes();

            Object response = rabbitTemplate.convertSendAndReceive(RabbitConfig.GERENTE_ASSIGNMENT_QUEUE, cargaAtual);

            if (response != null){
                if (response instanceof Integer){
                    return ((Integer)response).longValue();
                }
                if (response instanceof Long){
                    return (Long) response;
                }
            } 
        } catch (Exception e){
             System.out.println("Erro na comunicação com ms-gerente: " + e.getMessage());
         }

         return 1L;
    }

    //region Mudar Gerente
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

    //region Recalcular Limite
    @Transactional
    public void atualizarLimitePorSalario(Long idCliente, BigDecimal novoSalario) {
        final BigDecimal salarioMinimoParaLimite = new BigDecimal("2000.00");

        Conta conta = contaJpaRepository.findByIdCliente(idCliente)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada para o cliente ID: " + idCliente));

        BigDecimal novoLimiteCalculado;
        if (novoSalario.compareTo(salarioMinimoParaLimite) >= 0) {
            novoLimiteCalculado = novoSalario.divide(new BigDecimal("2"));
        } else {
            novoLimiteCalculado = BigDecimal.ZERO;
        }
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal dividaAtual = conta.getSaldo().abs(); 
            
            if (novoLimiteCalculado.compareTo(dividaAtual) < 0) {
                novoLimiteCalculado = dividaAtual;
            }
        }
        conta.setLimite(novoLimiteCalculado);
        contaJpaRepository.save(conta);
    }

}

