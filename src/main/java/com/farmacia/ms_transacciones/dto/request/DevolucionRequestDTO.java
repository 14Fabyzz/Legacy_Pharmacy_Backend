package com.farmacia.ms_transacciones.dto.request;

import com.farmacia.ms_transacciones.enums.MotivoDevolucion;
import com.farmacia.ms_transacciones.enums.TipoReembolso;
import lombok.Data;
import java.util.List;

@Data
public class DevolucionRequestDTO {
    private Long ventaId;
    private MotivoDevolucion motivo;
    private String descripcionMotivo;
    private TipoReembolso tipoReembolso;

    // Ahora usará la clase pública del otro archivo
    private List<DevolucionDetalleRequestDTO> detalles;
}
