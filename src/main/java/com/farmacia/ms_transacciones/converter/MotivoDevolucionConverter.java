package com.farmacia.ms_transacciones.converter;

import com.farmacia.ms_transacciones.enums.MotivoDevolucion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // <--- ¡LA CLAVE! Se aplica solo
public class MotivoDevolucionConverter implements AttributeConverter<MotivoDevolucion, String> {

    @Override
    public String convertToDatabaseColumn(MotivoDevolucion attribute) {
        return (attribute == null) ? null : attribute.getValor();
    }

    @Override
    public MotivoDevolucion convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : MotivoDevolucion.fromValor(dbData);
    }
}