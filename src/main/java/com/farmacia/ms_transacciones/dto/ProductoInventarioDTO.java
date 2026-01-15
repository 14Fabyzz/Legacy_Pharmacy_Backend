package com.farmacia.ms_transacciones.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ProductoInventarioDTO {
    private Integer id;
    private String nombreComercial;
    private BigDecimal precioVentaBase;
    private Integer stockActual;
}