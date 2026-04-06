package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
import com.legacy.pharmacy.reportes.client.InventarioClient;
import com.legacy.pharmacy.reportes.dto.InventarioConsolidadoDTO;
import com.legacy.pharmacy.reportes.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.service.RecolectorDatosService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CalculadorInventarioService {

    private final RecolectorDatosService recolectorDatosService;
    private final InventarioClient inventarioClient;

    public CalculadorInventarioService(RecolectorDatosService recolectorDatosService, InventarioClient inventarioClient) {
        this.recolectorDatosService = recolectorDatosService;
        this.inventarioClient = inventarioClient;
    }

    public GestionInventarioMetricasDTO calcularPulso(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        try {
            VentasRawDTO ventas = recolectorDatosService.obtenerVentasSincrono(inicio, fin, sucursalId);
            InventarioConsolidadoDTO inventarioReal = inventarioClient.obtenerMetricasConsolidado(inicio, fin, sucursalId);

            boolean isRealData = true;
            BigDecimal cogs;
            BigDecimal invPromedio;
            BigDecimal valorInventarioActual;
            long unidadesRecibidas;
            
            if (inventarioReal != null) {
                cogs = inventarioReal.getCogs() != null ? inventarioReal.getCogs() : BigDecimal.ZERO;
                invPromedio = inventarioReal.getInventarioPromedio() != null ? inventarioReal.getInventarioPromedio() : BigDecimal.ZERO;
                valorInventarioActual = inventarioReal.getValorInventarioActual() != null ? inventarioReal.getValorInventarioActual() : BigDecimal.ZERO;
                unidadesRecibidas = inventarioReal.getUnidadesRecibidas() != null ? inventarioReal.getUnidadesRecibidas() : 0L;
            } else {
                // Fallback: usar estimaciones si el endpoint real falló (Circuit Breaker)
                isRealData = false;
                InventarioRawDTO inventarioEstimado = recolectorDatosService.obtenerInventarioSincrono(inicio, fin, sucursalId);
                cogs = ventas.getCostoMercanciaVendidaCogs(); // Estimado en ventas
                invPromedio = inventarioEstimado.getInventarioPromedio();
                valorInventarioActual = inventarioEstimado.getValorInventarioActual();
                unidadesRecibidas = inventarioEstimado.getUnidadesRecibidas();
            }

            BigDecimal totalIngresos = ventas.getTotalIngresos();
            
            // IRI = cogs / inventarioPromedio
            BigDecimal iri = dividirSeguro(cogs, invPromedio);
            
            // Margen Bruto Monetario = totalIngresos - cogs
            BigDecimal margenBrutoMonetario = totalIngresos.subtract(cogs);
            
            // GMROI = Margen Bruto Monetario / inventarioPromedio
            BigDecimal gmroi = dividirSeguro(margenBrutoMonetario, invPromedio);
            
            // Sell-Through = (unidadesVendidas / unidadesRecibidas) * 100
            BigDecimal unidadesVendidas = BigDecimal.valueOf(ventas.getUnidadesVendidas());
            BigDecimal unidadesRecibidasBd = BigDecimal.valueOf(unidadesRecibidas);
            BigDecimal sellThrough = dividirSeguro(unidadesVendidas, unidadesRecibidasBd).multiply(BigDecimal.valueOf(100));
            
            // Semanas del periodo = Días entre inicio y fin divididos por 7 (mínimo 1)
            long dias = Math.max(1, ChronoUnit.DAYS.between(inicio, fin));
            BigDecimal semanas = BigDecimal.valueOf(Math.max(1, dias / 7.0));
            
            // Promedio Ventas Semanales = unidadesVendidas / Semanas del periodo
            BigDecimal promVentasSemanales = dividirSeguro(unidadesVendidas, semanas);
            
            // WOS = valorInventarioActual / Promedio Ventas Semanales
            BigDecimal wos = dividirSeguro(valorInventarioActual, promVentasSemanales);

            return GestionInventarioMetricasDTO.builder()
                    .rotacionInventarioIri(iri.setScale(2, RoundingMode.HALF_UP))
                    .gmroi(gmroi.setScale(2, RoundingMode.HALF_UP))
                    .sellThroughRate(sellThrough.setScale(2, RoundingMode.HALF_UP))
                    .weeksOfSupplyWos(wos.setScale(2, RoundingMode.HALF_UP))
                    .isRealData(isRealData)
                    .build();
        } catch (Exception e) {
            return GestionInventarioMetricasDTO.builder()
                    .rotacionInventarioIri(BigDecimal.ZERO)
                    .gmroi(BigDecimal.ZERO)
                    .sellThroughRate(BigDecimal.ZERO)
                    .weeksOfSupplyWos(BigDecimal.ZERO)
                    .isRealData(false)
                    .build();
        }
    }

    private BigDecimal dividirSeguro(BigDecimal dividendo, BigDecimal divisor) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividendo.divide(divisor, 2, java.math.RoundingMode.HALF_UP);
    }
}
