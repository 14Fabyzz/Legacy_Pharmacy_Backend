package com.legacy.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync // 🚀 Habilita el procesamiento asíncrono con @Async
@RestController
public class MsUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
    }

    @GetMapping("/api/test")
    public String test() {
        return "MS-Usuarios funcionando correctamente!";
    }
}
