package com.legacy.pharmacy.inventario.dto;

import lombok.Data;
import com.legacy.pharmacy.inventario.enums.TipoVenta;

@Data
public class MovimientoVentaDTO {
    private Integer productoId;
    private Integer cantidad;
    private String motivo; // Ventas enviará "VENTA" o "DEVOLUCION"
    private TipoVenta tipoVenta;
    private String destinoProducto; // BODEGA, MERMA, CUARENTENA
}