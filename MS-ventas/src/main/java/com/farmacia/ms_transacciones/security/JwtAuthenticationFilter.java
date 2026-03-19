package com.farmacia.ms_transacciones.security;

import com.farmacia.ms_transacciones.config.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtils.validateToken(token)) {
                // 1. Extraer datos
                String username = jwtUtils.getUsernameFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);

                System.out.println("✅ TOKEN VALIDO - Usuario: " + username + " - Rol: " + role);
                // ... lógica de autenticación ...

                // 2. Configurar Spring Security CON EL ROL
                // Spring Security suele esperar el prefijo "ROLE_" o usa hasAuthority en el
                // config

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(authority) // <--- ¡EL CAMBIO IMPORTANTE!
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 3. LLENAR USER CONTEXT (Para que el resto de código funcione igual)
                UserContext.setUserId(userId);
                UserContext.setUsername(username);
                UserContext.setUserRole(role);
            } else {
                System.out.println("❌ TOKEN INVALIDO O FIRMA NO COINCIDE EN DOCKER");
            }
        } else {
            // -----------------------------------------------------------------------------
            // FALLBACK: Confiar en Headers del Gateway (Solo para redes internas seguras)
            // -----------------------------------------------------------------------------
            String userIdHeader = request.getHeader("X-User-Id");
            String usernameHeader = request.getHeader("X-Username");
            String roleHeader = request.getHeader("X-User-Role");

            if (userIdHeader != null) {
                System.out.println("⚠️ AUTENTICACION VIA HEADERS (GATEWAY TRUST): " + usernameHeader);

                String role = (roleHeader != null) ? roleHeader : "USER";
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usernameHeader,
                        null,
                        List.of(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                try {
                    UserContext.setUserId(Long.parseLong(userIdHeader));
                } catch (NumberFormatException e) {
                    UserContext.setUserId(null); // O manejar error
                }
                UserContext.setUsername(usernameHeader);
                UserContext.setUserRole(role);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpieza vital
            UserContext.clear();
        }
    }
}