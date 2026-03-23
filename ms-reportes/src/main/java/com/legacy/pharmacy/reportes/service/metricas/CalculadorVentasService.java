package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.service.RecolectorDatosService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class CalculadorVentasService {

    private final RecolectorDatosService recolectorDatosService;

    public CalculadorVentasService(RecolectorDatosService recolectorDatosService) {
        this.recolectorDatosService = recolectorDatosService;
    }

    public VentasClientesMetricasDTO calcularMotor(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        try {
            VentasRawDTO ventas = recolectorDatosService.obtenerVentasSincrono(inicio, fin, sucursalId);

            BigDecimal totalIngresos = ventas.getTotalIngresos();
            BigDecimal numTransacciones = BigDecimal.valueOf(ventas.getNumeroTransacciones());
            BigDecimal unidadesVendidas = BigDecimal.valueOf(ventas.getUnidadesVendidas());
            BigDecimal cogs = ventas.getCostoMercanciaVendidaCogs();

            // Ticket Promedio = totalIngresos / numeroTransacciones
            BigDecimal ticketPromedio = dividirSeguro(totalIngresos, numTransacciones);

            // UPT = unidadesVendidas / numeroTransacciones
            BigDecimal upt = dividirSeguro(unidadesVendidas, numTransacciones);

            // Margen Bruto % = ((totalIngresos - cogs) / totalIngresos) * 100
            BigDecimal margenBrutoReal = totalIngresos.subtract(cogs);
            BigDecimal margenBrutoPorcentaje = dividirSeguro(margenBrutoReal, totalIngresos).multiply(BigDecimal.valueOf(100));

            return VentasClientesMetricasDTO.builder()
                    .ticketPromedio(ticketPromedio.setScale(2, RoundingMode.HALF_UP))
                    .unitsPerTransactionUpt(upt.setScale(2, RoundingMode.HALF_UP))
                    .margenUtilidadBruta(margenBrutoPorcentaje.setScale(2, RoundingMode.HALF_UP))
                    .build();
        } catch (Exception e) {
            return VentasClientesMetricasDTO.builder()
                    .ticketPromedio(BigDecimal.ZERO)
                    .unitsPerTransactionUpt(BigDecimal.ZERO)
                    .margenUtilidadBruta(BigDecimal.ZERO)
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
