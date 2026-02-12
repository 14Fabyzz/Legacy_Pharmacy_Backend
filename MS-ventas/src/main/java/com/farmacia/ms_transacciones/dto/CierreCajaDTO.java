package com.farmacia.ms_transacciones.dto;

import java.math.BigDecimal;

public class CierreCajaDTO {
    private BigDecimal totalEfectivoReal; // Cuánto dinero contó el cajero físicamente
    private String observaciones;

    public BigDecimal getTotalEfectivoReal() {
        return totalEfectivoReal;
    }

    public void setTotalEfectivoReal(BigDecimal totalEfectivoReal) {
        this.totalEfectivoReal = totalEfectivoReal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}