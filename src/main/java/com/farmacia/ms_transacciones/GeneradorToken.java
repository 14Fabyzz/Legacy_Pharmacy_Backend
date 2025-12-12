package com.farmacia.ms_transacciones; // <--- Fíjate que esté en el paquete principal

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class GeneradorToken {

    public static void main(String[] args) {

        // 1. TU CLAVE SECRETA (Debe ser IDÉNTICA a la de application.properties)
        String secretKey = "TGVnYWN5UGhhcm1hY3lJc1RoZUJlc3RTRU5BUHJveWVjdDIwMjY=";

        // 2. CREAR EL TOKEN
        String token = Jwts.builder()
                .setSubject("vendedor_prueba") // El usuario simulado
                .claim("rol", "ADMIN")         // Datos extra
                .setIssuedAt(new Date())       // Creado hoy
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Expira en 1 hora
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        // 3. MOSTRARLO EN CONSOLA
        System.out.println("=========================================");
        System.out.println("COPIA ESTE TOKEN (Sin espacios extra):");
        System.out.println(token);
        System.out.println("=========================================");
    }
}
