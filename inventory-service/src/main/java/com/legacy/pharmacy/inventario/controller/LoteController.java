package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.entity.Lote;
import com.legacy.pharmacy.inventario.service.ProductoService; // O LoteService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lotes") // Ojo con el prefijo según tu Gateway
public class LoteController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private com.legacy.pharmacy.inventario.service.InventarioService inventarioService;

    // -------------------------------------------------------------
    // GET /lotes/disponibles/{productoId}
    // Retorna producto con detalles financieros y sus lotes disponibles
    // -------------------------------------------------------------
    @GetMapping("/disponibles/{productoId}")
    public ResponseEntity<com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO> obtenerLotesDisponibles(
            @PathVariable Integer productoId) {
        com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO resultado = productoService
                .obtenerProductoConLotesDisponibles(productoId);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/por-vencer")
    public ResponseEntity<List<Lote>> verLotesPorVencer() {
        // Reutilizamos tu lógica existente de 'proximos-vencer' (ej. 30 días)
        // O retornamos lista vacía si prefieres implementarlo luego
        return ResponseEntity.ok(productoService.buscarLotesProximosVencer(30));
    }

    // -------------------------------------------------------------
    // PATCH /lotes/{id}/baja
    // Da de baja formalmente un lote por algún motivo (ej. Vencimiento)
    // -------------------------------------------------------------
    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> darDeBajaLote(
            @PathVariable Integer id,
            @RequestBody com.legacy.pharmacy.inventario.dto.BajaLoteRequest request) {

        java.util.Map<String, Object> resultado = inventarioService.darDeBajaLote(id, request.getMotivo());
        return ResponseEntity.ok(resultado);
    }

    // Aquí puedes mover los otros métodos de lotes que tenías sueltos
    // como @GetMapping("/vencidos"), @GetMapping("/proximos-vencer"), etc.
}