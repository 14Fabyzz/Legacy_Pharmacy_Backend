package com.legacy.inventory.controller;

import com.legacy.inventory.dto.ProductoDTO;
import com.legacy.inventory.entity.Lote;
import com.legacy.inventory.entity.Producto;
import com.legacy.inventory.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventario")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // --- RUTAS DE PRODUCTOS ---

    // GET /productos (Opcional ?estado=ACTIVO)
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(productoService.listarProductos(estado));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/productos/codigo/{codigoInterno}")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigoInterno) {
        return ResponseEntity.ok(productoService.buscarPorCodigoInterno(codigoInterno));
    }

    @GetMapping("/productos/barras/{codigoBarras}")
    public ResponseEntity<Producto> buscarPorBarras(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(productoService.buscarPorCodigoBarras(codigoBarras));
    }

    @GetMapping("/productos/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@RequestBody @Valid ProductoDTO dto) {
        return ResponseEntity.ok(productoService.guardarProducto(dto));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody @Valid ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @PatchMapping("/productos/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        productoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado a " + nuevoEstado));
    }

    // --- RUTAS DE LOTES (CONSULTAS) ---

    @GetMapping("/lotes/producto/{productoId}")
    public ResponseEntity<List<Lote>> verLotesProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(productoService.buscarLotesPorProducto(productoId));
    }

    @GetMapping("/lotes/vencidos")
    public ResponseEntity<List<Lote>> verLotesVencidos() {
        return ResponseEntity.ok(productoService.buscarLotesVencidos());
    }

    @GetMapping("/lotes/proximos-vencer")
    public ResponseEntity<List<Lote>> verProximosVencer(@RequestParam(defaultValue = "90") int dias) {
        return ResponseEntity.ok(productoService.buscarLotesProximosVencer(dias));
    }

    @GetMapping("/lotes/{loteId}")
    public ResponseEntity<Lote> verLotePorId(@PathVariable Integer loteId) {
        return ResponseEntity.ok(productoService.buscarLotePorId(loteId));
    }
}