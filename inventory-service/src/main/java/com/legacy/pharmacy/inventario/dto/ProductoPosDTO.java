package com.legacy.pharmacy.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoPosDTO {
    private Integer id;
    private String nombreComercial;
    private BigDecimal precioVenta; // maps to precioVentaTotal
    private Integer stockActual;
    private String codigoBarras;
    private String laboratorio; // Nombre del laboratorio
    private String presentacion;
}
