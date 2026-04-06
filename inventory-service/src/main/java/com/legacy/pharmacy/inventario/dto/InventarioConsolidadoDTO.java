package com.legacy.pharmacy.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioConsolidadoDTO {
    private BigDecimal cogs;
    private Long unidadesRecibidas;
    private BigDecimal valorInventarioActual;
    private BigDecimal inventarioPromedio;
}
