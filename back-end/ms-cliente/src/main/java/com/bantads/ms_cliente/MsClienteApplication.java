package com.bantads.ms_cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.bantads.ms_cliente.feign")
public class MsClienteApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsClienteApplication.class, args);
    }
}

