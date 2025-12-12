package com.farmacia.ms_transacciones.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequestDTO {
    // Datos de cabecera
    private Integer sucursalId;
    private Long clienteId;     // Puede venir nulo si es cliente ocasional
    private String formaPago;   // 'efectivo', 'tarjeta', etc.

    // Lista de productos
    private List<DetalleRequestDTO> productos;
}