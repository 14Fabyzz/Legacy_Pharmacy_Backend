package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidadoPagosResponseDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer sucursalId;
    private BigDecimal granTotal;
    private List<MetodoPagoDTO> metodosPago;
    private List<DetalleReferenciaPagoDTO> detallesReferencias;
}
