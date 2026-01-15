package com.farmacia.ms_transacciones.security;

import com.farmacia.ms_transacciones.config.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtils.validateToken(token)) {
                // 1. Extraer datos
                String username = jwtUtils.getUsernameFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);

                // 2. Configurar Spring Security (Para que deje pasar la petición)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList()); // Aquí podrías cargar roles reales
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 3. LLENAR TU USER CONTEXT (Para que el resto de tu código funcione igual)
                UserContext.setUserId(userId);
                UserContext.setUsername(username);
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