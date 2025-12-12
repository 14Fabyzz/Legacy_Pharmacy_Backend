package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.dto.VentaRequestDTO;
import com.farmacia.ms_transacciones.entity.Venta; // O Venta
import com.farmacia.ms_transacciones.service.VentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // <--- IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    // Endpoint para crear una nueva venta (HU-05)
    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequestDTO ventaRequest,
                                             Authentication authentication) { // 1. Inyectamos Authentication

        // 2. Sacamos el ID del vendedor desde el Token (Seguro y Automático)
        String vendedorId = authentication.getName();

        // 3. Ya no necesitamos @RequestHeader, usamos el ID extraído
        Venta nuevaVenta = ventasService.crearVenta(ventaRequest, vendedorId);

        return ResponseEntity.ok(nuevaVenta);
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventasService.obtenerTodasLasVentas());
    }
}
