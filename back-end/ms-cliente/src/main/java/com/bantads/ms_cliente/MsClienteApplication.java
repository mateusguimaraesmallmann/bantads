package com.bantads.ms_cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.bantads.ms_cliente.feign")
public class MsClienteApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsClienteApplication.class, args);
    }
}

