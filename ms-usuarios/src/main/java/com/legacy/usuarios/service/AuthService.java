package com.legacy.usuarios.service;

import com.legacy.usuarios.dto.LoginRequestDTO;
import com.legacy.usuarios.dto.LoginResponseDTO;
import com.legacy.usuarios.entity.Usuario;
import com.legacy.usuarios.entity.Usuario.EstadoUsuario;
import com.legacy.usuarios.exception.UnauthorizedException;
import com.legacy.usuarios.repository.UsuarioRepository;
import com.legacy.usuarios.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordUtil passwordUtil;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    /**
     * Autenticar usuario y generar token JWT (RF24.1)
     */
    // OPTIMIZACIÓN APM: Se eliminó @Transactional del método login().
    // La transacción ya no es necesaria aquí porque auditoriaService es ahora
    // @Async
    // (su propia transacción). Mantener @Transactional abriría una conexión de BD
    // durante todo el tiempo del hash BCrypt (~80ms) + el viaje async a Aiven.
    public LoginResponseDTO login(LoginRequestDTO request, String ipOrigen, String userAgent) {
        // OPTIMIZACIÓN APM: JOIN FETCH → usuario + rol en UN SOLO query a la BD remota
        Usuario usuario = usuarioRepository.findByLoginWithRol(request.getLogin())
                .orElseThrow(() -> {
                    // Registrar intento fallido en auditoría
                    auditoriaService.registrarLoginFallido(
                            request.getLogin(),
                            ipOrigen,
                            userAgent,
                            "Usuario no encontrado");
                    return new UnauthorizedException("Credenciales inválidas");
                });

        // Verificar estado del usuario
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            auditoriaService.registrarLoginFallido(
                    request.getLogin(),
                    ipOrigen,
                    userAgent,
                    "Cuenta bloqueada");
            throw new UnauthorizedException("Cuenta bloqueada. Contacte al administrador");
        }

        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            auditoriaService.registrarLoginFallido(
                    request.getLogin(),
                    ipOrigen,
                    userAgent,
                    "Cuenta inactiva");
            throw new UnauthorizedException("Cuenta inactiva");
        }

        // Verificar contraseña
        if (!passwordUtil.matches(request.getPassword(), usuario.getPasswordHash())) {
            // Incrementar intentos fallidos
            usuarioService.registrarIntentoFallido(request.getLogin());

            auditoriaService.registrarLoginFallido(
                    request.getLogin(),
                    ipOrigen,
                    userAgent,
                    "Contraseña incorrecta");

            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Login exitoso - Resetear intentos fallidos
        usuarioService.resetearIntentosFallidos(request.getLogin());

        // Actualizar último acceso
        usuarioService.actualizarUltimoAcceso(request.getLogin());

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getLogin(),
                usuario.getId(),
                usuario.getRol().getNombre());

        // Registrar login exitoso en auditoría
        auditoriaService.registrarLoginExitoso(usuario, ipOrigen, userAgent);

        // Construir respuesta
        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .login(usuario.getLogin())
                .rol(usuario.getRol().getNombre())
                .expiracion(System.currentTimeMillis() + jwtService.getExpirationTime())
                .build();
    }

    /**
     * Logout (opcional - para auditoría)
     */
    public void logout(String login) {
        usuarioRepository.findByLoginWithRol(login).ifPresent(usuario -> {
            auditoriaService.registrarLogout(usuario);
        });
    }

    /**
     * Validar token (útil para otros microservicios)
     */
    public boolean validarToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            return username != null && !username.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}