package com.bantads.ms_saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSagaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSagaApplication.class, args);
	}

}
