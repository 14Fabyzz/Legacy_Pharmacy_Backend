package com.legacy.pharmacy.reportes.dto;

import com.legacy.pharmacy.reportes.enums.Periodicidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta principal del reporte de ventas consolidadas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasConsolidadasDTO {

    /** Fecha inicio del rango consultado */
    private LocalDate fechaInicio;

    /** Fecha fin del rango consultado */
    private LocalDate fechaFin;

    /** Periodicidad de agrupación */
    private Periodicidad periodicidad;

    /** Sucursal filtrada (null si no se filtró) */
    private Integer sucursalId;

    // ========== Totales Consolidados ==========

    /** SUM(total) de todas las ventas COMPLETADAS en el rango */
    private BigDecimal totalIngresos;

    /** SUM(total_iva) de todas las ventas COMPLETADAS en el rango */
    private BigDecimal totalIva;

    /** totalIngresos - totalIva */
    private BigDecimal subtotalNeto;

    /** Siempre 0.00 (no existe columna descuento en la BD) */
    private BigDecimal totalDescuentos;

    /** COUNT total de ventas COMPLETADAS en el rango */
    private Long cantidadVentas;

    // ========== Desglose por Período ==========

    /** Lista de períodos agrupados según la periodicidad */
    private List<PeriodoVentaDTO> periodos;
}
