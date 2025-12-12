package com.farmacia.ms_transacciones.dto;

import lombok.Data;

@Data
public class DetalleRequestDTO {
    private Integer productoId;
    private Integer cantidad;
}
