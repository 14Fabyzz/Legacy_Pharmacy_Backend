package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.entity.ParametrosOperativos;
import com.legacy.pharmacy.reportes.repository.ParametrosOperativosRepository;
import com.legacy.pharmacy.reportes.service.RecolectorDatosService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class CalculadorVentasService {

    private final RecolectorDatosService recolectorDatosService;
    private final ParametrosOperativosRepository parametrosOperativosRepository;

    public CalculadorVentasService(RecolectorDatosService recolectorDatosService, 
                                   ParametrosOperativosRepository parametrosOperativosRepository) {
        this.recolectorDatosService = recolectorDatosService;
        this.parametrosOperativosRepository = parametrosOperativosRepository;
    }

    public VentasClientesMetricasDTO calcularMotor(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        try {
            VentasRawDTO ventas = VentasRawDTO.builder()
                    .totalIngresos(BigDecimal.ZERO)
                    .costoMercanciaVendidaCogs(BigDecimal.ZERO)
                    .numeroTransacciones(0)
                    .unidadesVendidas(0L)
                    .build();

            Optional<ParametrosOperativos> opParametros = sucursalId != null 
                ? parametrosOperativosRepository.findBySucursalId(sucursalId) 
                : Optional.empty();
                
            Long traficoDiario = opParametros.map(ParametrosOperativos::getTraficoPersonasDiarioPromedio).orElse(0L);

            BigDecimal totalIngresos = ventas.getTotalIngresos();
            BigDecimal numTransacciones = BigDecimal.valueOf(ventas.getNumeroTransacciones());
            BigDecimal unidadesVendidas = BigDecimal.valueOf(ventas.getUnidadesVendidas());
            BigDecimal cogs = ventas.getCostoMercanciaVendidaCogs();

            // Ticket Promedio = totalIngresos / numeroTransacciones
            BigDecimal ticketPromedio = safeDivide(totalIngresos, numTransacciones);

            // UPT = unidadesVendidas / numeroTransacciones
            BigDecimal upt = safeDivide(unidadesVendidas, numTransacciones);

            // Tasa de Conversión = (numeroTransacciones / traficoPersonasDiarioPromedio total del periodo) * 100
            long dias = Math.max(1, ChronoUnit.DAYS.between(inicio, fin));
            BigDecimal traficoTotal = BigDecimal.valueOf(traficoDiario * dias);
            BigDecimal tasaConversion = safeDivide(numTransacciones, traficoTotal).multiply(BigDecimal.valueOf(100));

            // Margen Bruto % = ((totalIngresos - cogs) / totalIngresos) * 100
            BigDecimal margenBrutoReal = totalIngresos.subtract(cogs);
            BigDecimal margenBrutoPorcentaje = safeDivide(margenBrutoReal, totalIngresos).multiply(BigDecimal.valueOf(100));

            return VentasClientesMetricasDTO.builder()
                    .ticketPromedio(ticketPromedio.setScale(2, RoundingMode.HALF_UP))
                    .unitsPerTransactionUpt(upt.setScale(2, RoundingMode.HALF_UP))
                    .tasaConversion(tasaConversion.setScale(2, RoundingMode.HALF_UP))
                    .margenUtilidadBruta(margenBrutoPorcentaje.setScale(2, RoundingMode.HALF_UP))
                    .build();
        } catch (Exception e) {
            return VentasClientesMetricasDTO.builder()
                    .ticketPromedio(BigDecimal.ZERO)
                    .unitsPerTransactionUpt(BigDecimal.ZERO)
                    .tasaConversion(BigDecimal.ZERO)
                    .margenUtilidadBruta(BigDecimal.ZERO)
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
