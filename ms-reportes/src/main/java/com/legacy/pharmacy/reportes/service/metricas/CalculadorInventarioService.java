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
            // Este método en el futuro llamará a recolectarMapearDatosAsincronamente
            // Pero por ahora asumimos que los datos están disponibles (mock) a través de una llamada sincrónica o join
            // o simplemente usamos un mock temporal para que compile y cuadre la lógica, hasta ajustar la orquestación.
            // Para poder compilar la lógica matemática, instancio DTOs vacíos:
            VentasRawDTO ventas = VentasRawDTO.builder()
                    .totalIngresos(BigDecimal.ZERO)
                    .costoMercanciaVendidaCogs(BigDecimal.ZERO)
                    .numeroTransacciones(0)
                    .unidadesVendidas(0L)
                    .build();

            InventarioRawDTO inventario = InventarioRawDTO.builder()
                    .valorInventarioActual(BigDecimal.ZERO)
                    .inventarioPromedio(BigDecimal.ZERO)
                    .unidadesRecibidas(0L)
                    .valorInventarioTeorico(BigDecimal.ZERO)
                    .valorInventarioFisico(BigDecimal.ZERO)
                    .build();

            BigDecimal cogs = ventas.getCostoMercanciaVendidaCogs();
            BigDecimal invPromedio = inventario.getInventarioPromedio();
            BigDecimal totalIngresos = ventas.getTotalIngresos();
            
            // IRI = cogs / inventarioPromedio
            BigDecimal iri = safeDivide(cogs, invPromedio);
            
            // Margen Bruto Monetario = totalIngresos - cogs
            BigDecimal margenBrutoMonetario = totalIngresos.subtract(cogs);
            
            // GMROI = Margen Bruto Monetario / inventarioPromedio
            BigDecimal gmroi = safeDivide(margenBrutoMonetario, invPromedio);
            
            // Sell-Through = (unidadesVendidas / unidadesRecibidas) * 100
            BigDecimal unidadesVendidas = BigDecimal.valueOf(ventas.getUnidadesVendidas());
            BigDecimal unidadesRecibidas = BigDecimal.valueOf(inventario.getUnidadesRecibidas());
            BigDecimal sellThrough = safeDivide(unidadesVendidas, unidadesRecibidas).multiply(BigDecimal.valueOf(100));
            
            // Semanas del periodo = Días entre inicio y fin divididos por 7 (mínimo 1)
            long dias = Math.max(1, ChronoUnit.DAYS.between(inicio, fin));
            BigDecimal semanas = BigDecimal.valueOf(Math.max(1, dias / 7.0));
            
            // Promedio Ventas Semanales = unidadesVendidas / Semanas del periodo
            BigDecimal promVentasSemanales = safeDivide(unidadesVendidas, semanas);
            
            // WOS = valorInventarioActual / Promedio Ventas Semanales
            BigDecimal valorInventarioActual = inventario.getValorInventarioActual();
            BigDecimal wos = safeDivide(valorInventarioActual, promVentasSemanales);

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

    private BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, 4, RoundingMode.HALF_UP);
    }
}
