package com.farmacia.ms_transacciones.dto.response;

import com.farmacia.ms_transacciones.enums.EstadoNota;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NotaCreditoResponseDTO {
    private Long id;
    private String numeroNota;
    private String numeroDevolucion;
    private Long clienteId;
    private String clienteNombre;
    private BigDecimal monto;
    private BigDecimal saldo;
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    private EstadoNota estado;
}
