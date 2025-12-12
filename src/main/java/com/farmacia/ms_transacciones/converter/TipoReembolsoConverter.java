package com.farmacia.ms_transacciones.converter;

import com.farmacia.ms_transacciones.enums.TipoReembolso;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoReembolsoConverter implements AttributeConverter<TipoReembolso, String> {

    @Override
    public String convertToDatabaseColumn(TipoReembolso attribute) {
        return (attribute == null) ? null : attribute.getValor();
    }

    @Override
    public TipoReembolso convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : TipoReembolso.fromValor(dbData);
    }
}