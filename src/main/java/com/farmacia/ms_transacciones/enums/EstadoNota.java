package com.farmacia.ms_transacciones.enums;

public enum EstadoNota {
    ACTIVA("activa"),
    USADA("usada"),
    VENCIDA("vencida"),
    CANCELADA("cancelada");

    private final String valor;

    EstadoNota(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}