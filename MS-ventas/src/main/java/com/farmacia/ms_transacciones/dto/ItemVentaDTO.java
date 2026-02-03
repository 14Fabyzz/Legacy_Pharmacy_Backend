package com.farmacia.ms_transacciones.dto;

import com.farmacia.ms_transacciones.enums.TipoVenta;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemVentaDTO {
    private Integer productoId;
    private Integer cantidad;

    // New field: TipoVenta enum (preferred)
    private TipoVenta tipoVenta;

    // Old field: Boolean (deprecated, for backward compatibility)
    @Deprecated
    private Boolean esVentaPorCaja; // true = Caja, false/null = Unidad

    private BigDecimal precioUnitario; // Opcional, para respuesta
    private BigDecimal subtotal; // Opcional, para respuesta
}
