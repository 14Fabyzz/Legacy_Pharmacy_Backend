package com.farmacia.ms_transacciones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoReembolso {
    EFECTIVO("efectivo"),
    NOTA_CREDITO("nota_credito"),
    CAMBIO_PRODUCTO("cambio_producto");

    private final String valor;

    TipoReembolso(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static TipoReembolso fromValor(String valor) {
        for (TipoReembolso tipo : TipoReembolso.values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de reembolso no válido: " + valor);
    }
}