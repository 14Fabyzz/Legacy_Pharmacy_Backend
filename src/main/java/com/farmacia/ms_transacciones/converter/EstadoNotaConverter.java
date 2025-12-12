package com.farmacia.ms_transacciones.converter;

import com.farmacia.ms_transacciones.enums.EstadoNota;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoNotaConverter implements AttributeConverter<EstadoNota, String> {

    @Override
    public String convertToDatabaseColumn(EstadoNota attribute) {
        return (attribute == null) ? null : attribute.getValor();
    }

    @Override
    public EstadoNota convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : EstadoNota.fromValor(dbData);
    }
}