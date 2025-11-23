package com.bantads.ms_saga.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bantads.ms_saga.config.RabbitMQConfig;
import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.dtos.saga.DadosClienteResponseDTO;
import com.bantads.ms_saga.dtos.saga.SagaCommand;
import com.bantads.ms_saga.dtos.state.AlteracaoPerfilSagaState;
import com.bantads.ms_saga.entity.SagaInstance;
import com.bantads.ms_saga.feign.cliente.ClienteClient;
import com.bantads.ms_saga.feign.conta.ContaClient;
import com.bantads.ms_saga.feign.gerente.GerenteClient;
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.ContaResponse;
import com.bantads.ms_saga.model.dto.output.GerenteResponse;

import feign.FeignException;

@Service
public class ClienteSagaService {

    @Autowired private ClienteClient clienteClient;
    @Autowired private ContaClient contaClient;
    @Autowired private GerenteClient gerenteClient;
    private static final Logger logger = LoggerFactory.getLogger(ClienteSagaService.class);
    @Autowired private SagaInstanceService sagaService;
    @Autowired private RabbitMQSender sender;    

    public DadosClienteResponseDTO montarClienteCompleto(String cpf) {
        DadosClienteResponseDTO dto = new DadosClienteResponseDTO();
        ClienteDTOOut cliente = clienteClient.buscarPorCpf(cpf);
        
        dto.setNome(cliente.getNome());
        dto.setCpf(cliente.getCpf());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setSalario(cliente.getSalario());
        if (cliente.getEndereco() != null) {
            dto.setCep(cliente.getEndereco().getCep());
            dto.setLogradouro(cliente.getEndereco().getLogradouro());
            dto.setNumero(cliente.getEndereco().getNumero());
            dto.setComplemento(cliente.getEndereco().getComplemento());
            dto.setCidade(cliente.getEndereco().getCidade());
            dto.setEstado(cliente.getEndereco().getEstado());
            dto.setBairro(cliente.getEndereco().getBairro());
        }
        try {
            ContaResponse conta = contaClient.buscarPorIdCliente(cliente.getId());
            
            dto.setNumeroConta(conta.getNumero());
            dto.setSaldo(conta.getSaldo());
            dto.setLimite(conta.getLimite());

                try {
                    GerenteResponse gerente = gerenteClient.buscarPorId(conta.getIdGerente());
                    dto.setNomeGerente(gerente.getNome());
                    dto.setEmailGerente(gerente.getEmail());
                    dto.setCpfGerente(gerente.getCpf());
                } catch (FeignException e) {
                    dto.setNomeGerente("Gerente não encontrado ou serviço indisponível");
                }
        } catch (FeignException.NotFound e) {
            dto.setNumeroConta("Conta não localizada");
        }
        return dto;
    }

    public ResponseEntity<?> alterarPerfil(String cpf, AlteracaoPerfilRequest request) {
    
        try {
            AlteracaoPerfilSagaState state = new AlteracaoPerfilSagaState(cpf, request);
            SagaInstance instance = sagaService.createSaga("ALTERACAO_PERFIL", state);
            request.setCpf(cpf);
            logger.info("Iniciando SAGA de Alteração de Perfil. CorrelationId: {}", instance.getCorrelationId());
            
            sender.sendSagaCommand(
                RabbitMQConfig.CLIENTE_UPDATE_KEY,
                new SagaCommand<>(request),
                instance.getCorrelationId()
            );

            return ResponseEntity.accepted().body(Map.of(
                "message", "Sua solicitação de alteração está sendo processada.",
                "correlationId", instance.getCorrelationId()
            ));

        }catch (Exception e) {
            logger.error("Erro ao INICIAR o SAGA de Alteração de Perfil: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro interno ao processar a solicitação.");
        }
    }    
}