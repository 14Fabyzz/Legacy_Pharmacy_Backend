package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.dto.AuditoriaDTO;
import com.legacy.pharmacy.inventario.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    // GET /api/inventario/movimientos/auditoria
    @GetMapping("/auditoria")
    public ResponseEntity<List<AuditoriaDTO>> obtenerAuditoria() {
        List<AuditoriaDTO> historial = movimientoService.obtenerHistorialCompleto();

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historial);
    }

    // GET /api/inventario/movimientos/producto/{id}
    @GetMapping("/producto/{id}")
    public ResponseEntity<List<com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO>> obtenerKardexProducto(
            @PathVariable Integer id) {
        return ResponseEntity.ok(movimientoService.obtenerKardexProducto(id));
    }

    // GET /api/inventario/movimientos/recientes
    @GetMapping("/recientes")
    public ResponseEntity<List<com.legacy.pharmacy.inventario.dto.MovimientoBitacoraDTO>> obtenerBitacoraReciente() {
        return ResponseEntity.ok(movimientoService.obtenerBitacoraReciente());
    }

    // GET /api/inventario/movimientos (Default to Recientes)
    @GetMapping("")
    public ResponseEntity<List<com.legacy.pharmacy.inventario.dto.MovimientoBitacoraDTO>> listarMovimientos() {
        return ResponseEntity.ok(movimientoService.obtenerBitacoraReciente());
    }
}