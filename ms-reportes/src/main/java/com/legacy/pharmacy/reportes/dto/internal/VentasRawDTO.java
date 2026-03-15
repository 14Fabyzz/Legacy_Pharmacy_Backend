package com.legacy.pharmacy.reportes.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentasRawDTO {
    private BigDecimal totalIngresos;
    private BigDecimal costoMercanciaVendidaCogs;
    private Integer numeroTransacciones;
    private Long unidadesVendidas;
}
