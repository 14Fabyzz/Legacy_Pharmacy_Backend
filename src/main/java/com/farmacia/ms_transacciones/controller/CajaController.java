package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.AperturaTurnoDTO;
import com.farmacia.ms_transacciones.dto.CierreTurnoDTO;
import com.farmacia.ms_transacciones.entity.TurnoCaja;
import com.farmacia.ms_transacciones.service.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importante
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/caja")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    // 1. ABRIR TURNO (HU-09.1) - VERSIÓN FINAL CON TOKEN
    @PostMapping("/abrir")
    public ResponseEntity<?> abrirTurno(@RequestBody AperturaTurnoDTO dto,
                                        Authentication authentication) {

        // 1. Sacamos el ID del usuario del Token (seguridad)
        String usuarioId = authentication.getName();

        // 2. Se lo asignamos al DTO (porque el JSON no lo trae)
        dto.setUsuarioId(usuarioId);

        try {
            TurnoCaja turno = cajaService.abrirTurno(dto);
            return ResponseEntity.ok(turno);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. CERRAR TURNO (HU-09.5) - RESTAURADO Y ACTUALIZADO
    @PostMapping("/cerrar")
    public ResponseEntity<?> cerrarTurno(@RequestBody CierreTurnoDTO dto,
                                         Authentication authentication) {

        // Usamos también el Token para saber quién está cerrando
        String usuarioId = authentication.getName();

        try {
            TurnoCaja turnoCerrado = cajaService.cerrarTurno(usuarioId, dto);
            return ResponseEntity.ok(turnoCerrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. CONSULTAR ESTADO
    @GetMapping("/estado")
    public ResponseEntity<?> consultarEstado(Authentication authentication) {

        String usuarioId = authentication.getName();

        return cajaService.obtenerTurnoActivo(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}