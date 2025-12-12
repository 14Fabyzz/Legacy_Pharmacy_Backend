package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.request.DevolucionRequestDTO;
import com.farmacia.ms_transacciones.dto.response.DevolucionResponseDTO;
import com.farmacia.ms_transacciones.service.DevolucionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
@Tag(name = "Devoluciones", description = "API para gestión de devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    @PostMapping
    @Operation(summary = "Crear nueva devolución")
    public ResponseEntity<DevolucionResponseDTO> crearDevolucion(@RequestBody DevolucionRequestDTO request) {
        return ResponseEntity.ok(devolucionService.crearDevolucion(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener devolución por ID")
    public ResponseEntity<DevolucionResponseDTO> obtenerDevolucion(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar devoluciones paginadas")
    public ResponseEntity<Page<DevolucionResponseDTO>> listarDevoluciones(Pageable pageable) {
        return ResponseEntity.ok(devolucionService.listarDevoluciones(pageable));
    }

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Buscar devoluciones por venta")
    public ResponseEntity<Page<DevolucionResponseDTO>> buscarPorVenta(
            @PathVariable Long ventaId,
            Pageable pageable) {
        return ResponseEntity.ok(devolucionService.buscarPorVenta(ventaId, pageable));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de devolución")
    public ResponseEntity<DevolucionResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(devolucionService.actualizarEstado(id, nuevoEstado));
    }
}
