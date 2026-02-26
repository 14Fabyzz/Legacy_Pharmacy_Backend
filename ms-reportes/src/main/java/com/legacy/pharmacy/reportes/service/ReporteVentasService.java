package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.PeriodoVentaDTO;
import com.legacy.pharmacy.reportes.dto.ReporteVentasConsolidadasDTO;
import com.legacy.pharmacy.reportes.enums.Periodicidad;
import com.legacy.pharmacy.reportes.exception.BusinessException;
import com.legacy.pharmacy.reportes.exception.ResourceNotFoundException;
import com.legacy.pharmacy.reportes.repository.VentaReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para generación de reportes de ventas consolidadas.
 */
@Service
@Transactional(readOnly = true)
public class ReporteVentasService {

    private static final Logger log = LoggerFactory.getLogger(ReporteVentasService.class);

    private final VentaReporteRepository ventaReporteRepository;

    public ReporteVentasService(VentaReporteRepository ventaReporteRepository) {
        this.ventaReporteRepository = ventaReporteRepository;
    }

    /**
     * Genera un reporte consolidado de ventas para el rango y periodicidad dados.
     *
     * @param fechaInicio  Fecha inicial del rango
     * @param fechaFin     Fecha final del rango (inclusiva)
     * @param periodicidad Agrupación: DIARIO, SEMANAL o MENSUAL
     * @param sucursalId   Filtro opcional por sucursal (null = todas)
     * @return DTO con totales consolidados y desglose por período
     */
    public ReporteVentasConsolidadasDTO generarReporteConsolidado(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Periodicidad periodicidad,
            Integer sucursalId) {

        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Convertir a LocalDateTime para las queries
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX); // 23:59:59.999999999

        log.debug("Generando reporte consolidado: {} a {}, periodicidad={}, sucursal={}",
                fechaInicio, fechaFin, periodicidad, sucursalId);

        // 1. Obtener totales consolidados
        List<Object[]> totalesRaw = ventaReporteRepository.obtenerTotalesConsolidados(
                inicio, fin, sucursalId);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        Long cantidadVentas = 0L;

        if (totalesRaw != null && !totalesRaw.isEmpty()) {
            Object[] row = totalesRaw.get(0);
            totalIngresos = toBigDecimal(row[0]);
            totalIva = toBigDecimal(row[1]);
            cantidadVentas = toLong(row[2]);
        }

        // Verificar si hay ventas en el período
        if (cantidadVentas == 0) {
            throw new ResourceNotFoundException(
                    "No se encontraron ventas en el período consultado");
        }

        // 2. Calcular subtotal neto y descuentos
        BigDecimal subtotalNeto = totalIngresos.subtract(totalIva)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDescuentos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // 3. Obtener desglose por período
        List<Object[]> periodosRaw = obtenerPeriodosAgrupados(
                periodicidad, inicio, fin, sucursalId);

        List<PeriodoVentaDTO> periodos = new ArrayList<>();
        for (Object[] row : periodosRaw) {
            BigDecimal periodoIngresos = toBigDecimal(row[1]);
            BigDecimal periodoIva = toBigDecimal(row[2]);
            BigDecimal periodoSubtotal = periodoIngresos.subtract(periodoIva)
                    .setScale(2, RoundingMode.HALF_UP);

            PeriodoVentaDTO dto = PeriodoVentaDTO.builder()
                    .periodo((String) row[0])
                    .totalIngresos(periodoIngresos.setScale(2, RoundingMode.HALF_UP))
                    .totalIva(periodoIva.setScale(2, RoundingMode.HALF_UP))
                    .subtotalNeto(periodoSubtotal)
                    .totalDescuentos(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .cantidadVentas(toLong(row[3]))
                    .build();

            periodos.add(dto);
        }

        // 4. Construir respuesta
        return ReporteVentasConsolidadasDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .periodicidad(periodicidad)
                .sucursalId(sucursalId)
                .totalIngresos(totalIngresos.setScale(2, RoundingMode.HALF_UP))
                .totalIva(totalIva.setScale(2, RoundingMode.HALF_UP))
                .subtotalNeto(subtotalNeto)
                .totalDescuentos(totalDescuentos)
                .cantidadVentas(cantidadVentas)
                .periodos(periodos)
                .build();
    }

    /**
     * Obtiene los datos agrupados según la periodicidad.
     */
    private List<Object[]> obtenerPeriodosAgrupados(
            Periodicidad periodicidad,
            LocalDateTime inicio,
            LocalDateTime fin,
            Integer sucursalId) {

        return switch (periodicidad) {
            case DIARIO -> ventaReporteRepository.obtenerAgrupadoPorDia(inicio, fin, sucursalId);
            case SEMANAL -> ventaReporteRepository.obtenerAgrupadoPorSemana(inicio, fin, sucursalId);
            case MENSUAL -> ventaReporteRepository.obtenerAgrupadoPorMes(inicio, fin, sucursalId);
        };
    }

    // ==========================================
    // Utilidades de conversión segura
    // ==========================================

    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd)
            return bd;
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null)
            return 0L;
        if (value instanceof Long l)
            return l;
        return Long.parseLong(value.toString());
    }
}
