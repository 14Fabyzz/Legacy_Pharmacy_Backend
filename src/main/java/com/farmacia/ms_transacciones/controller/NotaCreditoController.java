package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.response.NotaCreditoResponseDTO;
import com.farmacia.ms_transacciones.service.NotaCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notas-credito")
@RequiredArgsConstructor
@Tag(name = "Notas de Crédito", description = "API para gestión de notas de crédito")
public class NotaCreditoController {

    private final NotaCreditoService notaCreditoService;

    @PostMapping("/generar/{devolucionId}")
    @Operation(summary = "Generar nota de crédito a partir de una devolución")
    public ResponseEntity<NotaCreditoResponseDTO> generarNotaCredito(@PathVariable Long devolucionId) {
        return ResponseEntity.ok(notaCreditoService.generarNotaCredito(devolucionId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota de crédito por ID")
    public ResponseEntity<NotaCreditoResponseDTO> obtenerNotaCredito(@PathVariable Long id) {
        return ResponseEntity.ok(notaCreditoService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar notas de crédito paginadas")
    public ResponseEntity<Page<NotaCreditoResponseDTO>> listarNotasCredito(Pageable pageable) {
        return ResponseEntity.ok(notaCreditoService.listarNotasCredito(pageable));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar notas de crédito por cliente")
    public ResponseEntity<Page<NotaCreditoResponseDTO>> buscarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {
        return ResponseEntity.ok(notaCreditoService.buscarPorCliente(clienteId, pageable));
    }

    @PutMapping("/{id}/aplicar")
    @Operation(summary = "Aplicar monto de nota de crédito")
    public ResponseEntity<NotaCreditoResponseDTO> aplicarNotaCredito(
            @PathVariable Long id,
            @RequestParam Double monto) {
        return ResponseEntity.ok(notaCreditoService.aplicarNotaCredito(id, monto));
    }
}
