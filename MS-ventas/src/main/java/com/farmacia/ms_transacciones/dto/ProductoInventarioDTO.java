package com.farmacia.ms_transacciones.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ProductoInventarioDTO {
    @JsonProperty("productoId")
    private Integer id;

    @JsonProperty("nombreProducto")
    private String nombreComercial;

    @JsonProperty("precioVenta")
    private BigDecimal precioVentaBase;

    @JsonProperty("cantidadDisponible")
    private Integer stockActual;
}