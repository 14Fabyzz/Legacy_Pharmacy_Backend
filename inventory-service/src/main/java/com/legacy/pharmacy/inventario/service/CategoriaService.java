package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.CategoriaDTO;
import com.legacy.pharmacy.inventario.entity.Categoria;
import com.legacy.pharmacy.inventario.exception.BusinessException;
import com.legacy.pharmacy.inventario.exception.ResourceNotFoundException;
import com.legacy.pharmacy.inventario.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para Categoria.
 * Implementa CRUD completo con borrado lógico (soft delete).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Devuelve todas las categorías (activas e inactivas). Útil para la tabla de
     * administración.
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve solo las categorías activas, ordenadas por nombre. Útil para
     * combobox/selects.
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarActivos() {
        return categoriaRepository.findByActivaTrueOrderByNombreAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una categoría por ID. Lanza ResourceNotFoundException si no existe.
     */
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Integer id) {
        Categoria categoria = findOrThrow(id);
        return toDTO(categoria);
    }

    // -------------------------------------------------------------------------
    // ESCRITURA
    // -------------------------------------------------------------------------

    /**
     * Crea una nueva categoría.
     * Valida que el nombre no esté en blanco y no esté duplicado
     * (case-insensitive).
     */
    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        validarNombreUnico(dto.getNombre(), null);

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActiva(true);

        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada: id={}, nombre={}", guardada.getId(), guardada.getNombre());
        return toDTO(guardada);
    }

    /**
     * Actualiza una categoría existente.
     * Valida existencia y que el nuevo nombre no colisione con otra categoría
     * distinta.
     */
    @Transactional
    public CategoriaDTO actualizar(Integer id, CategoriaDTO dto) {
        Categoria categoria = findOrThrow(id);
        validarNombreUnico(dto.getNombre(), id);

        categoria.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) {
            categoria.setDescripcion(dto.getDescripcion());
        }

        Categoria actualizada = categoriaRepository.save(categoria);
        log.info("Categoría actualizada: id={}, nombre={}", actualizada.getId(), actualizada.getNombre());
        return toDTO(actualizada);
    }

    /**
     * Cambia el estado activo/inactivo de una categoría (Soft Delete / Toggle).
     * Nunca elimina el registro físicamente.
     */
    @Transactional
    public CategoriaDTO cambiarEstado(Integer id) {
        Categoria categoria = findOrThrow(id);
        boolean nuevoEstado = !Boolean.TRUE.equals(categoria.getActiva());
        categoria.setActiva(nuevoEstado);
        categoriaRepository.save(categoria);
        log.info("Categoría id={} → activa={}", id, nuevoEstado);
        return toDTO(categoria);
    }

    // -------------------------------------------------------------------------
    // HELPERS PRIVADOS
    // -------------------------------------------------------------------------

    private Categoria findOrThrow(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría con ID " + id + " no encontrada"));
    }

    /**
     * Valida que no exista otra categoría con el mismo nombre (case-insensitive).
     * 
     * @param nombre   El nombre a validar.
     * @param propioId El ID del registro que se está actualizando (null si es
     *                 creación).
     */
    private void validarNombreUnico(String nombre, Integer propioId) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de la categoría no puede estar en blanco");
        }
        Optional<Categoria> existente = categoriaRepository.findByNombreIgnoreCase(nombre.trim());
        if (existente.isPresent() && !existente.get().getId().equals(propioId)) {
            throw new BusinessException(
                    "Ya existe una categoría con el nombre '" + nombre.trim() + "'");
        }
    }

    /** Convierte la entidad a DTO. */
    private CategoriaDTO toDTO(Categoria c) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setActiva(c.getActiva());
        return dto;
    }
}
