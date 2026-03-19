package com.legacy.usuarios.service;

import com.legacy.usuarios.entity.AuditoriaLogin;
import com.legacy.usuarios.entity.AuditoriaLogin.TipoEvento;
import com.legacy.usuarios.entity.Usuario;
import com.legacy.usuarios.repository.AuditoriaLoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLoginRepository auditoriaRepository;

    /**
     * Registrar login exitoso (RF25.2)
     * OPTIMIZACIÓN APM: @Async → Fire-and-Forget. No bloquea el hilo del login.
     * El token ya fue devuelto al cliente antes de que este método inicie.
     */
    @Async
    @Transactional
    public void registrarLoginExitoso(Usuario usuario, String ipOrigen, String userAgent) {
        try {
            AuditoriaLogin auditoria = AuditoriaLogin.builder()
                    .usuario(usuario)
                    .loginIntentado(usuario.getLogin())
                    .tipoEvento(TipoEvento.LOGIN_EXITOSO)
                    .ipOrigen(ipOrigen)
                    .userAgent(userAgent)
                    .fecha(LocalDateTime.now())
                    .observaciones("Login exitoso")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // En modo Fire-and-Forget, nunca propagamos excepciones al hilo principal
            log.error("Error asíncrono al registrar login exitoso para usuario {}: {}",
                    usuario.getLogin(), e.getMessage());
        }
    }

    /**
     * Registrar login fallido (RF25.2)
     * OPTIMIZACIÓN APM: @Async → No bloquea el hilo que retorna el 401.
     */
    @Async
    @Transactional
    public void registrarLoginFallido(String login, String ipOrigen, String userAgent, String motivo) {
        try {
            AuditoriaLogin auditoria = AuditoriaLogin.builder()
                    .usuario(null) // No se encontró el usuario
                    .loginIntentado(login)
                    .tipoEvento(TipoEvento.LOGIN_FALLIDO)
                    .ipOrigen(ipOrigen)
                    .userAgent(userAgent)
                    .fecha(LocalDateTime.now())
                    .observaciones(motivo)
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error asíncrono al registrar login fallido para login {}: {}", login, e.getMessage());
        }
    }

    /**
     * Registrar logout
     * OPTIMIZACIÓN APM: @Async → El cliente recibe confirmación de logout
     * antes de que el registro en BD se complete.
     */
    @Async
    @Transactional
    public void registrarLogout(Usuario usuario) {
        try {
            AuditoriaLogin auditoria = AuditoriaLogin.builder()
                    .usuario(usuario)
                    .loginIntentado(usuario.getLogin())
                    .tipoEvento(TipoEvento.LOGOUT)
                    .fecha(LocalDateTime.now())
                    .observaciones("Logout exitoso")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error asíncrono al registrar logout para usuario {}: {}",
                    usuario.getLogin(), e.getMessage());
        }
    }

    /**
     * Registrar bloqueo de cuenta
     */
    @Async
    @Transactional
    public void registrarBloqueoCuenta(Usuario usuario, String motivo) {
        try {
            AuditoriaLogin auditoria = AuditoriaLogin.builder()
                    .usuario(usuario)
                    .loginIntentado(usuario.getLogin())
                    .tipoEvento(TipoEvento.BLOQUEO_CUENTA)
                    .fecha(LocalDateTime.now())
                    .observaciones(motivo)
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error asíncrono al registrar bloqueo para usuario {}: {}",
                    usuario.getLogin(), e.getMessage());
        }
    }

    /**
     * Registrar desbloqueo de cuenta
     */
    @Async
    @Transactional
    public void registrarDesbloqueo(Usuario usuario) {
        try {
            AuditoriaLogin auditoria = AuditoriaLogin.builder()
                    .usuario(usuario)
                    .loginIntentado(usuario.getLogin())
                    .tipoEvento(TipoEvento.DESBLOQUEO_CUENTA)
                    .fecha(LocalDateTime.now())
                    .observaciones("Cuenta desbloqueada")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error asíncrono al registrar desbloqueo para usuario {}: {}",
                    usuario.getLogin(), e.getMessage());
        }
    }

    /**
     * Registrar evento genérico
     */
    @Async
    public void registrarEvento(Usuario usuario, String observaciones, String ipOrigen) {
        log.info("AUDIT: {} | Usuario: {}", observaciones,
                (usuario != null ? usuario.getLogin() : "N/A"));
    }

    /**
     * Obtener historial de un usuario (operación de lectura — SÍNCRONA
     * intencionalmente)
     */
    @Transactional(readOnly = true)
    public List<AuditoriaLogin> obtenerHistorialUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    /**
     * Obtener eventos por tipo (operación de lectura — SÍNCRONA intencionalmente)
     */
    @Transactional(readOnly = true)
    public List<AuditoriaLogin> obtenerPorTipoEvento(TipoEvento tipoEvento) {
        return auditoriaRepository.findByTipoEventoOrderByFechaDesc(tipoEvento);
    }

    /**
     * Obtener eventos en un rango de fechas (operación de lectura — SÍNCRONA
     * intencionalmente)
     */
    @Transactional(readOnly = true)
    public List<AuditoriaLogin> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaBetween(inicio, fin);
    }
}