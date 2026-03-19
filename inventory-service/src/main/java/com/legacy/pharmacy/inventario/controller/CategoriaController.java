package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.dto.CategoriaDTO;
import com.legacy.pharmacy.inventario.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para la gestión de Categorías.
 *
 * GET /api/categorias → Todas (tabla de administración)
 * GET /api/categorias/activas → Solo activas (combobox al crear producto)
 * GET /api/categorias/{id} → Una por ID
 * POST /api/categorias → Crear
 * PUT /api/categorias/{id} → Actualizar
 * PATCH /api/categorias/{id}/estado → Toggle activo/inactivo (Soft Delete)
 */
@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /** Devuelve todas las categorías (activas e inactivas). */
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodos() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    /** Devuelve solo las categorías activas, ordenadas por nombre. */
    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaDTO>> listarActivos() {
        return ResponseEntity.ok(categoriaService.listarActivos());
    }

    /** Obtiene una categoría por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    /** Crea una nueva categoría. Valida que el nombre no sea duplicado. */
    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO creada = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /** Actualiza una categoría existente. Valida existencia y nombre único. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    /**
     * Cambia el estado de una categoría: activa → inactiva / inactiva → activa.
     * Implementa el Soft Delete (borrado lógico). Nunca borra el registro
     * físicamente.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CategoriaDTO> cambiarEstado(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaService.cambiarEstado(id));
    }
}
