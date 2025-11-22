package com.bantads.ms_auth.services;

import com.bantads.ms_auth.enums.Tipo;
import com.bantads.ms_auth.enums.Status;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.repositorys.UserRepository;
import com.bantads.ms_auth.saga.dto.CreateAuthUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAuthService {

    private final UserRepository userRepository;

    public User criarUsuarioInativo(CreateAuthUserCommand command) {
        User user = (User) userRepository.findByEmail(command.getEmail());
        if (user != null){
            if (user.userStatus() == "ACTIVE"){
                throw new RuntimeException("Usuário já cadastrado no sistema!");
            } else if (user.userStatus() == "PENDING"){
                throw new RuntimeException("Pedido já realizado, aguardando aprovação de um gerente.");
            }
            user.setRole(Tipo.valueOf(command.getRole()));
            user.setPassword(null);
            user.setStatus(Status.PENDING);

            return userRepository.save(user);
        }else {
            User newUser = new User();
            newUser.setEmail(command.getEmail());
            newUser.setPassword(null);
            newUser.setRole(Tipo.valueOf(command.getRole()));
            newUser.setStatus(Status.PENDING);

            return userRepository.save(newUser);
        }
    }
}