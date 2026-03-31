package com.legacy.pharmacy.inventario.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convierte de forma segura el String de la BD al enum TipoMovimiento.
 * Evita errores 500 (EnumJavaType.wrap) cuando la BD contiene un valor
 * que no existe en el enum Java (e.g. valores legacy, mayúsculas/minúsculas, etc.)
 */
@Converter
public class TipoMovimientoConverter implements AttributeConverter<TipoMovimiento, String> {

    private static final Logger log = LoggerFactory.getLogger(TipoMovimientoConverter.class);

    @Override
    public String convertToDatabaseColumn(TipoMovimiento attribute) {
        if (attribute == null) return null;
        return attribute.name();
    }

    @Override
    public TipoMovimiento convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            // Intentar coincidencia exacta primero
            return TipoMovimiento.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Valor desconocido en BD: loguear y devolver null en vez de romper la app
            log.warn("Valor de TipoMovimiento desconocido en BD: '{}'. Se mapeará como null.", dbData);
            return null;
        }
    }
}
