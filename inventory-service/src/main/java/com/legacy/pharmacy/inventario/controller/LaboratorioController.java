package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.dto.LaboratorioDTO;
import com.legacy.pharmacy.inventario.service.LaboratorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para la gestión de Laboratorios.
 *
 * GET /api/laboratorios → Todos (tabla de administración)
 * GET /api/laboratorios/activos → Solo activos (combobox al crear producto)
 * GET /api/laboratorios/{id} → Uno por ID
 * POST /api/laboratorios → Crear
 * PUT /api/laboratorios/{id} → Actualizar
 * PATCH /api/laboratorios/{id}/estado → Toggle activo/inactivo (Soft Delete)
 */
@RestController
@RequestMapping("/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    /** Devuelve todos los laboratorios (activos e inactivos). */
    @GetMapping
    public ResponseEntity<List<LaboratorioDTO>> listarTodos() {
        return ResponseEntity.ok(laboratorioService.listarTodos());
    }

    /** Devuelve solo los laboratorios activos, ordenados por nombre. */
    @GetMapping("/activos")
    public ResponseEntity<List<LaboratorioDTO>> listarActivos() {
        return ResponseEntity.ok(laboratorioService.listarActivos());
    }

    /** Obtiene un laboratorio por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(laboratorioService.obtenerPorId(id));
    }

    /** Crea un nuevo laboratorio. Valida que el nombre no sea duplicado. */
    @PostMapping
    public ResponseEntity<LaboratorioDTO> crear(@Valid @RequestBody LaboratorioDTO dto) {
        LaboratorioDTO creado = laboratorioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Actualiza un laboratorio existente. Valida existencia y nombre único. */
    @PutMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody LaboratorioDTO dto) {
        return ResponseEntity.ok(laboratorioService.actualizar(id, dto));
    }

    /**
     * Cambia el estado de un laboratorio: activo → inactivo / inactivo → activo.
     * Implementa el Soft Delete (borrado lógico). Nunca borra el registro
     * físicamente.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<LaboratorioDTO> cambiarEstado(@PathVariable Integer id) {
        return ResponseEntity.ok(laboratorioService.cambiarEstado(id));
    }
}
