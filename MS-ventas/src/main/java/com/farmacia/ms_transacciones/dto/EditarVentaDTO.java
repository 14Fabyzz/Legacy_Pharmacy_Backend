package com.farmacia.ms_transacciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class EditarVentaDTO {

    @NotBlank(message = "El motivo de la edición es obligatorio")
    private String motivo;

    @NotEmpty(message = "La lista de productos finales no puede estar vacía")
    private List<ItemVentaDTO> itemsFinales;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<ItemVentaDTO> getItemsFinales() {
        return itemsFinales;
    }

    public void setItemsFinales(List<ItemVentaDTO> itemsFinales) {
        this.itemsFinales = itemsFinales;
    }
}
