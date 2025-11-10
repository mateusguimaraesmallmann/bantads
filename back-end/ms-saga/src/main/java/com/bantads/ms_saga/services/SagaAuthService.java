// package com.bantads.ms_saga.services;

// import java.util.Map;
// import java.util.UUID;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.TimeoutException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import com.bantads.ms_saga.configurations.SagaMessaging;
// import com.bantads.ms_saga.dtos.AuthResponseDTO;
// import com.bantads.ms_saga.dtos.ClienteDTO;
// import com.bantads.ms_saga.dtos.FuncionarioDTO;
// import com.bantads.ms_saga.dtos.LoginRequestDTO;
// import com.bantads.ms_saga.dtos.LoginResponseDTO;
// import com.bantads.ms_saga.enums.TipoUsuario;
// import com.bantads.ms_saga.producers.LoginProducer;

// @Service
// public class SagaAuthService {

//     private static final Logger logger = LoggerFactory.getLogger(SagaAuthService.class);

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private LoginProducer loginProducer;

//     private final Map<String, CompletableFuture<Map<String, Object>>> pendingRequests = new ConcurrentHashMap<>();

//     private static final long FUTURE_RESPONSE_TIMEOUT = 30;

//     public ResponseEntity<Object> login(LoginRequestDTO loginRequestDTO) {
//         String correlationIdAuth = UUID.randomUUID().toString();
//         String correlationIdCliente = UUID.randomUUID().toString();
//         String correlationIdFuncionario = UUID.randomUUID().toString();

//         CompletableFuture<Map<String, Object>> responseFutureAuth = new CompletableFuture<>();
//         CompletableFuture<Map<String, Object>> responseFutureCliente = new CompletableFuture<>();
//         CompletableFuture<Map<String, Object>> responseFutureFuncionario = new CompletableFuture<>();

//         pendingRequests.put(correlationIdAuth, responseFutureAuth);
//         pendingRequests.put(correlationIdCliente, responseFutureCliente);
//         pendingRequests.put(correlationIdFuncionario, responseFutureFuncionario);

//         try {
//             loginProducer.sendLogin(loginRequestDTO, correlationIdAuth);

//             Map<String, Object> responseAuth = responseFutureAuth.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

//             if (responseAuth != null && responseAuth.get("errorMessage") != null) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha incorretos!");
//             }

//             AuthResponseDTO loginResponse = objectMapper.convertValue(responseAuth, AuthResponseDTO.class);

//             if (loginResponse != null && TipoUsuario.CLIENTE.equals(loginResponse.getTipo())) {
//                 ClienteDTO clienteDTO = new ClienteDTO(null, null, loginRequestDTO.getLogin(), null, null, null);

//                 loginProducer.sendLoginCliente(clienteDTO, correlationIdCliente);
//                 Map<String, Object> responseCliente = responseFutureCliente.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
//                 ClienteDTO cliente = objectMapper.convertValue(responseCliente, ClienteDTO.class);

//                 LoginResponseDTO response = new LoginResponseDTO(loginResponse.getAccess_token(), "bearer", "CLIENTE", cliente);

//                 return ResponseEntity.status(HttpStatus.OK).body(response);
//             } else {
//                 FuncionarioDTO funcionarioDTO = new FuncionarioDTO(null, null, loginRequestDTO.getLogin(), null, null, false);

//                 loginProducer.sendLoginFuncionario(funcionarioDTO, correlationIdFuncionario);
//                 Map<String, Object> responseFuncionario = responseFutureFuncionario.get(FUTURE_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
//                 FuncionarioDTO funcionario = objectMapper.convertValue(responseFuncionario, FuncionarioDTO.class);

//                 LoginResponseDTO response = new LoginResponseDTO(loginResponse.getAccess_token(), "bearer", "FUNCIONARIO", funcionario);
//                 return ResponseEntity.status(HttpStatus.OK).body(response);
//             }

//         } catch (TimeoutException e) {
//             logger.error("Timeout na autenticação: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Timeout no processamento da autenticação.");
//         } catch (Exception e) {
//             logger.error("Erro no SagaAuthService: {}", e.getMessage(), e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no processamento: " + e.getMessage());
//         } finally {
//             pendingRequests.remove(correlationIdAuth);
//             pendingRequests.remove(correlationIdCliente);
//             pendingRequests.remove(correlationIdFuncionario);
//         }
//     }

//     @RabbitListener(queues = SagaMessaging.QUEUE_RPL_AUTH_LOGIN)
//     public void handleAuthResponse(Message message) {
//         handleResponse(message);
//     }

//     @RabbitListener(queues = SagaMessaging.QUEUE_RPL_AUTH_CLIENTE)
//     public void handleClienteResponse(Message message) {
//         handleResponse(message);
//     }

//     @RabbitListener(queues = SagaMessaging.QUEUE_RPL_AUTH_FUNCIONARIO)
//     public void handleFuncionarioResponse(Message message) {
//         handleResponse(message);
//     }

//     private void handleResponse(Message message) {
//         try {
//             String correlationId = message.getMessageProperties().getCorrelationIdString();
//             if (correlationId == null) {
//                 // fallback: try raw correlation id
//                 Object raw = message.getMessageProperties().getCorrelationId();
//                 correlationId = raw != null ? raw.toString() : null;
//             }
//             if (correlationId == null) {
//                 logger.warn("Mensagem recebida sem correlationId, descartando.");
//                 return;
//             }

//             @SuppressWarnings("unchecked")
//             Map<String, Object> response = objectMapper.readValue(message.getBody(), Map.class);

//             CompletableFuture<Map<String, Object>> future = pendingRequests.remove(correlationId);
//             if (future != null) {
//                 future.complete(response);
//                 logger.info("Resposta correlacionada com correlationId {}", correlationId);
//             } else {
//                 logger.warn("Nenhum future pendente para correlationId {}", correlationId);
//             }

//         } catch (Exception e) {
//             logger.error("Erro ao processar mensagem de resposta: {}", e.getMessage(), e);
//         }
//     }
// }