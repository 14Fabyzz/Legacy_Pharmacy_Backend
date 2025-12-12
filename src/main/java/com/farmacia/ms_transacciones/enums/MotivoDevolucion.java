package com.farmacia.ms_transacciones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MotivoDevolucion {
    PRODUCTO_VENCIDO("producto_vencido"),
    PRODUCTO_DANADO("producto_danado"), // Asegúrate que en BD sea 'producto_danado'
    ERROR_DESPACHO("error_despacho"),
    CLIENTE_INSATISFECHO("cliente_insatisfecho"),
    OTRO("otro");

    private final String valor;

    MotivoDevolucion(String valor) {
        this.valor = valor;
    }

    @JsonValue // Para que en el JSON de respuesta salga en minúsculas
    public String getValor() {
        return valor;
    }

    @JsonCreator // Para aceptar mayúsculas o minúsculas en el JSON de entrada
    public static MotivoDevolucion fromValor(String valor) {
        if (valor == null) return null;
        for (MotivoDevolucion m : MotivoDevolucion.values()) {
            if (m.valor.equalsIgnoreCase(valor) || m.name().equalsIgnoreCase(valor)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Motivo inválido: " + valor);
    }
}