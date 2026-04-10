package com.legacy.pharmacy.reportes.controller;

import com.legacy.pharmacy.reportes.dto.cierre.CierreTurnoIntegralDTO;
import com.legacy.pharmacy.reportes.service.metricas.CierreTurnoAnaliticoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cierre-turno")
public class CierreTurnoReporteController {

    private final CierreTurnoAnaliticoService cierreTurnoService;

    public CierreTurnoReporteController(CierreTurnoAnaliticoService cierreTurnoService) {
        this.cierreTurnoService = cierreTurnoService;
    }

    /**
     * Obtiene el reporte integral de un turno (conciliación + desglose de movimientos).
     * @param turnoId El ID del turno cerrado en MS-Ventas.
     * @return 200 OK con el DTO o 404 si no se encontró.
     */
    @GetMapping("/{turnoId}")
    public ResponseEntity<CierreTurnoIntegralDTO> obtenerCierreTurnoIntegral(@PathVariable Long turnoId) {
        CierreTurnoIntegralDTO reporte = cierreTurnoService.generarReporteIntegralCierre(turnoId);
        
        if (reporte == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reporte);
    }

    /**
     * Obtiene una lista de consolidación de cierres de turno en un rango de fechas.
     */
    @GetMapping("/rango")
    public ResponseEntity<java.util.List<com.legacy.pharmacy.reportes.dto.cierre.CierreTurnoConciliacionDTO>> obtenerCierresTurnoRango(
            @org.springframework.web.bind.annotation.RequestParam("fechaInicio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
            @org.springframework.web.bind.annotation.RequestParam("fechaFin") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
        return ResponseEntity.ok(cierreTurnoService.generarReporteCierresRango(fechaInicio, fechaFin));
    }
}
