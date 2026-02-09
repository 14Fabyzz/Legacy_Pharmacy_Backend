package com.legacy.pharmacy.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Integer productoId;
    private String nombreProducto;
    private Integer cantidadDisponible;
    private Integer cantidadMinima;
    private String estado; // "STOCK_OK", "STOCK_BAJO", "SIN_STOCK"
    private Boolean disponibleParaVenta;

    // Campos para Kiosco de Precios
    private Boolean esFraccionable;
    private BigDecimal precioVentaUnidad;
    private Integer unidadesPorBlister;
    private BigDecimal precioVentaBlister;
    private Boolean esControlado;
}