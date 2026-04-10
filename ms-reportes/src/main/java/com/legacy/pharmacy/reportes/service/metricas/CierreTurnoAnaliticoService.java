package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.cierre.CierreTurnoConciliacionDTO;
import com.legacy.pharmacy.reportes.dto.cierre.CierreTurnoIntegralDTO;
import com.legacy.pharmacy.reportes.dto.cierre.MovimientoTurnoDTO;
import com.legacy.pharmacy.reportes.exception.ResourceNotFoundException;
import com.legacy.pharmacy.reportes.service.RecolectorDatosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la orquestación y validación contable para el reporte de Cierre de Turno.
 * Mantiene el Single Responsibility Principle aislado de las métricas comerciales (ReportesAnaliticosService).
 */
@Service
public class CierreTurnoAnaliticoService {

    private static final Logger log = LoggerFactory.getLogger(CierreTurnoAnaliticoService.class);

    private final RecolectorDatosService recolectorDatosService;

    public CierreTurnoAnaliticoService(RecolectorDatosService recolectorDatosService) {
        this.recolectorDatosService = recolectorDatosService;
    }

    /**
     * Ensambla el reporte integral de Cierre de Turno con su conciliación y desglose de movimientos.
     * @param turnoId Identificador del turno a consultar.
     * @return El DTO con la cabecera y el detalle, o null si el turno no existe.
     */
    public CierreTurnoIntegralDTO generarReporteIntegralCierre(Long turnoId) {
        log.info("Generando reporte integral para Cierre de Turno ID: {}", turnoId);

        // 1. Obtener la Conciliación Financiera (Cabecera Teórica vs Real)
        CierreTurnoConciliacionDTO conciliacion = recolectorDatosService.obtenerCabeceraTurno(turnoId);
        
        if (conciliacion == null) {
            log.warn("No se encontró el turno con ID: {}", turnoId);
            throw new ResourceNotFoundException("No se encontró el turno con ID: " + turnoId);
        }

        // 2. Obtener el Desglose de Movimientos (Ingresos, Egresos, Devoluciones)
        List<MovimientoTurnoDTO> movimientos = recolectorDatosService.obtenerMovimientosDelTurno(turnoId);

        // 3. (Opcional) Realizar cálculos extras o auditoría en memoria si se requiriese en el futuro,
        // por ejemplo, cruzar la suma de los movimientos netos vs el totalVentasTeorico.

        // 4. Retornar el Ensamblado
        CierreTurnoIntegralDTO reporte = new CierreTurnoIntegralDTO();
        reporte.setEncabezado(conciliacion);
        reporte.setMovimientos(movimientos);

        log.info("Reporte integral generado exitosamente para Turno ID: {}. Total Movimientos: {}", 
                 turnoId, movimientos.size());

        return reporte;
    }

    /**
     * Obtiene los cierres de turno consolidados (cabeceras) en un rango de fechas.
     */
    public List<CierreTurnoConciliacionDTO> generarReporteCierresRango(java.time.LocalDate inicio, java.time.LocalDate fin) {
        log.info("Generando reporte de cierres de turno para el rango: {} a {}", inicio, fin);
        return recolectorDatosService.obtenerCierresTurnoRango(inicio, fin);
    }
}
