package com.legacy.pharmacy.reportes.controller;

import com.legacy.pharmacy.reportes.config.UserContext;
import com.legacy.pharmacy.reportes.dto.ReporteVentasConsolidadasDTO;
import com.legacy.pharmacy.reportes.enums.Periodicidad;
import com.legacy.pharmacy.reportes.exception.BusinessException;
import com.legacy.pharmacy.reportes.service.ReporteVentasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para reportes de ventas consolidadas.
 *
 * Endpoint:
 * GET /api/v1/reportes/ventas/consolidado
 * ?fechaInicio=2026-01-01
 * &fechaFin=2026-01-31
 * &periodicidad=MENSUAL
 * &sucursalId=1 (opcional)
 */
@RestController
@RequestMapping("/ventas")
public class ReporteVentasController {

    private static final Logger log = LoggerFactory.getLogger(ReporteVentasController.class);

    private final ReporteVentasService reporteVentasService;

    public ReporteVentasController(ReporteVentasService reporteVentasService) {
        this.reporteVentasService = reporteVentasService;
    }

    @GetMapping("/consolidado")
    public ResponseEntity<ReporteVentasConsolidadasDTO> obtenerReporteConsolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam Periodicidad periodicidad,
            @RequestParam(required = false) Integer sucursalId) {

        // Verificar que el usuario sea ADMIN/ADMINISTRADOR
        if (!UserContext.isAdmin()) {
            throw new BusinessException(
                    "Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden generar reportes");
        }

        log.info("Solicitud de reporte consolidado: {} a {}, periodicidad={}, sucursal={}, usuario={}",
                fechaInicio, fechaFin, periodicidad, sucursalId, UserContext.getUsername());

        ReporteVentasConsolidadasDTO reporte = reporteVentasService.generarReporteConsolidado(
                fechaInicio, fechaFin, periodicidad, sucursalId);

        return ResponseEntity.ok(reporte);
    }
}
