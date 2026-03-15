package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EficienciaOperativaMetricasDTO {
    private BigDecimal ventasPorMetroCuadrado;
    private BigDecimal porcentajeMerma;
    private BigDecimal puntoEquilibrio;
}
