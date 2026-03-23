package com.farmacia.ms_transacciones.dto;

import com.farmacia.ms_transacciones.enums.TipoVenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BatchItemDevolucionDTO {
    private Integer productoId;
    private Integer cantidad;
    private TipoVenta tipoVenta;
    private String destinoProducto; // STOCK, MERMA, CUARENTENA
    private String motivo;
    private Long loteId;

    public BatchItemDevolucionDTO() {
    }

    public BatchItemDevolucionDTO(Integer productoId, Integer cantidad, TipoVenta tipoVenta, String destinoProducto,
            String motivo, Long loteId) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.tipoVenta = tipoVenta;
        this.destinoProducto = destinoProducto;
        this.motivo = motivo;
        this.loteId = loteId;
    }

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

    public String getDestinoProducto() {
        return destinoProducto;
    }

    public void setDestinoProducto(String destinoProducto) {
        this.destinoProducto = destinoProducto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }
}
