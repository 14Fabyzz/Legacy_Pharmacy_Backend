package com.legacy.pharmacy.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoBitacoraDTO {
    private Long id;
    private LocalDateTime fecha;
    @com.fasterxml.jackson.annotation.JsonProperty("nombre_producto")
    private String nombreProducto;
    @com.fasterxml.jackson.annotation.JsonProperty("usuario_responsable")
    private String usuarioResponsable;
    private String tipo;
    private Integer cantidad;
    @com.fasterxml.jackson.annotation.JsonProperty("documento_ref")
    private String documentoRef;
}
