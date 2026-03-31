package com.farmacia.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class ProductoInventarioDTO {
    @JsonProperty("productoId")
    private Integer id;

    @JsonProperty("nombreProducto")
    private String nombreComercial;

    @JsonProperty("tipo")
    private String tipo; // TANGIBLE o SERVICIO

    @JsonProperty("precioVentaBase")
    private BigDecimal precioVentaBase; // Precio por Caja

    @JsonProperty("precioVentaTotal")
    private BigDecimal precioVentaTotal; // Precio Final con IVA

    @JsonProperty("ivaPorcentaje")
    private BigDecimal ivaPorcentaje;

    @JsonProperty("precioVentaUnidad")
    private BigDecimal precioVentaUnidad; // Precio por Unidad

    @JsonProperty("precioVentaBlister")
    private BigDecimal precioVentaBlister; // Precio por Blister (empaque intermedio)

    @JsonProperty("esFraccionable")
    private Boolean esFraccionable; // ¿Permite venta por unidad?

    @JsonProperty("unidadesPorCaja")
    private Integer unidadesPorCaja; // Factor de conversión

    @JsonProperty("unidadesPorBlister")
    private Integer unidadesPorBlister; // Informativo para UX (botones rápidos)

    @JsonProperty("esControlado")
    private Boolean esControlado; // Medicamento controlado (requiere cliente real)

    @JsonProperty("cantidadDisponible")
    private Integer stockActual;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioVentaBase() {
        return precioVentaBase;
    }

    public void setPrecioVentaBase(BigDecimal precioVentaBase) {
        this.precioVentaBase = precioVentaBase;
    }

    public BigDecimal getPrecioVentaTotal() {
        return precioVentaTotal;
    }

    public void setPrecioVentaTotal(BigDecimal precioVentaTotal) {
        this.precioVentaTotal = precioVentaTotal;
    }

    public BigDecimal getIvaPorcentaje() {
        return ivaPorcentaje;
    }

    public void setIvaPorcentaje(BigDecimal ivaPorcentaje) {
        this.ivaPorcentaje = ivaPorcentaje;
    }

    public BigDecimal getPrecioVentaUnidad() {
        return precioVentaUnidad;
    }

    public void setPrecioVentaUnidad(BigDecimal precioVentaUnidad) {
        this.precioVentaUnidad = precioVentaUnidad;
    }

    public BigDecimal getPrecioVentaBlister() {
        return precioVentaBlister;
    }

    public void setPrecioVentaBlister(BigDecimal precioVentaBlister) {
        this.precioVentaBlister = precioVentaBlister;
    }

    public Boolean getEsFraccionable() {
        return esFraccionable;
    }

    public void setEsFraccionable(Boolean esFraccionable) {
        this.esFraccionable = esFraccionable;
    }

    public Integer getUnidadesPorCaja() {
        return unidadesPorCaja;
    }

    public void setUnidadesPorCaja(Integer unidadesPorCaja) {
        this.unidadesPorCaja = unidadesPorCaja;
    }

    public Integer getUnidadesPorBlister() {
        return unidadesPorBlister;
    }

    public void setUnidadesPorBlister(Integer unidadesPorBlister) {
        this.unidadesPorBlister = unidadesPorBlister;
    }

    public Boolean getEsControlado() {
        return esControlado;
    }

    public void setEsControlado(Boolean esControlado) {
        this.esControlado = esControlado;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }
}