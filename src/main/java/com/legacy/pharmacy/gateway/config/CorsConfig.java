package com.legacy.pharmacy.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración CORS para el Gateway
 * <p>
 * Permite que aplicaciones frontend (Angular, React) puedan
 * consumir el API desde diferentes orígenes
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Orígenes permitidos (ajustar según ambiente)
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",  // Angular dev
                "http://localhost:3000",  // React dev
                "http://localhost:8080"   // Gateway mismo
        ));

        // Métodos HTTP permitidos
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers permitidos
        corsConfig.setAllowedHeaders(Arrays.asList("*"));

        // Permitir credenciales (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // Tiempo de caché de preflight
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
