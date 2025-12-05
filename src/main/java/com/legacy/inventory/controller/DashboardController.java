package com.legacy.inventory.controller;

import com.legacy.inventory.entity.ProductoCard;
import com.legacy.inventory.repository.ProductoCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private ProductoCardRepository cardRepository;

    // GET http://localhost:8080/api/v1/dashboard/cards
    // Sirve para pintar la grilla principal de productos
    @GetMapping("/cards")
    public ResponseEntity<List<ProductoCard>> obtenerTarjetas(@RequestParam(required = false) String busqueda) {
        if (busqueda != null && !busqueda.isEmpty()) {
            // Enviamos la misma cadena de búsqueda 3 veces (para que busque en los 3 campos)
            return ResponseEntity.ok(cardRepository.findByNombreContainingIgnoreCaseOrCodigoInternoContainingIgnoreCaseOrCodigoBarrasContainingIgnoreCase(busqueda, busqueda, busqueda));
        }
        return ResponseEntity.ok(cardRepository.findAll());
    }
}