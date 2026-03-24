package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.repository.VentaReporteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.legacy.pharmacy.reportes.client.InventarioClient;

@Service
public class ReportesAnaliticosService {

    private final VentaReporteRepository ventaReporteRepository;
    private final InventarioClient inventarioClient;

    public ReportesAnaliticosService(VentaReporteRepository ventaReporteRepository, InventarioClient inventarioClient) {
        this.ventaReporteRepository = ventaReporteRepository;
        this.inventarioClient = inventarioClient;
    }

    public List<Map<String, Object>> getVentasPorCliente(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        return ventaReporteRepository.getVentasPorCliente(start, end, sucursalId);
    }

    public List<Map<String, Object>> getVentasClienteProducto(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        return ventaReporteRepository.getVentasClienteProducto(start, end, sucursalId);
    }

    public List<Map<String, Object>> getConsolidadoVentas(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        return ventaReporteRepository.getConsolidadoVentas(start, end, sucursalId);
    }

    public List<Map<String, Object>> getComparativoMensual(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        return ventaReporteRepository.getComparativoMensual(start, end, sucursalId);
    }

    // ==========================================
    // MÉTODOS RENDIMIENTO INVENTARIO
    // ==========================================

    public List<Map<String, Object>> getTop10Productos(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId, Integer categoriaId, Integer laboratorioId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        
        boolean filtrarIds = (categoriaId != null || laboratorioId != null);
        List<Integer> productIds;
        
        if (filtrarIds) {
            productIds = inventarioClient.obtenerIdsPorFiltros(categoriaId, laboratorioId);
            if (productIds == null || productIds.isEmpty()) {
                // Return empty list if filter yields no products
                return java.util.Collections.emptyList();
            }
        } else {
            // Dummy list to prevent JPA crash on IN() when filtrarIds=false
            productIds = java.util.Collections.singletonList(-1);
        }
        
        return ventaReporteRepository.getTop10Productos(start, end, sucursalId, filtrarIds, productIds);
    }

    public List<Map<String, Object>> getProductosBajaRotacion(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        return ventaReporteRepository.getProductosBajaRotacion(start, end, sucursalId);
    }

    public List<Map<String, Object>> getComparativoProducto(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId) {
        // Periodo A
        LocalDateTime startA = fechaInicio.atStartOfDay();
        LocalDateTime endA = fechaFin.atTime(LocalTime.MAX);
        List<Map<String, Object>> ventasA = ventaReporteRepository.getComparativoProducto(startA, endA, sucursalId);

        // Periodo B (Previo equivalente)
        long diasRango = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        LocalDate fechaInicioB = fechaInicio.minusDays(diasRango);
        LocalDate fechaFinB = fechaInicio.minusDays(1);
        LocalDateTime startB = fechaInicioB.atStartOfDay();
        LocalDateTime endB = fechaFinB.atTime(LocalTime.MAX);
        List<Map<String, Object>> ventasB = ventaReporteRepository.getComparativoProducto(startB, endB, sucursalId);

        // Map para fusionar por "producto"
        java.util.Map<String, java.util.Map<String, Object>> fusion = new java.util.HashMap<>();

        // Poblar Periodo A
        for (Map<String, Object> filaA : ventasA) {
            String producto = (String) filaA.get("producto");
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("producto", producto);
            map.put("unidades_a", filaA.get("unidades") != null ? ((Number) filaA.get("unidades")).longValue() : 0L);
            map.put("ventas_a", filaA.get("ventas") != null ? new java.math.BigDecimal(filaA.get("ventas").toString()) : java.math.BigDecimal.ZERO);
            map.put("unidades_b", 0L);
            map.put("ventas_b", java.math.BigDecimal.ZERO);
            fusion.put(producto, map);
        }

        // Poblar Periodo B
        for (Map<String, Object> filaB : ventasB) {
            String producto = (String) filaB.get("producto");
            java.util.Map<String, Object> map = fusion.getOrDefault(producto, new java.util.HashMap<>());
            if (!map.containsKey("producto")) {
                map.put("producto", producto);
                map.put("unidades_a", 0L);
                map.put("ventas_a", java.math.BigDecimal.ZERO);
            }
            map.put("unidades_b", filaB.get("unidades") != null ? ((Number) filaB.get("unidades")).longValue() : 0L);
            map.put("ventas_b", filaB.get("ventas") != null ? new java.math.BigDecimal(filaB.get("ventas").toString()) : java.math.BigDecimal.ZERO);
            fusion.putIfAbsent(producto, map);
        }

        // Calcular Variaciones y Tendencias
        List<Map<String, Object>> resultado = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> item : fusion.values()) {
            long unidadesA = (long) item.get("unidades_a");
            long unidadesB = (long) item.get("unidades_b");
            java.math.BigDecimal ventasAVal = (java.math.BigDecimal) item.get("ventas_a");
            java.math.BigDecimal ventasBVal = (java.math.BigDecimal) item.get("ventas_b");

            long variacionUnidades = unidadesA - unidadesB;
            java.math.BigDecimal variacionVentas = ventasAVal.subtract(ventasBVal);

            double porcentaje = 0.0;
            if (ventasBVal.compareTo(java.math.BigDecimal.ZERO) > 0) {
                porcentaje = variacionVentas.doubleValue() / ventasBVal.doubleValue() * 100.0;
            } else if (ventasAVal.compareTo(java.math.BigDecimal.ZERO) > 0) {
                porcentaje = 100.0; // Incremento máximo si antes no se vendió
            }

            String tendencia;
            if (porcentaje > 0) {
                tendencia = String.format("📈 +%.1f%%", Math.abs(porcentaje)).replace(",", ".");
            } else if (porcentaje < 0) {
                tendencia = String.format("📉 -%.1f%%", Math.abs(porcentaje)).replace(",", ".");
            } else {
                tendencia = "➖ 0%";
            }

            item.put("variacion_unidades", variacionUnidades);
            item.put("variacion_ventas", variacionVentas);
            item.put("crecimiento_porcentaje", porcentaje);
            item.put("tendencia", tendencia);
            
            resultado.add(item);
        }

        // Ordenar por ventas_a descendente
        resultado.sort((a, b) -> {
            java.math.BigDecimal va = (java.math.BigDecimal) a.get("ventas_a");
            java.math.BigDecimal vb = (java.math.BigDecimal) b.get("ventas_a");
            return vb.compareTo(va);
        });

        // Limitar a los Top 100 comparables para evitar saturación de la UI
        int limit = Math.min(resultado.size(), 100);
        return resultado.subList(0, limit);
    }
}
