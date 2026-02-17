package com.farmacia.ms_transacciones.dto;

import java.util.List;

public class SolicitudDevolucionDTO {

    private Long ventaId;
    private String motivo;
    private List<ItemDevolucionDTO> items;

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<ItemDevolucionDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDevolucionDTO> items) {
        this.items = items;
    }
}