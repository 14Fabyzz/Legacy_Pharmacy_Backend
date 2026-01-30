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
    private BigDecimal precioVentaBase; // Precio por Caja
    private BigDecimal precioVentaUnidad; // Precio por Unidad (Calculado)
    private Boolean esFraccionable; // ¿Permite venta menudeada?
    private Integer unidadesPorCaja; // Factor de conversión
    private Integer unidadesPorBlister; // Informativo para UX (botones rápidos)
    private Integer cantidadDisponible; // Stock TOTAL en unidades
    private Integer cantidadMinima;
    private String estado; // STOCK_OK, BAJO, SIN_STOCK
    private Boolean disponibleParaVenta;
}