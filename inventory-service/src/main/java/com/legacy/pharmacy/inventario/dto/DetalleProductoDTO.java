package com.legacy.pharmacy.inventario.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DetalleProductoDTO {
    private Integer id; // <-- Added ID for POS
    private String nombreComercial;
    private String codigoInterno;
    private BigDecimal precioCompraReferencia; // Costo
    private BigDecimal porcentajeGanancia;
    private BigDecimal ivaPorcentaje;
    private BigDecimal precioVentaBase; // Subtotal
    private BigDecimal precioVentaTotal; // PVP Final
    private BigDecimal precioVentaUnidad; // Si aplica
    private BigDecimal precioVentaBlister; // Si aplica
    private Boolean esFraccionable; // <-- Added for POS logic
    private Integer stockTotal; // Suma de los lotes
    private String imagenUrl;
}
