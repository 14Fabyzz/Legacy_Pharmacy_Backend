package com.farmacia.ms_transacciones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MotivoDevolucion {
    PRODUCTO_VENCIDO("producto_vencido"),
    PRODUCTO_DANADO("producto_danado"),
    ERROR_DESPACHO("error_despacho"),
    CLIENTE_INSATISFECHO("cliente_insatisfecho"),
    OTRO("otro");

    private final String valor;

    MotivoDevolucion(String valor) {
        this.valor = valor;
    }

    @JsonValue // Para que al enviar datos (serializar), se use el valor en minúsculas.
    public String getValor() {
        return valor;
    }

    @JsonCreator // Para que al recibir datos (deserializar), sepa cómo crear el enum desde el String.
    public static MotivoDevolucion fromValor(String valor) {
        for (MotivoDevolucion motivo : MotivoDevolucion.values()) {
            if (motivo.valor.equalsIgnoreCase(valor)) {
                return motivo;
            }
        }
        // Si no encuentra el valor, lanza una excepción clara.
        throw new IllegalArgumentException("Valor no válido para MotivoDevolucion: " + valor);
    }
}