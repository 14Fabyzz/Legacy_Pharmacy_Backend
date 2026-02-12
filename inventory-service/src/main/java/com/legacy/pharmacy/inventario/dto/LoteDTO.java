package com.legacy.pharmacy.inventario.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoteDTO {
    private Integer id;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private Integer cantidadActual;
    private BigDecimal costoCompra;
}
