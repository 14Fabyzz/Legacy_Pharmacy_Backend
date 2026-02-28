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
public class MetodoPagoDTO {
    private String nombreMetodo;
    private Long cantidadVentas;
    private BigDecimal totalRecaudado;
    private BigDecimal porcentajeParticipacion;
}
