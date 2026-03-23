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
public class GestionInventarioMetricasDTO {
    private BigDecimal rotacionInventarioIri;
    private BigDecimal gmroi;
    private BigDecimal sellThroughRate;
    private BigDecimal weeksOfSupplyWos;
}
