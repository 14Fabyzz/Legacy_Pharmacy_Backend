package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.repository.VentaReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Servicio que recolecta datos crudos de ventas e inventario.
 * - Ventas: consulta directa a la BD compartida (PostgreSQL)
 * - Inventario: estimación basada en ventas (hasta integrar inventory-service)
 */
@Service
public class RecolectorDatosService {

    private static final Logger log = LoggerFactory.getLogger(RecolectorDatosService.class);

    private final VentaReporteRepository ventaReporteRepository;

    public RecolectorDatosService(VentaReporteRepository ventaReporteRepository) {
        this.ventaReporteRepository = ventaReporteRepository;
    }

    /**
     * Obtiene datos de ventas directamente desde la BD compartida de PostgreSQL.
     */
    @Transactional(readOnly = true)
    public VentasRawDTO obtenerVentasSincrono(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        log.info("Consultando datos de ventas desde la BD para sucursal {} [{} - {}]", sucursalId, inicio, fin);

        try {
            var inicioDateTime = inicio.atStartOfDay();
            var finDateTime = fin.atTime(LocalTime.MAX);

            BigDecimal totalIngresos = ventaReporteRepository.obtenerTotalIngresos(inicioDateTime, finDateTime, sucursalId);
            Long numTransacciones = ventaReporteRepository.contarTransacciones(inicioDateTime, finDateTime, sucursalId);
            Long unidadesVendidas = ventaReporteRepository.sumarUnidadesVendidas(inicioDateTime, finDateTime, sucursalId);

            // Estimación de COGS (70% del ingreso) hasta tener datos reales de inventario
            BigDecimal cogs = totalIngresos != null ? totalIngresos.multiply(BigDecimal.valueOf(0.70)) : BigDecimal.ZERO;

            VentasRawDTO dto = VentasRawDTO.builder()
                    .totalIngresos(totalIngresos != null ? totalIngresos : BigDecimal.ZERO)
                    .costoMercanciaVendidaCogs(cogs)
                    .numeroTransacciones(numTransacciones != null ? numTransacciones.intValue() : 0)
                    .unidadesVendidas(unidadesVendidas != null ? unidadesVendidas : 0L)
                    .build();

            log.info("Datos de ventas obtenidos: ingresos={}, COGS={}, transacciones={}, unidades={}",
                    dto.getTotalIngresos(), dto.getCostoMercanciaVendidaCogs(),
                    dto.getNumeroTransacciones(), dto.getUnidadesVendidas());

            return dto;
        } catch (Exception e) {
            log.error("Error al consultar datos de ventas desde la BD", e);
            return VentasRawDTO.builder()
                    .totalIngresos(BigDecimal.ZERO)
                    .costoMercanciaVendidaCogs(BigDecimal.ZERO)
                    .numeroTransacciones(0)
                    .unidadesVendidas(0L)
                    .build();
        }
    }

    /**
     * Genera datos estimados de inventario basados en ventas.
     * Esta es una aproximación hasta que se integre el inventory-service.
     */
    public InventarioRawDTO obtenerInventarioSincrono(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        log.info("Generando datos estimados de inventario para sucursal {} [{} - {}]", sucursalId, inicio, fin);

        try {
            var inicioDateTime = inicio.atStartOfDay();
            var finDateTime = fin.atTime(LocalTime.MAX);

            BigDecimal totalIngresos = ventaReporteRepository.obtenerTotalIngresos(inicioDateTime, finDateTime, sucursalId);
            Long unidadesVendidas = ventaReporteRepository.sumarUnidadesVendidas(inicioDateTime, finDateTime, sucursalId);

            if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;
            if (unidadesVendidas == null) unidadesVendidas = 0L;

            // Estimaciones basadas en ratios típicos de farmacia
            BigDecimal costoEstimado = totalIngresos.multiply(BigDecimal.valueOf(0.70));
            BigDecimal inventarioPromedio = costoEstimado.multiply(BigDecimal.valueOf(1.5));
            long unidadesRecibidas = Math.round(unidadesVendidas * 1.2);

            InventarioRawDTO dto = InventarioRawDTO.builder()
                    .inventarioPromedio(inventarioPromedio)
                    .valorInventarioActual(costoEstimado)
                    .unidadesRecibidas(unidadesRecibidas)
                    .valorInventarioTeorico(inventarioPromedio)
                    .valorInventarioFisico(inventarioPromedio.multiply(BigDecimal.valueOf(0.98)))
                    .build();

            log.info("Inventario estimado: promedio={}, actual={}, recibidas={}",
                    dto.getInventarioPromedio(), dto.getValorInventarioActual(), dto.getUnidadesRecibidas());

            return dto;
        } catch (Exception e) {
            log.error("Error al generar datos estimados de inventario", e);
            return InventarioRawDTO.builder()
                    .inventarioPromedio(BigDecimal.ZERO)
                    .valorInventarioActual(BigDecimal.ZERO)
                    .unidadesRecibidas(0L)
                    .valorInventarioTeorico(BigDecimal.ZERO)
                    .valorInventarioFisico(BigDecimal.ZERO)
                    .build();
        }
    }
}
