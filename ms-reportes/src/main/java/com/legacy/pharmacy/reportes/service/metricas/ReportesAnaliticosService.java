package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.repository.VentaReporteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportesAnaliticosService {

    private final VentaReporteRepository ventaReporteRepository;

    public ReportesAnaliticosService(VentaReporteRepository ventaReporteRepository) {
        this.ventaReporteRepository = ventaReporteRepository;
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
}
