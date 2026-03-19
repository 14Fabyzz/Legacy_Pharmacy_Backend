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
    private java.math.BigDecimal ajusteRedondeo;

    private String vendedorNombre;
    private Integer sucursalId;
    private com.farmacia.ms_transacciones.enums.MetodoPago metodoPago;
    private String referenciaPago;

    private String estado;
    private Long clienteId;
    private String clienteNombre; // <-- NUEVO: Para enviar el nombre del cliente real al frontend
    private List<ItemVentaDTO> items;
    private List<String> resumenProductos;

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

    public com.farmacia.ms_transacciones.enums.MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(com.farmacia.ms_transacciones.enums.MetodoPago metodoPago) {
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

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public List<ItemVentaDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaDTO> items) {
        this.items = items;
    }

    public List<String> getResumenProductos() {
        return resumenProductos;
    }

    public void setResumenProductos(List<String> resumenProductos) {
        this.resumenProductos = resumenProductos;
    }

    public BigDecimal getAjusteRedondeo() {
        return ajusteRedondeo;
    }

    public void setAjusteRedondeo(BigDecimal ajusteRedondeo) {
        this.ajusteRedondeo = ajusteRedondeo;
    }
}