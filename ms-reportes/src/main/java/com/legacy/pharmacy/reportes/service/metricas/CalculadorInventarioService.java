package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
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

    public CalculadorInventarioService(RecolectorDatosService recolectorDatosService) {
        this.recolectorDatosService = recolectorDatosService;
    }

    public GestionInventarioMetricasDTO calcularPulso(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        try {
            VentasRawDTO ventas = recolectorDatosService.obtenerVentasSincrono(inicio, fin, sucursalId);
            InventarioRawDTO inventario = recolectorDatosService.obtenerInventarioSincrono(inicio, fin, sucursalId);

            BigDecimal cogs = ventas.getCostoMercanciaVendidaCogs();
            BigDecimal invPromedio = inventario.getInventarioPromedio();
            BigDecimal totalIngresos = ventas.getTotalIngresos();
            
            // IRI = cogs / inventarioPromedio
            BigDecimal iri = dividirSeguro(cogs, invPromedio);
            
            // Margen Bruto Monetario = totalIngresos - cogs
            BigDecimal margenBrutoMonetario = totalIngresos.subtract(cogs);
            
            // GMROI = Margen Bruto Monetario / inventarioPromedio
            BigDecimal gmroi = dividirSeguro(margenBrutoMonetario, invPromedio);
            
            // Sell-Through = (unidadesVendidas / unidadesRecibidas) * 100
            BigDecimal unidadesVendidas = BigDecimal.valueOf(ventas.getUnidadesVendidas());
            BigDecimal unidadesRecibidas = BigDecimal.valueOf(inventario.getUnidadesRecibidas());
            BigDecimal sellThrough = dividirSeguro(unidadesVendidas, unidadesRecibidas).multiply(BigDecimal.valueOf(100));
            
            // Semanas del periodo = Días entre inicio y fin divididos por 7 (mínimo 1)
            long dias = Math.max(1, ChronoUnit.DAYS.between(inicio, fin));
            BigDecimal semanas = BigDecimal.valueOf(Math.max(1, dias / 7.0));
            
            // Promedio Ventas Semanales = unidadesVendidas / Semanas del periodo
            BigDecimal promVentasSemanales = dividirSeguro(unidadesVendidas, semanas);
            
            // WOS = valorInventarioActual / Promedio Ventas Semanales
            BigDecimal valorInventarioActual = inventario.getValorInventarioActual();
            BigDecimal wos = dividirSeguro(valorInventarioActual, promVentasSemanales);

            return GestionInventarioMetricasDTO.builder()
                    .rotacionInventarioIri(iri.setScale(2, RoundingMode.HALF_UP))
                    .gmroi(gmroi.setScale(2, RoundingMode.HALF_UP))
                    .sellThroughRate(sellThrough.setScale(2, RoundingMode.HALF_UP))
                    .weeksOfSupplyWos(wos.setScale(2, RoundingMode.HALF_UP))
                    .build();
        } catch (Exception e) {
            return GestionInventarioMetricasDTO.builder()
                    .rotacionInventarioIri(BigDecimal.ZERO)
                    .gmroi(BigDecimal.ZERO)
                    .sellThroughRate(BigDecimal.ZERO)
                    .weeksOfSupplyWos(BigDecimal.ZERO)
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
