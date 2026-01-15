package com.farmacia.ms_transacciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Borra @EnableFeignClients, ya no lo usamos porque tenemos RestTemplateConfig
@SpringBootApplication
public class MsTransaccionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransaccionesApplication.class, args);
	}
}
