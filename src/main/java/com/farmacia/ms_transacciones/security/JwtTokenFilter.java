package com.farmacia.ms_transacciones.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. VERIFICAR SI EL FILTRO SE EJECUTA
        System.out.println(">>> 1. Entrando al Filtro JWT para: " + request.getRequestURI());

        String header = request.getHeader("Authorization");

        // 2. VER QUÉ HEADER LLEGA (¿Es null? ¿Dice Bearer?)
        System.out.println(">>> 2. Header Authorization recibido: '" + header + "'");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println(">>> 3. Token extraído: " + token);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String usuarioId = claims.getSubject();
                System.out.println(">>> 4. Token VÁLIDO. Usuario: " + usuarioId);

                if (usuarioId != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            usuarioId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println(">>> 5. Usuario autenticado en el sistema");
                }

            } catch (Exception e) {
                // AQUÍ VEREMOS SI FALLA LA FIRMA O LA CLAVE
                System.out.println(">>>ERROR VALIDANDO TOKEN:");
                e.printStackTrace();
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println(">>> ⚠️ El header es NULL o no empieza con 'Bearer '");
        }

        filterChain.doFilter(request, response);
    }
}