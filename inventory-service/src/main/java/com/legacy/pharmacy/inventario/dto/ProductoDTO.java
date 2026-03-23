package com.legacy.pharmacy.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// Removed unused import
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

    // === CAMPOS DE ENTRADA (Editables por usuario) ===

    @com.fasterxml.jackson.annotation.JsonProperty("precioCompraReferencia")
    private BigDecimal precioCompraReferencia;

    @com.fasterxml.jackson.annotation.JsonProperty("porcentajeGanancia")
    private BigDecimal porcentajeGanancia;

    @com.fasterxml.jackson.annotation.JsonProperty("ivaPorcentaje")
    private BigDecimal ivaPorcentaje;

    // === CAMPOS CALCULADOS (Solo lectura) ===

    @com.fasterxml.jackson.annotation.JsonProperty(value = "precioVentaBase", access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private BigDecimal precioVentaBase;

    @com.fasterxml.jackson.annotation.JsonProperty(value = "precioVentaTotal", access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private BigDecimal precioVentaTotal;

    private Integer stockMinimo;
    private Boolean esControlado;
    private Boolean refrigerado;

    @com.fasterxml.jackson.annotation.JsonProperty("tipo")
    private String tipo; // TANGIBLE o SERVICIO

    // Campos para unidades fraccionadas
    @com.fasterxml.jackson.annotation.JsonProperty("esFraccionable")
    private Boolean esFraccionable;

    @com.fasterxml.jackson.annotation.JsonProperty("unidadesPorCaja")
    @jakarta.validation.constraints.Min(value = 1, message = "Las unidades por caja deben ser al menos 1")
    private Integer unidadesPorCaja;

    @com.fasterxml.jackson.annotation.JsonProperty("unidadesPorBlister")
    private Integer unidadesPorBlister;

    @com.fasterxml.jackson.annotation.JsonProperty(value = "precioVentaUnidad", access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private BigDecimal precioVentaUnidad;

    @com.fasterxml.jackson.annotation.JsonProperty(value = "precioVentaBlister", access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private BigDecimal precioVentaBlister;

    // === IMÁGENES ===
    private String imagenUrl;
    private String imagenId;
}