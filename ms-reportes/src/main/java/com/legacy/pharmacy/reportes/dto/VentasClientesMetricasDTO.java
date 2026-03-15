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
public class VentasClientesMetricasDTO {
    private BigDecimal ticketPromedio;
    private BigDecimal unitsPerTransactionUpt;
    private BigDecimal tasaConversion;
    private BigDecimal margenUtilidadBruta;
}
