package com.farmacia.ms_transacciones.model;

import com.farmacia.ms_transacciones.enums.TipoVenta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas", indexes = {
        @Index(name = "idx_detalleventa_venta", columnList = "venta_id"),
        @Index(name = "idx_detalleventa_producto", columnList = "productoId")
})
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer productoId;

    private String productoNombre; // Opcional, pero útil para historial
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta")
    private TipoVenta tipoVenta;

    // Old field: Boolean (deprecated, for backward compatibility)
    @Deprecated
    @Column(name = "es_venta_por_caja")
    private Boolean esVentaPorCaja; // true = Caja, false = Unidad

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    @JsonIgnore // Rompe el bucle infinito JSON
    private Venta venta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public Boolean getEsVentaPorCaja() {
        return esVentaPorCaja;
    }

    public void setEsVentaPorCaja(Boolean esVentaPorCaja) {
        this.esVentaPorCaja = esVentaPorCaja;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
}