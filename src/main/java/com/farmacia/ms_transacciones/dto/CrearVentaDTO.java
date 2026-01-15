package com.farmacia.ms_transacciones.dto;
import lombok.Data;
import java.util.List;
@Data
public class CrearVentaDTO {
    private Long clienteId;
    private List<ItemVentaDTO> items;
    private String metodoPago;
}