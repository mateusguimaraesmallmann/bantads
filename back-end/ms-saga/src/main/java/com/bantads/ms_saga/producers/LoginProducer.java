package com.bantads.ms_saga.producers;

import org.springframework.stereotype.Component;
import com.bantads.ms_saga.dtos.ClienteDTO;
import com.bantads.ms_saga.dtos.LoginRequestDTO;

@Component
public class LoginProducer {

    // Stubs para compilar. Implemente envio ao RabbitMQ conforme sua infra.
    public void sendLogin(LoginRequestDTO dto, String correlationId) { /* send to auth */ }

    public void sendLoginCliente(ClienteDTO dto, String correlationId) { /* send to cliente */ }

}