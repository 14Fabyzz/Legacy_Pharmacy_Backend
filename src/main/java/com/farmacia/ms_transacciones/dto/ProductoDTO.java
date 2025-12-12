package com.farmacia.ms_transacciones.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String codigo;
    private BigDecimal precio;
    private Integer stock;
    private String lote; // Importante para la HU-10 (FEFO)
    private String fechaVencimiento;
}