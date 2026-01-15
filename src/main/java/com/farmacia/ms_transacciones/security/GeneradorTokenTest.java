package com.farmacia.ms_transacciones;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class GeneradorTokenTest {
    public static void main(String[] args) {
        // 1. TU CLAVE SECRETA (Copiada exacta de application.properties)
        String secret = "TXlTZWNyZXRLZXlGb3JKV1RUb2tlbkxlZ2FjeVBoYXJtYWN5MjAyNVNwcmluZ0Jvb3Q0VmVyeVNlY3VyZUtleQecret=TXlTZWNyZXRLZXlGb3JKV1RUb2tlbkxlZ2FjeVBoYXJtYWN5MjAyNVNwcmluZ0Jvb3Q0VmVyeVNlY3VyZUtleQ";

        // 2. Crear la llave de firma
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // 3. Generar el Token simulando ser un Administrador
        String token = Jwts.builder()
                .setSubject("admin") // Username
                .claim("userId", 1L) // ID del usuario (Simulamos que es el ID 1)
                .claim("role", "ADMIN") // Rol
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día de vida
                .signWith(key)
                .compact();

        System.out.println("==================================================");
        System.out.println("COPIA ESTE TOKEN PARA INSOMNIA:");
        System.out.println(token);
        System.out.println("==================================================");
    }
}
