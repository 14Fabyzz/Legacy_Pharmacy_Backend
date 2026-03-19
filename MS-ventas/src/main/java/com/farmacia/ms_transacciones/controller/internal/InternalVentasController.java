package com.farmacia.ms_transacciones.controller.internal;

import com.farmacia.ms_transacciones.dto.internal.VentasRawDTO;
import com.farmacia.ms_transacciones.service.internal.InternalVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/ventas/internal")
public class InternalVentasController {

    @Autowired
    private InternalVentasService internalVentasService;

    @GetMapping("/datos-crudos")
    public ResponseEntity<VentasRawDTO> obtenerDatosCrudos(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fin,
            @RequestParam(value = "sucursalId", required = false) Integer sucursalId) {

        java.time.LocalDateTime inicioTime = inicio.atStartOfDay();
        java.time.LocalDateTime finTime = fin.atTime(java.time.LocalTime.MAX);

        VentasRawDTO datos = internalVentasService.obtenerDatosCrudos(inicioTime, finTime);
        return ResponseEntity.ok(datos);
    }
}
