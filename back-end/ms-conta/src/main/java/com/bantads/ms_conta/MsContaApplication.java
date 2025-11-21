package com.bantads.ms_conta;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bantads.ms_conta")
@EnableRabbit
public class MsContaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsContaApplication.class, args);
    }

}