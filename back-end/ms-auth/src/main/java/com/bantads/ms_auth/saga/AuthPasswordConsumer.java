package com.bantads.ms_auth.saga;

import com.bantads.ms_auth.enums.Status;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.repositorys.UserRepository;
import com.bantads.ms_auth.saga.dto.SagaCommand;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthPasswordConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RabbitListener(queues = "auth-update-password")
    public void atualizarSenha(SagaCommand<Map<String, Object>> command) { 
        Map<String, Object> payload = command.getPayload();

        String email = (String) payload.get("email");
        String novaSenha = (String) payload.get("novaSenha");

        Object userObj = userRepository.findByEmail(email);
        
        if (userObj instanceof User) {
            User user = (User) userObj;
            user.setPassword(passwordEncoder.encode(novaSenha));
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            System.out.println("Senha atualizada com sucesso para o usuário: " + email);
        } else {
            System.err.println("Usuário não encontrado para atualização de senha: " + email);
        }
    }
}