package com.farmacia.ms_transacciones.dto.internal;

import java.math.BigDecimal;

public class VentasRawDTO {
    private BigDecimal totalIngresos;
    private BigDecimal costoMercanciaVendidaCogs;
    private Long numeroTransacciones;
    private Long unidadesVendidas;

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getCostoMercanciaVendidaCogs() {
        return costoMercanciaVendidaCogs;
    }

    public void setCostoMercanciaVendidaCogs(BigDecimal costoMercanciaVendidaCogs) {
        this.costoMercanciaVendidaCogs = costoMercanciaVendidaCogs;
    }

    public Long getNumeroTransacciones() {
        return numeroTransacciones;
    }

    public void setNumeroTransacciones(Long numeroTransacciones) {
        this.numeroTransacciones = numeroTransacciones;
    }

    public Long getUnidadesVendidas() {
        return unidadesVendidas;
    }

    public void setUnidadesVendidas(Long unidadesVendidas) {
        this.unidadesVendidas = unidadesVendidas;
    }
}
