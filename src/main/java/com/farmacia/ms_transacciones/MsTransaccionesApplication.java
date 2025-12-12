package com.farmacia.ms_transacciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration; // Importar esto

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class}) // <--- AGREGAR ESTO
@EnableFeignClients(basePackages = "com.farmacia.ms_transacciones.feign")
public class MsTransaccionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransaccionesApplication.class, args);
	}
}
