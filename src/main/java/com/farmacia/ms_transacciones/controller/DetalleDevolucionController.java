package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.response.DetalleDevolucionResponseDTO;
import com.farmacia.ms_transacciones.entity.DetalleDevolucion;
import com.farmacia.ms_transacciones.service.DetalleDevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-devolucion")
@RequiredArgsConstructor
public class DetalleDevolucionController {

    private final DetalleDevolucionService detalleDevolucionService;

    @GetMapping("/{id}")
    public ResponseEntity<DetalleDevolucionResponseDTO> obtenerPorId(@PathVariable Long id) {
        DetalleDevolucionResponseDTO detalle = detalleDevolucionService.obtenerPorId(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/devolucion/{devolucionId}")
    public ResponseEntity<List<DetalleDevolucionResponseDTO>> obtenerPorDevolucion(@PathVariable Long devolucionId) {
        List<DetalleDevolucionResponseDTO> detalles = detalleDevolucionService.obtenerPorDevolucion(devolucionId);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DetalleDevolucionResponseDTO>> obtenerPorEstado(@PathVariable String estado) {
        List<DetalleDevolucionResponseDTO> detalles = detalleDevolucionService.obtenerPorEstado(estado);
        return ResponseEntity.ok(detalles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleDevolucion> actualizarDetalleDevolucion(
            @PathVariable Long id,
            @RequestBody DetalleDevolucion detalle) {
        DetalleDevolucion detalleActualizado = detalleDevolucionService.actualizarDetalleDevolucion(id, detalle);
        return ResponseEntity.ok(detalleActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleDevolucion(@PathVariable Long id) {
        detalleDevolucionService.eliminarDetalleDevolucion(id);
        return ResponseEntity.noContent().build();
    }
}