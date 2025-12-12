package com.farmacia.ms_transacciones.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CierreTurnoDTO {
    private BigDecimal efectivoReal; // Lo que contó el cajero billete por billete
    private String observaciones;
}
