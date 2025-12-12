package com.farmacia.ms_transacciones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoNota {
    ACTIVA("activa"),
    USADA("usada"),
    VENCIDA("vencida"),
    CANCELADA("cancelada");

    private final String valor;

    EstadoNota(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static EstadoNota fromValor(String valor) {
        if (valor == null) return null;
        for (EstadoNota e : EstadoNota.values()) {
            if (e.valor.equalsIgnoreCase(valor) || e.name().equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de nota inválido: " + valor);
    }
}