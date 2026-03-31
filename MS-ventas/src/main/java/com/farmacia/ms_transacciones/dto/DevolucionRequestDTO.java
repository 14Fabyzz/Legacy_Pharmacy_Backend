package com.farmacia.ms_transacciones.dto;

import java.util.List;

public class DevolucionRequestDTO {

    @jakarta.validation.constraints.NotBlank(message = "El motivo de la anulación es obligatorio")
    private String motivo;

    private String destino; // Destino general para la anulación total

    private List<ItemDevolucionDTO> items; // Si es null o vacío, se asume devolución total.

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public List<ItemDevolucionDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDevolucionDTO> items) {
        this.items = items;
    }
}
