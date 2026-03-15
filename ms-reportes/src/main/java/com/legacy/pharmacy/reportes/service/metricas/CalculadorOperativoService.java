package com.legacy.pharmacy.reportes.service.metricas;

import com.legacy.pharmacy.reportes.dto.EficienciaOperativaMetricasDTO;
import com.legacy.pharmacy.reportes.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.entity.ParametrosOperativos;
import com.legacy.pharmacy.reportes.repository.ParametrosOperativosRepository;
import com.legacy.pharmacy.reportes.service.RecolectorDatosService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class CalculadorOperativoService {

    private final RecolectorDatosService recolectorDatosService;
    private final ParametrosOperativosRepository parametrosOperativosRepository;

    public CalculadorOperativoService(RecolectorDatosService recolectorDatosService, 
                                      ParametrosOperativosRepository parametrosOperativosRepository) {
        this.recolectorDatosService = recolectorDatosService;
        this.parametrosOperativosRepository = parametrosOperativosRepository;
    }

    public EficienciaOperativaMetricasDTO calcularSalud(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        try {
            VentasRawDTO ventas = VentasRawDTO.builder()
                    .totalIngresos(BigDecimal.ZERO)
                    .costoMercanciaVendidaCogs(BigDecimal.ZERO)
                    .build();

            InventarioRawDTO inventario = InventarioRawDTO.builder()
                    .valorInventarioTeorico(BigDecimal.ZERO)
                    .valorInventarioFisico(BigDecimal.ZERO)
                    .build();

            Optional<ParametrosOperativos> opParametros = sucursalId != null 
                    ? parametrosOperativosRepository.findBySucursalId(sucursalId) 
                    : Optional.empty();

            BigDecimal metrosCuadrados = opParametros.map(ParametrosOperativos::getMetrosCuadrados).orElse(BigDecimal.valueOf(1));
            BigDecimal costosFijosMensuales = opParametros.map(ParametrosOperativos::getCostosFijosMensuales).orElse(BigDecimal.ZERO);

            BigDecimal totalIngresos = ventas.getTotalIngresos();
            BigDecimal cogs = ventas.getCostoMercanciaVendidaCogs();
            BigDecimal valorInventarioTeorico = inventario.getValorInventarioTeorico();
            BigDecimal valorInventarioFisico = inventario.getValorInventarioFisico();

            // Ventas por m2 = totalIngresos / metrosCuadrados
            BigDecimal ventasPorM2 = safeDivide(totalIngresos, metrosCuadrados);

            // Merma % = (valorInventarioTeorico - valorInventarioFisico) / totalIngresos * 100
            BigDecimal diferenciaInventario = valorInventarioTeorico.subtract(valorInventarioFisico);
            BigDecimal mermaPorcentaje = safeDivide(diferenciaInventario, totalIngresos).multiply(BigDecimal.valueOf(100));

            // Margen Contribucion % = ((totalIngresos - cogs) / totalIngresos)
            BigDecimal margenContrPorcentaje = safeDivide(totalIngresos.subtract(cogs), totalIngresos);

            // Punto de Equilibrio = costosFijosMensuales / Margen Contribucion % (expresado en decimal)
            BigDecimal puntoEquilibrio = safeDivide(costosFijosMensuales, margenContrPorcentaje);

            return EficienciaOperativaMetricasDTO.builder()
                    .ventasPorMetroCuadrado(ventasPorM2.setScale(2, RoundingMode.HALF_UP))
                    .porcentajeMerma(mermaPorcentaje.setScale(2, RoundingMode.HALF_UP))
                    .puntoEquilibrio(puntoEquilibrio.setScale(2, RoundingMode.HALF_UP))
                    .build();
        } catch (Exception e) {
            return EficienciaOperativaMetricasDTO.builder()
                    .ventasPorMetroCuadrado(BigDecimal.ZERO)
                    .porcentajeMerma(BigDecimal.ZERO)
                    .puntoEquilibrio(BigDecimal.ZERO)
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
