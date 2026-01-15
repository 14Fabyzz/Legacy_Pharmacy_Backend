package com.farmacia.ms_transacciones.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class VentaResponseDTO {
    private Long id;
    private String numeroFactura;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
    private Long clienteId;
    private List<ItemVentaDTO> items;
}