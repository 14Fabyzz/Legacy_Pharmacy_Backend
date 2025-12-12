package com.farmacia.ms_transacciones.dto.response;

import com.farmacia.ms_transacciones.enums.MotivoDevolucion;
import com.farmacia.ms_transacciones.enums.TipoReembolso;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DevolucionResponseDTO {
    private Long id;
    private String numeroDevolucion;
    private String numeroFactura;
    private LocalDateTime fechaDevolucion;
    private MotivoDevolucion motivo;
    private String descripcionMotivo;
    private BigDecimal totalDevolucion;
    private TipoReembolso tipoReembolso;
    private String estado;
    private List<DetalleDevolucionResponseDTO> detalles;
}
