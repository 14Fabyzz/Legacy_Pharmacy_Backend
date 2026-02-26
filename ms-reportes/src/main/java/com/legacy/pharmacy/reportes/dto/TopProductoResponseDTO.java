package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la respuesta del reporte de Top Productos (Mayor Rotación).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductoResponseDTO {

    /** Nombre del producto */
    private String nombreProducto;

    /** Presentación del producto (UNIDAD, BLISTER, CAJA) basada en tipo_venta */
    private String presentacion;

    /** Sumatoria total de la cantidad vendida en el período */
    private Long totalVendido;

    /** Sumatoria total de ingresos generados en el período (suma de subtotales) */
    private BigDecimal ingresoGenerado;
}
