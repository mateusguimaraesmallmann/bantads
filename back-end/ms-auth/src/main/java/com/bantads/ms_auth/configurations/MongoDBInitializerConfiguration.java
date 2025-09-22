package com.bantads.ms_auth.configurations;

import com.bantads.ms_auth.enums.Tipo;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.repositorys.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MongoDBInitializerConfiguration {

    @Bean
    CommandLineRunner initDatabase(MongoTemplate mongoTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder) { 
        
        return args -> {

            if (!mongoTemplate.collectionExists(User.class)) {
                mongoTemplate.createCollection(User.class);
            }

            userRepository.deleteAll();

            userRepository.save(new User(null, "cli1@bantads.com.br", passwordEncoder.encode("tads"), Tipo.CLIENTE));
            userRepository.save(new User(null, "cli2@bantads.com.br", passwordEncoder.encode("tads"), Tipo.CLIENTE));
            userRepository.save(new User(null, "cli3@bantads.com.br", passwordEncoder.encode("tads"), Tipo.CLIENTE));
            userRepository.save(new User(null, "cli4@bantads.com.br", passwordEncoder.encode("tads"), Tipo.CLIENTE));
            userRepository.save(new User(null, "cli5@bantads.com.br", passwordEncoder.encode("tads"), Tipo.CLIENTE));

            userRepository.save(new User(null, "ger1@bantads.com.br", passwordEncoder.encode("tads"), Tipo.GERENTE));
            userRepository.save(new User(null, "ger2@bantads.com.br", passwordEncoder.encode("tads"), Tipo.GERENTE));
            userRepository.save(new User(null, "ger3@bantads.com.br", passwordEncoder.encode("tads"), Tipo.GERENTE));

            userRepository.save(new User(null, "adm1@bantads.com.br", passwordEncoder.encode("tads"), Tipo.ADMINISTRADOR));

        };
    }
    
}