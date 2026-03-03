package com.farmacia.ms_transacciones.dto;

import com.farmacia.ms_transacciones.enums.TipoVenta;
import java.math.BigDecimal;

public class ItemVentaDTO {
    private Integer productoId;
    private Integer cantidad;

    // New field: TipoVenta enum (preferred)
    private TipoVenta tipoVenta;

    // Old field: Boolean (deprecated, for backward compatibility)
    @Deprecated
    private Boolean esVentaPorCaja; // true = Caja, false/null = Unidad

    private BigDecimal descuento; // Opcional, para descuentos autorizados
    private BigDecimal precioUnitario; // Opcional, para respuesta
    private BigDecimal subtotal; // Opcional, para respuesta
    private Integer cantidadDevuelta; // Opcional, para respuesta

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
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

    public Integer getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(Integer cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }
}
