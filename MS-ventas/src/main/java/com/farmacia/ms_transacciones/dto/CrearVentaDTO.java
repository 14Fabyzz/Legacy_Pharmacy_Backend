package com.farmacia.ms_transacciones.dto;

import java.util.List;
import java.math.BigDecimal;

public class CrearVentaDTO {
    private Long clienteId;
    private List<ItemVentaDTO> items;

    private String metodoPago; // "EFECTIVO" o "TRANSFERENCIA"
    private String referenciaPago; // Texto manual si es transferencia

    // ¿Cuánto dinero entregó el cliente?
    private BigDecimal montoRecibido;

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

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }
}