package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.BitacoraVentaDTO;
import com.farmacia.ms_transacciones.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<List<BitacoraVentaDTO>> obtenerBitacoraPorTurno(@PathVariable Long turnoId) {
        System.out.println("CONTROLLER-AUDITORIA: Recibida peticion GET /api/v1/auditoria/turno/" + turnoId);
        List<BitacoraVentaDTO> bitacora = ventaService.obtenerBitacoraPorTurno(turnoId);
        return ResponseEntity.ok(bitacora);
    }
}
