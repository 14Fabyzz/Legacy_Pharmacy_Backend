package com.legacy.pharmacy.reportes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "parametros_operativos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametrosOperativos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sucursalId; // Debe ser único por sucursal lógicamente

    private BigDecimal metrosCuadrados;
    
    private BigDecimal costosFijosMensuales;
    
    private Long traficoPersonasDiarioPromedio;
}
