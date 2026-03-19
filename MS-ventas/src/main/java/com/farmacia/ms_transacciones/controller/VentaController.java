package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.CrearVentaDTO;
import com.farmacia.ms_transacciones.dto.VentaResponseDTO;
import com.farmacia.ms_transacciones.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping("/realizar")
    public ResponseEntity<VentaResponseDTO> crearVenta(@RequestBody CrearVentaDTO dto) {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion POST /realizar");
        return ResponseEntity.ok(ventaService.crearVenta(dto));
    }

    @GetMapping
    public ResponseEntity<java.util.List<VentaResponseDTO>> obtenerHistorialVentas() {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion GET /api/v1/ventas");
        return ResponseEntity.ok(ventaService.obtenerHistorialVentas());
    }

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<java.util.List<VentaResponseDTO>> obtenerHistorialVentasPorTurno(
            @PathVariable Long turnoId) {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion GET /api/v1/ventas/turno/" + turnoId);
        return ResponseEntity.ok(ventaService.obtenerHistorialVentasPorTurno(turnoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id) {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion GET /api/v1/ventas/" + id);
        return ResponseEntity.ok(ventaService.obtenerVentaPorId(id));
    }

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<VentaResponseDTO> procesarDevolucion(
            @PathVariable Long id,
            @RequestBody com.farmacia.ms_transacciones.dto.DevolucionRequestDTO solicitud) {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion POST /api/v1/ventas/" + id + "/devolucion");
        return ResponseEntity.ok(ventaService.procesarDevolucion(id, solicitud));
    }

    @GetMapping("/semanales")
    public ResponseEntity<java.util.List<java.math.BigDecimal>> obtenerVentasSemanales() {
        System.out.println("CONTROLLER-VENTAS: Recibida peticion GET /api/v1/ventas/semanales");
        return ResponseEntity.ok(ventaService.obtenerVentasSemanales());
    }
}
