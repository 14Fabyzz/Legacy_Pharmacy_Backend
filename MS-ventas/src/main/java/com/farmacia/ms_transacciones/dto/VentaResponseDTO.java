package com.farmacia.ms_transacciones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {
    private Long id;
    private String numeroFactura;
    private LocalDateTime fechaVenta;
    private BigDecimal total;

    // NUEVOS CAMPOS PARA EL VOUCHER - Datos para imprimir en el Voucher
    private java.math.BigDecimal montoRecibido;
    private java.math.BigDecimal cambio; // La devuelta
    private java.math.BigDecimal totalIva; // Impuesto calculado

    private String vendedorNombre;
    private Integer sucursalId;
    private String metodoPago;
    private String referenciaPago;

    private String estado;
    private Long clienteId;
    private List<ItemVentaDTO> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemVentaDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaDTO> items) {
        this.items = items;
    }
}