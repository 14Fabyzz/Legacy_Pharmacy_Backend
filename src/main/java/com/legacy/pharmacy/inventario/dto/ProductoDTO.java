package com.legacy.pharmacy.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {

    @NotBlank(message = "El código interno es obligatorio")
    private String codigoInterno;

    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreComercial;

    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;

    @NotNull(message = "El laboratorio es obligatorio")
    private Integer laboratorioId;

    private Integer principioActivoId;

    private String concentracion;
    private String presentacion;
    private String registroInvima;

    @NotNull
    @Positive
    private BigDecimal precioVentaBase;

    private Integer stockMinimo;
    private Boolean esControlado;
    private Boolean refrigerado;
}