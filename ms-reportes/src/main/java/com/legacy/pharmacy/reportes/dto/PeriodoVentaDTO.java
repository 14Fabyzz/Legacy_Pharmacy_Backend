package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para cada período agrupado (día, semana o mes) dentro del reporte.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoVentaDTO {

    /** Etiqueta del período (ej: "2026-01-15", "2026-W03", "2026-01") */
    private String periodo;

    /** SUM(total) de ventas COMPLETADAS en el período */
    private BigDecimal totalIngresos;

    /** SUM(total_iva) de ventas COMPLETADAS en el período */
    private BigDecimal totalIva;

    /** totalIngresos - totalIva */
    private BigDecimal subtotalNeto;

    /** Siempre 0.00 (no existe columna descuento en la BD) */
    private BigDecimal totalDescuentos;

    /** COUNT de ventas COMPLETADAS en el período */
    private Long cantidadVentas;
}
