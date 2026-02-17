package com.legacy.pharmacy.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoKardexDTO {
    private Long id;
    private LocalDateTime fecha;
    private String tipo;
    private Integer cantidad;

    @JsonProperty("saldo_resultante")
    private Integer saldoResultante;

    @JsonProperty("documento_ref")
    private String documentoRef;

    @JsonProperty("nombre_producto")
    private String nombreProducto;

    @JsonProperty("codigo_barras")
    private String codigoBarras;

    private String usuario;
    private String detalle;
    private String lote;

    @JsonProperty("costo_unitario")
    private BigDecimal costoUnitario;
}
