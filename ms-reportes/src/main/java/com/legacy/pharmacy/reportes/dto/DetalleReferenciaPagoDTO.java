package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReferenciaPagoDTO {
    private Long idVenta;
    private String referenciaPago;
    private BigDecimal monto;
    private LocalDateTime fechaVenta;
    private String metodoPago;
}
