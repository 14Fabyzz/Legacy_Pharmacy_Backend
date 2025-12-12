package com.farmacia.ms_transacciones.dto;

import lombok.Data;

@Data
public class DetalleDevolucionDTO {
    private Long detalleVentaId;
    private Integer cantidad;
}