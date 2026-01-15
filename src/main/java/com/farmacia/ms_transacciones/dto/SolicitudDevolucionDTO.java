package com.farmacia.ms_transacciones.dto;
import lombok.Data;
import java.util.List;

@Data
public class SolicitudDevolucionDTO {
    private Long ventaId;
    private String motivo;
    private List<ItemDevolucionDTO> items; // Qué productos devuelve
}
