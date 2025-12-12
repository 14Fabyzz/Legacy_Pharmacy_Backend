package com.farmacia.ms_transacciones.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDevolucionResponseDTO {
    private Long id;
    private Long detalleVentaId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String motivoDetalle;
    private String estado;
    private String destinoProducto;


}