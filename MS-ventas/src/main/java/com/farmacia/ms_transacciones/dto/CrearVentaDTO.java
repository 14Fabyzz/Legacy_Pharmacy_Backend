package com.farmacia.ms_transacciones.dto;

import java.util.List;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import com.farmacia.ms_transacciones.enums.MetodoPago;

public class CrearVentaDTO {
    private Long clienteId;
    private List<ItemVentaDTO> items;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago; // "EFECTIVO", "TARJETA" o "TRANSFERENCIA"
    private String referenciaPago; // Texto manual si es tarjeta/transferencia

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

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
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