package com.bantads.ms_conta.service.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.bantads.ms_conta.config.RabbitConfig;
import com.bantads.ms_conta.model.dto.GerenteCargaDTO;
import com.bantads.ms_conta.model.dto.cqrs.ContaCqrsDTO;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.MudarGerenteDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.ExtratoDTO;
import com.bantads.ms_conta.model.dto.output.MovimentacaoExtratoDTO;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.model.entity.jpa.Conta;
import com.bantads.ms_conta.model.entity.jpa.Movimentacao;
import com.bantads.ms_conta.model.enums.Status;
import com.bantads.ms_conta.model.enums.TipoMovimentacao;
import com.bantads.ms_conta.queue.producer.ContaProducer;
import com.bantads.ms_conta.repository.jpa.ContaJpaRepository;
import com.bantads.ms_conta.repository.jpa.MovimentacaoJpaRepository;
import com.bantads.ms_conta.saga.dto.ContaSyncDTO;
import com.bantads.ms_conta.saga.dto.CreateContaCommand;
import com.bantads.ms_conta.saga.dto.SagaCommand;

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

    //region Ativar Conta
    @Transactional
    public void ativarConta(Long idCliente, String nomeCliente, String emailCliente) {
        Conta conta = contaJpaRepository.findByIdCliente(idCliente)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada!"));

        String novoNumero = gerarNumeroConta();
        conta.setNumero(novoNumero);
        String novaSenha = gerarSenhaAleatoria();

        conta.setStatus(Status.ACTIVE);
        contaJpaRepository.save(conta);

        // Parâmetros do e-mail após ativação da conta
        Map<String, String> emailPayload = new HashMap<>();
        emailPayload.put("email", emailCliente);
        emailPayload.put("assunto", "BANTADS - Conta Aprovada!");
        emailPayload.put("mensagem", String.format(
            "Olá %s, sua conta foi aprovada!\nNúmero da Conta: %s\nSenha: %s", 
            nomeCliente, novoNumero, novaSenha));

        rabbitTemplate.convertAndSend("conta-ativada", emailPayload);

        // Envio do payload com e-mail e senha para o ms-auth atualizar seu registro
        Map<String, Object> authData = new HashMap<>();
        authData.put("email", emailCliente);
        authData.put("novaSenha", novaSenha);

        SagaCommand<Map<String, Object>> command = new SagaCommand<>();
        command.setPayload(authData);

        rabbitTemplate.convertAndSend("auth-update-password", command);

    }

    //region Desativar Conta
    @Transactional
    public void desativarConta(Long idCliente, String nomeCliente, String emailCliente, String motivoReprovacao) {
        Conta conta = contaJpaRepository.findByIdCliente(idCliente)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada!"));


        conta.setMotivoReprovacao(motivoReprovacao);
        conta.setStatus(Status.INACTIVE);
        contaJpaRepository.save(conta);

        // Parâmetros do e-mail, passando o motivo da reprovação junto
        Map<String, String> emailPayload = new HashMap<>();
        emailPayload.put("email", emailCliente);
        emailPayload.put("assunto", "BANTADS - Solicitação Negada");
        emailPayload.put("mensagem", String.format(
            "Olá %s, informamos que infelizmente sua solicitação de conta não foi aprovada palo seguinte motivo: %s",
            nomeCliente, motivoReprovacao
        ));

        rabbitTemplate.convertAndSend("conta-ativada", emailPayload);
    }

    //region Gerar Senha
    // Gera a senha aqui e encaminha para o ms-auth atualizar no seu registro via RabbitMQ
    private String gerarSenhaAleatoria() {
        // Gera número entre 0 e 9999 e formata com zeros à esquerda (ex: 0042)
        Random random = new Random();
        return "tads"; 
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
        ContaSyncDTO syncDTO = new ContaSyncDTO(
            conta.getId(),
            conta.getIdCliente(),
            conta.getIdGerente(),
            conta.getNumero(),
            conta.getSaldo(),
            conta.getLimite(),
            conta.getDataCriacao(),
            conta.getStatus()
        );
        rabbitTemplate.convertAndSend(
            RabbitConfig.CONTA_SYNC_EXCHANGE,
            RabbitConfig.CONTA_SYNC_KEY,
            syncDTO
        );

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
        ContaSyncDTO syncDTO = new ContaSyncDTO(
            conta.getId(),
            conta.getIdCliente(),
            conta.getIdGerente(),
            conta.getNumero(),
            conta.getSaldo(),
            conta.getLimite(),
            conta.getDataCriacao(),
            conta.getStatus()
        );
        rabbitTemplate.convertAndSend(
            RabbitConfig.CONTA_SYNC_EXCHANGE,
            RabbitConfig.CONTA_SYNC_KEY,
            syncDTO
        );        

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

        Conta destino = contaJpaRepository.findByNumero(transferirDTOIn.getDestino()) 
                .orElseThrow(() -> new EntityNotFoundException("Conta de destino não encontrada"));
        if (origem.getSaldo().compareTo(transferirDTOIn.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem");
        }

        origem.setSaldo(origem.getSaldo().subtract(transferirDTOIn.getValor()));
        destino.setSaldo(destino.getSaldo().add(transferirDTOIn.getValor()));

        criarMovimentacao(destino, origem.getNumero(), destino.getNumero(), 
            transferirDTOIn.getValor(), TipoMovimentacao.TRANSFERENCIA);

        Movimentacao movimentacaoOrigem = criarMovimentacao(origem, origem.getNumero(), destino.getNumero(), 
            transferirDTOIn.getValor(), TipoMovimentacao.TRANSFERENCIA);

        contaJpaRepository.save(origem);
        contaJpaRepository.save(destino);

        ContaSyncDTO syncOrigemDTO = new ContaSyncDTO(
            origem.getId(),
            origem.getIdCliente(),
            origem.getIdGerente(),
            origem.getNumero(),
            origem.getSaldo(), 
            origem.getLimite(),
            origem.getDataCriacao(),
            origem.getStatus()
        );
        rabbitTemplate.convertAndSend(RabbitConfig.CONTA_SYNC_EXCHANGE, RabbitConfig.CONTA_SYNC_KEY, syncOrigemDTO);

        ContaSyncDTO syncDestinoDTO = new ContaSyncDTO(
            destino.getId(),
            destino.getIdCliente(),
            destino.getIdGerente(),
            destino.getNumero(),
            destino.getSaldo(),
            destino.getLimite(),
            destino.getDataCriacao(),
            destino.getStatus()
        );
        rabbitTemplate.convertAndSend(RabbitConfig.CONTA_SYNC_EXCHANGE, RabbitConfig.CONTA_SYNC_KEY, syncDestinoDTO);        

        return new TransferirDTOOut(
                movimentacaoOrigem.getContaOrigem(),
                movimentacaoOrigem.getData(),
                movimentacaoOrigem.getContaDestino(),
                origem.getSaldo(),
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
        ContaSyncDTO syncDTO = new ContaSyncDTO(
            conta.getId(),
            conta.getIdCliente(),
            conta.getIdGerente(),
            conta.getNumero(),
            conta.getSaldo(),
            conta.getLimite(),
            conta.getDataCriacao(),
            conta.getStatus()
        );

        rabbitTemplate.convertAndSend(
            RabbitConfig.CONTA_SYNC_EXCHANGE,
            RabbitConfig.CONTA_SYNC_KEY,
            syncDTO
        );        
    }

    //region extrato
    public ExtratoDTO consultarExtrato(String numeroConta) {

        Conta conta = contaJpaRepository.findByNumero(numeroConta)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada: " + numeroConta));
        List<Movimentacao> movimentacoes = movimentacaoJpaRepository.findByConta_NumeroOrderByDataDesc(numeroConta);

        List<MovimentacaoExtratoDTO> listaDtos = movimentacoes.stream()
            .map(m -> new MovimentacaoExtratoDTO(
                m.getData(),
                m.getTipo().name().toLowerCase(), 
                m.getContaOrigem() != null ? m.getContaOrigem() : "", 
                m.getContaDestino() != null ? m.getContaDestino() : "",
                m.getValor()
            ))
            .toList();

        return new ExtratoDTO(
            conta.getNumero(),
            conta.getSaldo(),
            listaDtos
        );
    }

}

