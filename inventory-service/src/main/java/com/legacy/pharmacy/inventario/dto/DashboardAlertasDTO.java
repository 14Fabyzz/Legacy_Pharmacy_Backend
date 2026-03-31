package com.legacy.pharmacy.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * DTO del Dashboard de Alertas — Estándar de Semaforización Farmacéutica
 * (Colombia).
 *
 * ROJO : Lotes vencidos o que vencen en <= 90 días desde hoy.
 * AMARILLO: Lotes que vencen entre 91 y 180 días desde hoy.
 * VERDE : Lotes que vencen en > 180 días (solo se expone el conteo, la lista
 * viene vacía por optimización).
 */
@Data
@Builder
public class DashboardAlertasDTO {

    // --- TOTALES DE SEMAFORIZACIÓN (para las tarjetas del Dashboard) ---
    private long totalRojo;
    private long totalAmarillo;
    private long totalVerde;

    // --- TOTAL DE STOCK BAJO (métrica de inventario, se conserva) ---
    private long totalStockBajo;

    // --- LISTAS DETALLADAS (para las tablas del Dashboard) ---

    @JsonProperty("rojo")
    private List<Map<String, Object>> listaRoja;

    @JsonProperty("amarillo")
    private List<Map<String, Object>> listaAmarilla;

    /**
     * Lista verde: siempre se retorna como lista vacía ([]) por optimización de
     * memoria.
     * El Frontend usa únicamente el contador `totalVerde` para la tarjeta resumen.
     * Retornar lista vacía (no null) previene errores de NgFor en Angular.
     */
    @JsonProperty("verde")
    private List<Map<String, Object>> listaVerde;

    @JsonProperty("stockBajo")
    private List<Map<String, Object>> listaStockBajo;
}