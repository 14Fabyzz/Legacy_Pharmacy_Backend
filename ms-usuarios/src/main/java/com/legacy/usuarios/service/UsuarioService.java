package com.legacy.usuarios.service;

import com.legacy.usuarios.dto.CambioPasswordDTO;
import com.legacy.usuarios.dto.UsuarioCreateDTO;
import com.legacy.usuarios.dto.UsuarioDTO;
import com.legacy.usuarios.dto.UsuarioUpdateDTO;
import com.legacy.usuarios.entity.Rol;
import com.legacy.usuarios.entity.Usuario;
import com.legacy.usuarios.entity.Usuario.EstadoUsuario;
import com.legacy.usuarios.exception.BusinessException;
import com.legacy.usuarios.exception.NotFoundException;
import com.legacy.usuarios.exception.UnauthorizedException;
import com.legacy.usuarios.repository.RolRepository;
import com.legacy.usuarios.repository.UsuarioRepository;
import com.legacy.usuarios.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordUtil passwordUtil;
    private final AuditoriaService auditoriaService;

    /**
     * Obtener todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener solo usuarios activos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerActivos() {
        return usuarioRepository.findAllActivos().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));
        return convertirADTO(usuario);
    }

    /**
     * Obtener usuario por login
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorLogin(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con login: " + login));
        return convertirADTO(usuario);
    }

    /**
     * Crear un nuevo usuario (RF22.1)
     */
    @Transactional
    public UsuarioDTO crear(UsuarioCreateDTO dto) {
        // Validar que no exista el login
        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new BusinessException("Ya existe un usuario con el login: " + dto.getLogin());
        }

        // Validar que no exista la cédula
        if (usuarioRepository.existsByCedula(dto.getCedula())) {
            throw new BusinessException("Ya existe un usuario con la cédula: " + dto.getCedula());
        }

        // El email debe ser único en todo el sistema
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un usuario registrado con el email: " + dto.getEmail());
        }

        // Validar que el rol exista
        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + dto.getRolId()));

        // Crear usuario con todos los campos, incluyendo email y telefono
        Usuario usuario = Usuario.builder()
                .nombreCompleto(dto.getNombreCompleto())
                .cedula(dto.getCedula())
                .login(dto.getLogin())
                .passwordHash(passwordUtil.encodePassword(dto.getPassword()))
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .rol(rol)
                .sucursalId(dto.getSucursalId())
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidos(0)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        // Registrar en auditoría
        auditoriaService.registrarEvento(
                guardado,
                "Usuario creado: " + guardado.getLogin(),
                null);

        return convertirADTO(guardado);
    }

    /**
     * Actualizar un usuario (RF22.2)
     */
    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        // Actualizar nombre si viene
        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(dto.getNombreCompleto());
        }

        // Actualizar email si viene: verificar que no esté en uso por OTRO usuario
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            usuarioRepository.findByEmail(dto.getEmail())
                    .filter(existente -> !existente.getId().equals(id))
                    .ifPresent(existente -> {
                        throw new BusinessException("El email " + dto.getEmail() + " ya está registrado por otro usuario");
                    });
            usuario.setEmail(dto.getEmail());
        }

        // Actualizar teléfono si viene
        if (dto.getTelefono() != null && !dto.getTelefono().isBlank()) {
            usuario.setTelefono(dto.getTelefono());
        }

        // Actualizar rol si viene
        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + dto.getRolId()));
            usuario.setRol(rol);
        }

        // Actualizar sucursal si viene
        if (dto.getSucursalId() != null) {
            usuario.setSucursalId(dto.getSucursalId());
        }

        // Actualizar contraseña si viene
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordUtil.encodePassword(dto.getPassword()));
        }

        // Actualizar estado si viene
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());

            // Si se desbloquea, resetear intentos fallidos
            if (dto.getEstado() == EstadoUsuario.ACTIVO) {
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
            }
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        // Registrar en auditoría
        auditoriaService.registrarEvento(
                actualizado,
                "Usuario actualizado: " + actualizado.getLogin(),
                null);

        return convertirADTO(actualizado);
    }

    /**
     * Desactivar usuario (RF22.3)
     */
    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);

        // Registrar en auditoría
        auditoriaService.registrarEvento(
                usuario,
                "Usuario desactivado: " + usuario.getLogin(),
                null);
    }

    /**
     * Cambiar estado de usuario explícitamente (ACTIVO / INACTIVO / BLOQUEADO)
     * Al reactivar (ACTIVO) se resetean los intentos fallidos y la fecha de
     * bloqueo.
     */
    @Transactional
    public UsuarioDTO cambiarEstado(Long id, EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        EstadoUsuario estadoAnterior = usuario.getEstado();
        usuario.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoUsuario.ACTIVO) {
            usuario.setIntentosFallidos(0);
            usuario.setFechaBloqueo(null);
        }

        usuarioRepository.save(usuario);

        auditoriaService.registrarEvento(
                usuario,
                "Estado de usuario cambió de " + estadoAnterior + " a " + nuevoEstado + ": " + usuario.getLogin(),
                null);

        return convertirADTO(usuario);
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void cambiarPassword(Long id, CambioPasswordDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar contraseña actual
        if (!passwordUtil.matches(dto.getPasswordActual(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordUtil.encodePassword(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);

        // Registrar en auditoría
        auditoriaService.registrarEvento(
                usuario,
                "Contraseña cambiada para usuario: " + usuario.getLogin(),
                null);
    }

    /**
     * Bloquear usuario por intentos fallidos (RF25.1)
     */
    @Transactional
    public void bloquearPorIntentosFallidos(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con login: " + login));

        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setFechaBloqueo(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Registrar en auditoría
        auditoriaService.registrarBloqueoCuenta(usuario, "Bloqueado por intentos fallidos");
    }

    /**
     * Registrar intento fallido
     */
    @Transactional
    public void registrarIntentoFallido(String login) {
        usuarioRepository.findByLogin(login).ifPresent(usuario -> {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

            // Bloquear si supera 5 intentos
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setEstado(EstadoUsuario.BLOQUEADO);
                usuario.setFechaBloqueo(LocalDateTime.now());
            }

            usuarioRepository.save(usuario);
        });
    }

    /**
     * Resetear intentos fallidos
     */
    @Transactional
    public void resetearIntentosFallidos(String login) {
        usuarioRepository.findByLogin(login).ifPresent(usuario -> {
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
        });
    }

    /**
     * Actualizar último acceso
     */
    @Transactional
    public void actualizarUltimoAcceso(String login) {
        usuarioRepository.findByLogin(login).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    /**
     * Convertir entidad a DTO
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .cedula(usuario.getCedula())
                .login(usuario.getLogin())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rolId(usuario.getRol().getId())
                .rolNombre(usuario.getRol().getNombre())
                .sucursalId(usuario.getSucursalId())
                .estado(usuario.getEstado())
                .intentosFallidos(usuario.getIntentosFallidos())
                .fechaBloqueo(usuario.getFechaBloqueo())
                .fechaCreacion(usuario.getFechaCreacion())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .build();
    }
}
