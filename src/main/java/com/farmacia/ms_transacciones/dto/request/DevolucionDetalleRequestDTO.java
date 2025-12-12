package com.farmacia.ms_transacciones.dto.request;

import lombok.Data;

@Data
public class DevolucionDetalleRequestDTO { // <--- Agregado PUBLIC
    private Integer productoId;            // <--- Cambiado de Long a INTEGER (importante)
    private Integer cantidad;
    private String motivoDetalle;
}
