package com.legacy.pharmacy.inventario.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductoConLotesDTO {
    private DetalleProductoDTO detalleProducto;
    private List<LoteDTO> lotes;
}
