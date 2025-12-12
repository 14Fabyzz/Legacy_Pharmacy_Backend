package com.farmacia.ms_transacciones.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AperturaTurnoDTO {
    private String usuarioId; // Quién abre (o se saca del token)
    private Integer sucursalId;
    private BigDecimal saldoInicial; // Con cuánto dinero empieza
}