package com.farmacia.ms_transacciones.dto;

import java.util.List;

public class DevolucionRequestDTO {

    private String motivo;
    private List<ItemDevolucionDTO> items; // Si es null o vacío, se asume devolución total.

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
