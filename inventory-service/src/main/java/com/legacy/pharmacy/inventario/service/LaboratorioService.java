package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.LaboratorioDTO;
import com.legacy.pharmacy.inventario.entity.Laboratorio;
import com.legacy.pharmacy.inventario.exception.BusinessException;
import com.legacy.pharmacy.inventario.exception.ResourceNotFoundException;
import com.legacy.pharmacy.inventario.repository.LaboratorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para Laboratorio.
 * Implementa CRUD completo con borrado lógico (soft delete).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Devuelve todos los laboratorios (activos e inactivos). Útil para la tabla de
     * administración.
     */
    @Transactional(readOnly = true)
    public List<LaboratorioDTO> listarTodos() {
        return laboratorioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve solo los laboratorios activos, ordenados por nombre. Útil para
     * combobox/selects.
     */
    @Transactional(readOnly = true)
    public List<LaboratorioDTO> listarActivos() {
        return laboratorioRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un laboratorio por ID. Lanza ResourceNotFoundException si no existe.
     */
    @Transactional(readOnly = true)
    public LaboratorioDTO obtenerPorId(Integer id) {
        Laboratorio laboratorio = findOrThrow(id);
        return toDTO(laboratorio);
    }

    // -------------------------------------------------------------------------
    // ESCRITURA
    // -------------------------------------------------------------------------

    /**
     * Crea un nuevo laboratorio.
     * Valida que el nombre no esté en blanco y no esté duplicado
     * (case-insensitive).
     */
    @Transactional
    public LaboratorioDTO crear(LaboratorioDTO dto) {
        validarNombreUnico(dto.getNombre(), null);

        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre(dto.getNombre().trim());
        laboratorio.setDescripcion(dto.getDescripcion());
        laboratorio.setPais(dto.getPais());
        laboratorio.setTelefono(dto.getTelefono());
        laboratorio.setEmail(dto.getEmail());
        laboratorio.setActivo(true);

        Laboratorio guardado = laboratorioRepository.save(laboratorio);
        log.info("Laboratorio creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return toDTO(guardado);
    }

    /**
     * Actualiza un laboratorio existente.
     * Valida existencia y que el nuevo nombre no colisione con otro laboratorio
     * distinto.
     */
    @Transactional
    public LaboratorioDTO actualizar(Integer id, LaboratorioDTO dto) {
        Laboratorio laboratorio = findOrThrow(id);
        validarNombreUnico(dto.getNombre(), id);

        laboratorio.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null)
            laboratorio.setDescripcion(dto.getDescripcion());
        if (dto.getPais() != null)
            laboratorio.setPais(dto.getPais());
        if (dto.getTelefono() != null)
            laboratorio.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null)
            laboratorio.setEmail(dto.getEmail());

        Laboratorio actualizado = laboratorioRepository.save(laboratorio);
        log.info("Laboratorio actualizado: id={}, nombre={}", actualizado.getId(), actualizado.getNombre());
        return toDTO(actualizado);
    }

    /**
     * Cambia el estado activo/inactivo de un laboratorio (Soft Delete / Toggle).
     * Nunca elimina el registro físicamente.
     */
    @Transactional
    public LaboratorioDTO cambiarEstado(Integer id) {
        Laboratorio laboratorio = findOrThrow(id);
        boolean nuevoEstado = !Boolean.TRUE.equals(laboratorio.getActivo());
        laboratorio.setActivo(nuevoEstado);
        laboratorioRepository.save(laboratorio);
        log.info("Laboratorio id={} → activo={}", id, nuevoEstado);
        return toDTO(laboratorio);
    }

    // -------------------------------------------------------------------------
    // HELPERS PRIVADOS
    // -------------------------------------------------------------------------

    private Laboratorio findOrThrow(Integer id) {
        return laboratorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Laboratorio con ID " + id + " no encontrado"));
    }

    /**
     * Valida que no exista otro laboratorio con el mismo nombre (case-insensitive).
     * 
     * @param nombre   El nombre a validar.
     * @param propioId El ID del registro que se está actualizando (null si es
     *                 creación).
     */
    private void validarNombreUnico(String nombre, Integer propioId) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre del laboratorio no puede estar en blanco");
        }
        Optional<Laboratorio> existente = laboratorioRepository.findByNombreIgnoreCase(nombre.trim());
        if (existente.isPresent() && !existente.get().getId().equals(propioId)) {
            throw new BusinessException(
                    "Ya existe un laboratorio con el nombre '" + nombre.trim() + "'");
        }
    }

    /** Convierte la entidad a DTO. */
    private LaboratorioDTO toDTO(Laboratorio l) {
        LaboratorioDTO dto = new LaboratorioDTO();
        dto.setId(l.getId());
        dto.setNombre(l.getNombre());
        dto.setDescripcion(l.getDescripcion());
        dto.setPais(l.getPais());
        dto.setTelefono(l.getTelefono());
        dto.setEmail(l.getEmail());
        dto.setActivo(l.getActivo());
        return dto;
    }
}
