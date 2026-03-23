package com.farmacia.ms_transacciones.dto;

public class ItemDevolucionDTO {

    private Integer productoId;
    private Integer cantidad;
    private String motivoDetalle;
    private String destinoProducto;
    private Long loteId;

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

    public String getMotivoDetalle() {
        return motivoDetalle;
    }

    public void setMotivoDetalle(String motivoDetalle) {
        this.motivoDetalle = motivoDetalle;
    }

    public String getDestinoProducto() {
        return destinoProducto;
    }

    public void setDestinoProducto(String destinoProducto) {
        this.destinoProducto = destinoProducto;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }
}
