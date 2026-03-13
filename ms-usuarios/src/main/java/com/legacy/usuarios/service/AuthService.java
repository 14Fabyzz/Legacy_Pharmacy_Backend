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
     * Autenticar usuario y generar token JWT.
     * Acepta como identificador el login (nombre de usuario) O el email.
     * La detección es automática: si el valor contiene '@' se busca por email.
     */
    // OPTIMIZACIÓN APM: Sin @Transactional. La auditoría es @Async (transacción propia).
    // Mantener @Transactional abriría conexión durante todo el hash BCrypt (~80ms).
    public LoginResponseDTO login(LoginRequestDTO request, String ipOrigen, String userAgent) {

        String identifier = request.getIdentifier();

        // --- Resolución dual: email vs login ---
        // Si el identifier contiene '@', asumimos que es un email.
        Usuario usuario = esEmail(identifier)
                ? resolverPorEmail(identifier, ipOrigen, userAgent)
                : resolverPorLogin(identifier, ipOrigen, userAgent);

        // Verificar estado del usuario
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            auditoriaService.registrarLoginFallido(
                    usuario.getLogin(), ipOrigen, userAgent, "Cuenta bloqueada");
            throw new UnauthorizedException("Cuenta bloqueada. Contacte al administrador");
        }

        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            auditoriaService.registrarLoginFallido(
                    usuario.getLogin(), ipOrigen, userAgent, "Cuenta inactiva");
            throw new UnauthorizedException("Cuenta inactiva");
        }

        // Verificar contraseña
        if (!passwordUtil.matches(request.getPassword(), usuario.getPasswordHash())) {
            usuarioService.registrarIntentoFallido(usuario.getLogin());
            auditoriaService.registrarLoginFallido(
                    usuario.getLogin(), ipOrigen, userAgent, "Contraseña incorrecta");
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Login exitoso — resetear intentos fallidos y actualizar último acceso
        usuarioService.resetearIntentosFallidos(usuario.getLogin());
        usuarioService.actualizarUltimoAcceso(usuario.getLogin());

        // Generar token JWT (siempre usando el login como subject)
        String token = jwtService.generateToken(
                usuario.getLogin(),
                usuario.getId(),
                usuario.getRol().getNombre());

        // Registrar login exitoso en auditoría
        auditoriaService.registrarLoginExitoso(usuario, ipOrigen, userAgent);

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

    // -------------------------------------------------------------------------
    // Métodos privados de resolución
    // -------------------------------------------------------------------------

    /** Detecta si el identificador es un email por la presencia de '@'. */
    private boolean esEmail(String identifier) {
        return identifier != null && identifier.contains("@");
    }

    /** Busca el usuario por email con JOIN FETCH de rol (1 query). */
    private Usuario resolverPorEmail(String email, String ipOrigen, String userAgent) {
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> {
                    auditoriaService.registrarLoginFallido(
                            email, ipOrigen, userAgent, "Email no encontrado");
                    return new UnauthorizedException("Credenciales inválidas");
                });
    }

    /** Busca el usuario por login con JOIN FETCH de rol (1 query). */
    private Usuario resolverPorLogin(String login, String ipOrigen, String userAgent) {
        return usuarioRepository.findByLoginWithRol(login)
                .orElseThrow(() -> {
                    auditoriaService.registrarLoginFallido(
                            login, ipOrigen, userAgent, "Usuario no encontrado");
                    return new UnauthorizedException("Credenciales inválidas");
                });
    }

    // -------------------------------------------------------------------------
    // Logout y validación de token
    // -------------------------------------------------------------------------

    /**
     * Logout (opcional - para auditoría)
     */
    public void logout(String login) {
        usuarioRepository.findByLoginWithRol(login).ifPresent(usuario ->
                auditoriaService.registrarLogout(usuario)
        );
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