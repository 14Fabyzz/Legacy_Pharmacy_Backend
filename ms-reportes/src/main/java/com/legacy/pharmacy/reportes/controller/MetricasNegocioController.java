package com.legacy.pharmacy.reportes.controller;

import com.legacy.pharmacy.reportes.config.UserContext;
import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
import com.legacy.pharmacy.reportes.dto.ResumenInteligenteResponseDTO;
import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.exception.BusinessException;
import com.legacy.pharmacy.reportes.service.ResumenInteligenteService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorInventarioService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorVentasService;
import com.legacy.pharmacy.reportes.service.metricas.ReportesAnaliticosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MetricasNegocioController {

    private static final Logger log = LoggerFactory.getLogger(MetricasNegocioController.class);

    private final ResumenInteligenteService resumenInteligenteService;
    private final CalculadorInventarioService calculadorInventarioService;
    private final CalculadorVentasService calculadorVentasService;
    private final ReportesAnaliticosService reportesAnaliticosService;

    public MetricasNegocioController(ResumenInteligenteService resumenInteligenteService,
                                     CalculadorInventarioService calculadorInventarioService,
                                     CalculadorVentasService calculadorVentasService,
                                     ReportesAnaliticosService reportesAnaliticosService) {
        this.resumenInteligenteService = resumenInteligenteService;
        this.calculadorInventarioService = calculadorInventarioService;
        this.calculadorVentasService = calculadorVentasService;
        this.reportesAnaliticosService = reportesAnaliticosService;
    }

    @GetMapping("/dashboard/inventario")
    public ResponseEntity<GestionInventarioMetricasDTO> obtenerMetricasInventario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden consultar estas métricas");
        }
        GestionInventarioMetricasDTO metricas = calculadorInventarioService.calcularPulso(fechaInicio, fechaFin, sucursalId);
        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/dashboard/ventas")
    public ResponseEntity<VentasClientesMetricasDTO> obtenerMetricasVentasYClientes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden consultar estas métricas");
        }
        VentasClientesMetricasDTO metricas = calculadorVentasService.calcularMotor(fechaInicio, fechaFin, sucursalId);
        return ResponseEntity.ok(metricas);
    }

    public static class ResumenRequest {
        public LocalDate fechaInicio;
        public LocalDate fechaFin;
        public Integer sucursalId;
    }

    @PostMapping("/resumen-inteligente")
    public ResponseEntity<ResumenInteligenteResponseDTO> generarResumenInteligente(@RequestBody ResumenRequest req) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(
                    "Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden generar resúmenes con IA");
        }

        log.info("Solicitud de resumen inteligente de métricas: {} a {}, sucursal={}, usuario={}",
                req.fechaInicio, req.fechaFin, req.sucursalId, UserContext.getUsername());

        ResumenInteligenteResponseDTO resumen = resumenInteligenteService.generarResumen(
                req.fechaInicio, req.fechaFin, req.sucursalId);

        return ResponseEntity.ok(resumen);
    }

    // ==========================================
    // ENDPOINTS REPORTES ANALITICOS
    // ==========================================
    @GetMapping("/analitico/ventas-cliente")
    public ResponseEntity<List<Map<String, Object>>> getVentasCliente(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getVentasPorCliente(fechaInicio, fechaFin, sucursalId));
    }

    @GetMapping("/analitico/ventas-cliente-producto")
    public ResponseEntity<List<Map<String, Object>>> getVentasClienteProducto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getVentasClienteProducto(fechaInicio, fechaFin, sucursalId));
    }

    @GetMapping("/analitico/consolidado")
    public ResponseEntity<List<Map<String, Object>>> getConsolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getConsolidadoVentas(fechaInicio, fechaFin, sucursalId));
    }

    @GetMapping("/analitico/comparativo")
    public ResponseEntity<List<Map<String, Object>>> getComparativo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getComparativoMensual(fechaInicio, fechaFin, sucursalId));
    }

    // ==========================================
    // ENDPOINTS RENDIMIENTO INVENTARIO
    // ==========================================

    @GetMapping("/analitico/top-10-productos")
    public ResponseEntity<List<Map<String, Object>>> getTop10Productos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Integer laboratorioId) {
        return ResponseEntity.ok(reportesAnaliticosService.getTop10Productos(fechaInicio, fechaFin, sucursalId, categoriaId, laboratorioId));
    }

    @GetMapping("/analitico/baja-rotacion")
    public ResponseEntity<List<Map<String, Object>>> getBajaRotacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getProductosBajaRotacion(fechaInicio, fechaFin, sucursalId));
    }

    @GetMapping("/analitico/comparativo-producto")
    public ResponseEntity<List<Map<String, Object>>> getComparativoProducto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reportesAnaliticosService.getComparativoProducto(fechaInicio, fechaFin, sucursalId));
    }
}
