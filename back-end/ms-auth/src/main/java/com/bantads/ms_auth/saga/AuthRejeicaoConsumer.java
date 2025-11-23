package com.bantads.ms_auth.saga;

import com.bantads.ms_auth.configurations.RabbitMQConfig;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.enums.Status;
import com.bantads.ms_auth.repositorys.UserRepository;
import com.bantads.ms_auth.saga.dto.SagaCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AuthRejeicaoConsumer {

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.AUTH_STATUS_QUEUE)
    public void atualizarStatus(SagaCommand<Map<String, String>> command) {
        Map<String, String> payload = command.getPayload();
        String email = payload.get("email");

        Object userOpt = userRepository.findByEmail(email);
        
        if (userOpt instanceof User){
            User user = (User) userOpt;
            user.setStatus(Status.INACTIVE);
            userRepository.save(user);
            System.out.println("Usuário inativado no Mongo.");
        } else {
            System.err.println("Usuário não encontrado para atualização de senha: " + email);
        }
        }
    }