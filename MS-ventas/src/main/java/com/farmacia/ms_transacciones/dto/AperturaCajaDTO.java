package com.farmacia.ms_transacciones.dto;

import java.math.BigDecimal;

public class AperturaCajaDTO {
    private BigDecimal saldoInicial;
    private Integer sucursalId;

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }
}
