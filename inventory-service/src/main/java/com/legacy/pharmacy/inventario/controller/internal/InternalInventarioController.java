package com.legacy.pharmacy.inventario.controller.internal;

import com.legacy.pharmacy.inventario.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.inventario.service.internal.InternalInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/inventario/internal")
public class InternalInventarioController {

    @Autowired
    private InternalInventarioService internalInventarioService;

    @GetMapping("/datos-crudos")
    public ResponseEntity<InventarioRawDTO> obtenerDatosCrudos(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {

        java.time.LocalDateTime inicioTime = inicio.atStartOfDay();
        java.time.LocalDateTime finTime = fin.atTime(java.time.LocalTime.MAX);

        InventarioRawDTO datos = internalInventarioService.obtenerDatosCrudos(inicioTime, finTime);
        return ResponseEntity.ok(datos);
    }
}
