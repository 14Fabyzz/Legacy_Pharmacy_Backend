package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.dto.EntradaMercanciaDTO;
import com.legacy.pharmacy.inventario.service.InventarioService;
import jakarta.validation.Valid; // Para validar que el JSON no venga vacío
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // Indica que esta clase responde JSON
@RequestMapping("") // La ruta base: localhost:8080/api/v1/inventario
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private com.legacy.pharmacy.inventario.service.ProductoService productoService;

    @PostMapping("/entrada")
    public ResponseEntity<Map<String, Object>> registrarEntrada(@RequestBody @Valid EntradaMercanciaDTO entradaDTO) {
        try {
            // Recibimos el Map con la respuesta
            Map<String, Object> resultado = inventarioService.registrarEntrada(entradaDTO);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error en la consola para verlo mejor
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/lotes/entrada-masiva")
    public ResponseEntity<?> registrarEntradaMasiva(@RequestBody @Valid List<EntradaMercanciaDTO> entradas) {
        try {
            var resultado = inventarioService.registrarEntradaMasiva(entradas);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- BÚSQUEDA RAPIDA PARA FRONTEND (Corrección Error 500) ---
    @GetMapping("/buscar")
    public ResponseEntity<List<com.legacy.pharmacy.inventario.dto.ProductoDTO>> buscar(
            @RequestParam("termino") String termino) {
        List<com.legacy.pharmacy.inventario.entity.Producto> productos = productoService.buscarPorNombre(termino);

        List<com.legacy.pharmacy.inventario.dto.ProductoDTO> dtos = productos.stream().map(p -> {
            com.legacy.pharmacy.inventario.dto.ProductoDTO dto = new com.legacy.pharmacy.inventario.dto.ProductoDTO();
            dto.setCodigoInterno(p.getCodigoInterno());
            dto.setCodigoBarras(p.getCodigoBarras());
            dto.setNombreComercial(p.getNombreComercial());
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setLaboratorioId(p.getLaboratorio().getId());
            if (p.getPrincipioActivo() != null) {
                dto.setPrincipioActivoId(p.getPrincipioActivo().getId());
            }
            dto.setConcentracion(p.getConcentracion());
            dto.setPresentacion(p.getPresentacion());
            dto.setRegistroInvima(p.getRegistroInvima());
            dto.setPrecioCompraReferencia(p.getPrecioCompraReferencia());
            dto.setPorcentajeGanancia(p.getPorcentajeGanancia());
            dto.setIvaPorcentaje(p.getIvaPorcentaje());

            // Campos calculados
            dto.setPrecioVentaBase(p.getPrecioVentaBase());
            dto.setPrecioVentaTotal(p.getPrecioVentaTotal());
            dto.setPrecioVentaUnidad(p.getPrecioVentaUnidad());
            dto.setPrecioVentaBlister(p.getPrecioVentaBlister());

            dto.setStockMinimo(p.getStockMinimo());
            dto.setEsControlado(p.getEsControlado());
            dto.setRefrigerado(p.getRefrigerado());
            dto.setTipo(p.getTipo().name());

            dto.setEsFraccionable(p.getEsFraccionable());
            dto.setUnidadesPorCaja(p.getUnidadesPorCaja());
            dto.setUnidadesPorBlister(p.getUnidadesPorBlister());

            return dto;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // --- BÚSQUEDA POS OPTIMIZADA (Laboratorio + Stock) ---
    // --- BÚSQUEDA POS OPTIMIZADA (Refactor: Reutilización de Lógica) ---
    @GetMapping("/busqueda-pos")
    public ResponseEntity<List<com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO>> busquedaPos(
            @RequestParam("termino") String termino) {

        // 1. Buscamos productos (Filtro Correcto: CONTAINING / LIKE)
        List<com.legacy.pharmacy.inventario.entity.Producto> productos = productoService.buscarPorNombre(termino);

        // 2. Reutilizamos la lógica de "Ficha Completa" (ProductoService)
        List<com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO> resultados = productos.stream()
                .map(p -> productoService.obtenerProductoConLotesDisponibles(p.getId()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(resultados);
    }

    // --- NUEVO ENDPOINT DE SALIDA ---
    // POST: http://localhost:8080/api/v1/inventario/salida
    @PostMapping("/salida")
    public ResponseEntity<?> registrarSalida(
            @RequestBody @Valid com.legacy.pharmacy.inventario.dto.SalidaMercanciaDTO salidaDTO) {
        try {
            var resultado = inventarioService.registrarSalida(salidaDTO);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            // Esto capturará el error "Stock insuficiente" si intentas vender más de lo que
            // tienes
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}