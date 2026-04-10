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
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.legacy.pharmacy.reportes.dto.cierre.CierreTurnoConciliacionDTO;
import com.legacy.pharmacy.reportes.dto.cierre.MovimientoTurnoDTO;

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

    /**
     * Obtiene la cabecera (datos teóricos y reales) del Cierre de un Turno específico.
     */
    @Transactional(readOnly = true)
    public CierreTurnoConciliacionDTO obtenerCabeceraTurno(Long turnoId) {
        log.info("Recolectando cabecera de conciliacion para el turno: {}", turnoId);
        List<Map<String, Object>> rawList = ventaReporteRepository.getEncabezadoCierreTurno(turnoId);
        if (rawList == null || rawList.isEmpty()) {
            return null; // El turno no existe o es nulo
        }

        Map<String, Object> raw = rawList.get(0);

        CierreTurnoConciliacionDTO dto = new CierreTurnoConciliacionDTO();
        dto.setId(mapToLong(raw.get("id")));
        dto.setUsuarioId(mapToString(raw.get("usuario_id")));
        dto.setSucursalId(mapToInteger(raw.get("sucursal_id")));
        dto.setEstado(mapToString(raw.get("estado")));
        dto.setFechaApertura(mapToLocalDateTime(raw.get("fecha_apertura")));
        dto.setFechaCierre(mapToLocalDateTime(raw.get("fecha_cierre")));
        dto.setSaldoInicial(mapToBigDecimal(raw.get("saldo_inicial")));
        dto.setTotalVentasTeorico(mapToBigDecimal(raw.get("total_ventas_teorico")));
        dto.setTotalEfectivoReal(mapToBigDecimal(raw.get("total_efectivo_real")));
        dto.setTotalEgresos(mapToBigDecimal(raw.get("total_egresos")));
        dto.setDiferencia(mapToBigDecimal(raw.get("diferencia")));
        dto.setObservacionesCierre(mapToString(raw.get("observaciones_cierre")));
        
        return dto;
    }

    /**
     * Obtiene una lista de cabeceras de Cierres de Turno en un rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<CierreTurnoConciliacionDTO> obtenerCierresTurnoRango(LocalDate inicio, LocalDate fin) {
        log.info("Recolectando cierres de turno en el rango {} a {}", inicio, fin);
        
        var inicioDateTime = inicio.atStartOfDay();
        var finDateTime = fin.atTime(LocalTime.MAX);
        
        List<Map<String, Object>> rawList = ventaReporteRepository.getCierresTurnoRangoFechas(inicioDateTime, finDateTime);
        
        return rawList.stream().map(raw -> {
            CierreTurnoConciliacionDTO dto = new CierreTurnoConciliacionDTO();
            dto.setId(mapToLong(raw.get("id")));
            dto.setUsuarioId(mapToString(raw.get("usuario_id")));
            dto.setSucursalId(mapToInteger(raw.get("sucursal_id")));
            dto.setEstado(mapToString(raw.get("estado")));
            dto.setFechaApertura(mapToLocalDateTime(raw.get("fecha_apertura")));
            dto.setFechaCierre(mapToLocalDateTime(raw.get("fecha_cierre")));
            dto.setSaldoInicial(mapToBigDecimal(raw.get("saldo_inicial")));
            dto.setTotalVentasTeorico(mapToBigDecimal(raw.get("total_ventas_teorico")));
            dto.setTotalEfectivoReal(mapToBigDecimal(raw.get("total_efectivo_real")));
            dto.setTotalEgresos(mapToBigDecimal(raw.get("total_egresos")));
            dto.setDiferencia(mapToBigDecimal(raw.get("diferencia")));
            dto.setObservacionesCierre(mapToString(raw.get("observaciones_cierre")));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene todos los movimientos asociados a un Turno.
     */
    @Transactional(readOnly = true)
    public List<MovimientoTurnoDTO> obtenerMovimientosDelTurno(Long turnoId) {
        log.info("Recolectando movimientos del turno: {}", turnoId);
        List<Map<String, Object>> rawList = ventaReporteRepository.getMovimientosPorTurno(turnoId);
        
        return rawList.stream().map(raw -> {
            MovimientoTurnoDTO dto = new MovimientoTurnoDTO();
            dto.setId(mapToLong(raw.get("id")));
            dto.setFecha(mapToLocalDateTime(raw.get("fecha")));
            dto.setTipo(mapToString(raw.get("tipo")));
            dto.setMonto(mapToBigDecimal(raw.get("monto")));
            dto.setReferencia(mapToString(raw.get("referencia")));
            dto.setDescripcion(mapToString(raw.get("descripcion")));
            return dto;
        }).collect(Collectors.toList());
    }

    // Utilidades de casteo seguro para los Maps nativos
    
    private Long mapToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) { return null; }
    }

    private String mapToString(Object obj) {
        if (obj == null) return null;
        return obj.toString();
    }
    
    private Integer mapToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) { return null; }
    }
    
    private BigDecimal mapToBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return BigDecimal.valueOf(((Number) obj).doubleValue());
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) { return BigDecimal.ZERO; }
    }
    
    private LocalDateTime mapToLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Timestamp) return ((Timestamp) obj).toLocalDateTime();
        if (obj instanceof java.sql.Date) return ((java.sql.Date) obj).toLocalDate().atStartOfDay();
        if (obj instanceof LocalDateTime) return (LocalDateTime) obj;
        
        String str = obj.toString();
        if (str.contains(" ")) {
            str = str.replace(" ", "T");
        }
        try {
            return LocalDateTime.parse(str);
        } catch (Exception e) {
            try {
                return LocalDate.parse(str).atStartOfDay();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
