package com.bantads.ms_saga.services.autocadastro;

import com.bantads.ms_saga.client.ClienteClient;
import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.AprovarClienteDTOOut;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutocadastroSagaService {

    private final ClienteClient ClienteClient;

    public AprovarClienteDTOOut aprovarCliente(String cpf) {
        
        AprovarClienteDTOOut response = new AprovarClienteDTOOut();
        
        try {
            ClienteClient.buscarPorCpf(cpf);

            response.setCpf(cpf);        

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public CadastroClienteDTOIn cadastrarCliente(CadastroClienteDTOIn dto) {
        
        CadastroClienteDTOIn response = new CadastroClienteDTOIn();
        
        try {
            ClienteClient.buscarPorCpf(dto.getCpf());

            response.setCpf(response.getCpf());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public ResponseEntity<?> autoCadastro(AutoCadastroRequestDTO body) {
        String correlationIdAuth = UUID.randomUUID().toString();
        String correlationIdCliente = UUID.randomUUID().toString();

        CompletableFuture<Map<String, Object>> responseFutureAuth = new CompletableFuture<>();
        CompletableFuture<Map<String, Object>> responseFutureCliente = new CompletableFuture<>();

        pendingRequests.put(correlationIdAuth, responseFutureAuth);
        pendingRequests.put(correlationIdCliente, responseFutureCliente);

        try {

            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(body.getEmail(), body.getSenha(), TipoUsuario.FUNCIONARIO);
            ClienteDTO ClienteRequestDTO = new ClienteDTO(null, body.getCpf(), body.getEmail(), body.getNome(), body.getTelefone(), true);

            clienteProducer.sendCadastrarLogin(registerRequestDTO, correlationIdAuth);
            clienteProducer.sendCadastrarFuncionario(ClienteDTO, correlationIdCliente);

            // Recebe respostas dos dois microsserviços
            Map<String, Object> responseAuth = responseFutureAuth.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
            Map<String, Object> responseCliente = responseFutureCliente.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

            String errorAuth = (String) responseAuth.get("errorMessage");
            String errorCliente = (String) responseCliente.get("errorMessage");

            if (errorAuth != null || errorFuncionario != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao relizar o cadastro do funcionário.");
            }

            //Converte
            ClienteCadastroResponseDTO response = objectMapper.convertValue(responseFuncionario, ClienteCadastroResponseDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Erro no AutocadastroSagaService: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no processamento SAGA: " + e.getMessage());
        }
    }

    public ResponseEntity<?> alterar(ClienteAlteracaoRequestDTO body, String codigo) {
        String correlationIdConsultaFuncionario = UUID.randomUUID().toString();
        String correlationIdAuth = UUID.randomUUID().toString();
        String correlationIdCliente= UUID.randomUUID().toString();

        CompletableFuture<Map<String, Object>> responseFutureConsultaCliente = new CompletableFuture<>();
        CompletableFuture<Map<String, Object>> responseFutureAuth = new CompletableFuture<>();
        CompletableFuture<Map<String, Object>> responseFutureCliente = new CompletableFuture<>();

        pendingRequests.put(correlationIdConsultaFuncionario, responseFutureConsultaCliente);
        pendingRequests.put(correlationIdAuth, responseFutureAuth);
        pendingRequests.put(correlationIdCliente, responseFutureCliente);

        try {

            ClienteConsultaRequestDTO request = new ClienteConsultaRequestDTO(Long.valueOf(codigo));

            clienteProducer.sendConsultaFuncionarioPorId(request, correlationIdConsultaFuncionario);

            //Aguarda os dados atuais
            Map<String, Object> dadosClienteAntigo = responseFutureConsultaCliente.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
            //Convert
            ClienteDTO cadastroAntigo = objectMapper.convertValue(dadosFuncionarioAntigo, ClienteDTO.class);

            UpdateLoginRequestDTO updateLoginDTO = new UpdateLoginRequestDTO(body.getEmail(), cadastroAntigo.getEmail(), body.getSenha(), TipoUsuario.FUNCIONARIO);
            
            Long codigoFunc = Long.valueOf(codigo);
            ClienteDTO updateClienteDTO = new ClienteDTO(codigoFunc, body.getCpf(), body.getEmail(), body.getNome(), body.getTelefone(), true);

            clienteProducer.sendAlterarLogin(updateLoginDTO, correlationIdAuth);
            clienteProducer.sendAlterarFuncionario(updateClienteDTO, correlationIdCliente);

            Map<String, Object> responseAuth = responseFutureAuth.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
            Map<String, Object> responseCliente = responseFutureCliente.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

            String errorAuth = (String) responseAuth.get("errorMessage");
            String errorFuncionario = (String) responseCliente.get("errorMessage");

            if (errorAuth != null || errorFuncionario != null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao realizar a atualização do funcionário.");
            }

            ClienteCadastroResponseDTO response = objectMapper.convertValue(responseFuncionario, ClienteCadastroResponseDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Erro no AutocadastroSagaService: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao realizar a atualização do funcionário." + e.getMessage());
        }
    }
    
}
