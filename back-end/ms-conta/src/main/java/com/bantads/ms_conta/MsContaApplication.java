package com.bantads.ms_conta;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.bantads.ms_conta")
@EnableJpaRepositories(basePackages = "com.bantads.ms_conta.repository.jpa")
@EntityScan(basePackages = "com.bantads.ms_conta.model.entity.jpa")
@EnableRabbit
public class MsContaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsContaApplication.class, args);
    }
}