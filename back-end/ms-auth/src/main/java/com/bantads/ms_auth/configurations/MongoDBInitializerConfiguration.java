package com.bantads.ms_auth.configurations;

import com.bantads.ms_auth.enums.Tipo;
import com.bantads.ms_auth.enums.Status;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.repositorys.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

@Configuration
public class MongoDBInitializerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoDBInitializerConfiguration.class);

    @Bean
    CommandLineRunner initDatabase(MongoTemplate mongoTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder) { 
        
        return args -> {
            log.info("Iniciando a criação de usuários iniciais no MongoDB...");

            if (!mongoTemplate.collectionExists("users")) {
                mongoTemplate.createCollection("users");
            }

            userRepository.deleteAll();
            log.info("Usuários antigos deletados. Inserindo novos usuários...");

            userRepository.save(new User(null, "cli1@bantads.com.br","12912861012", passwordEncoder.encode("tads"), Tipo.CLIENTE, Status.ACTIVE));
            userRepository.save(new User(null, "cli2@bantads.com.br","09506382000", passwordEncoder.encode("tads"), Tipo.CLIENTE, Status.ACTIVE));
            userRepository.save(new User(null, "cli3@bantads.com.br","85733854057", passwordEncoder.encode("tads"), Tipo.CLIENTE, Status.ACTIVE));
            userRepository.save(new User(null, "cli4@bantads.com.br","58872160006", passwordEncoder.encode("tads"), Tipo.CLIENTE, Status.ACTIVE));
            userRepository.save(new User(null, "cli5@bantads.com.br","76179646090", passwordEncoder.encode("tads"), Tipo.CLIENTE, Status.ACTIVE));

            userRepository.save(new User(null, "ger1@bantads.com.br","98574307084", passwordEncoder.encode("tads"), Tipo.GERENTE, Status.ACTIVE));
            userRepository.save(new User(null, "ger2@bantads.com.br","64065268052", passwordEncoder.encode("tads"), Tipo.GERENTE, Status.ACTIVE));
            userRepository.save(new User(null,"ger3@bantads.com.br", "23862179060", passwordEncoder.encode("tads"), Tipo.GERENTE, Status.ACTIVE));

            userRepository.save(new User(null, "adm1@bantads.com.br", "40501740066",passwordEncoder.encode("tads"), Tipo.ADMINISTRADOR, Status.ACTIVE));

            log.info("Usuários iniciais do MongoDB inseridos com sucesso!");
        };
    }
    
}