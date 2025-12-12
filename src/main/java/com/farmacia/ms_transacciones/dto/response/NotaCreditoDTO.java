package com.farmacia.ms_transacciones.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NotaCreditoDTO {
    private Long id;
    private String numeroNota;
    private Long devolucionId;
    private Long clienteId;
    private BigDecimal monto;
    private BigDecimal saldoDisponible;
    private LocalDateTime fechaEmision;
    private String estado;
}
