package com.torneos.partidas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PartidasApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartidasApplication.class, args);
	}

}
