package com.legacy.pharmacy.inventario.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRawDTO {
    private BigDecimal valorInventarioActual;
    private BigDecimal inventarioPromedio;
    private Long unidadesRecibidas;
    private BigDecimal valorInventarioTeorico;
    private BigDecimal valorInventarioFisico;
}
