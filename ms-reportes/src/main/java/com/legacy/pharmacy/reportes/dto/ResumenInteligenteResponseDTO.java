package com.legacy.pharmacy.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenInteligenteResponseDTO {
    private String resumenGenerado;
    private ReporteVentasConsolidadasDTO reporteBase;
    private List<TopProductoResponseDTO> topProductos;
}
