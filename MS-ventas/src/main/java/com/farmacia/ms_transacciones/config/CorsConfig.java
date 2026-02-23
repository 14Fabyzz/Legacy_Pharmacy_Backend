package com.farmacia.ms_transacciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permitir orígenes (usando Patterns para evitar conflictos con
        // allowCredentials)
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "*"));

        // Es vital especificar los headers permitidos para que el OPTIONS sea aprobado
        config.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
