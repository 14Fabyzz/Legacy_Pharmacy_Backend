package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.PeriodoVentaDTO;
import com.legacy.pharmacy.reportes.dto.ReporteVentasConsolidadasDTO;
import com.legacy.pharmacy.reportes.dto.ResumenInteligenteResponseDTO;
import com.legacy.pharmacy.reportes.dto.TopProductoResponseDTO;
import com.legacy.pharmacy.reportes.enums.Periodicidad;
import com.legacy.pharmacy.reportes.exception.BusinessException;
import com.legacy.pharmacy.reportes.exception.ResourceNotFoundException;
import com.legacy.pharmacy.reportes.repository.VentaReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para generación de reportes de ventas consolidadas.
 */
@Service
@Transactional(readOnly = true)
public class ReporteVentasService {

    private static final Logger log = LoggerFactory.getLogger(ReporteVentasService.class);

    private final VentaReporteRepository ventaReporteRepository;
    private final GeminiClientService geminiClientService;

    public ReporteVentasService(VentaReporteRepository ventaReporteRepository,
            GeminiClientService geminiClientService) {
        this.ventaReporteRepository = ventaReporteRepository;
        this.geminiClientService = geminiClientService;
    }

    /**
     * Genera un reporte consolidado de ventas para el rango y periodicidad dados.
     *
     * @param fechaInicio  Fecha inicial del rango
     * @param fechaFin     Fecha final del rango (inclusiva)
     * @param periodicidad Agrupación: DIARIO, SEMANAL o MENSUAL
     * @param sucursalId   Filtro opcional por sucursal (null = todas)
     * @return DTO con totales consolidados y desglose por período
     */
    public ReporteVentasConsolidadasDTO generarReporteConsolidado(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Periodicidad periodicidad,
            Integer sucursalId) {

        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Convertir a LocalDateTime para las queries
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX); // 23:59:59.999999999

        log.debug("Generando reporte consolidado: {} a {}, periodicidad={}, sucursal={}",
                fechaInicio, fechaFin, periodicidad, sucursalId);

        // 1. Obtener totales consolidados
        List<Object[]> totalesRaw = ventaReporteRepository.obtenerTotalesConsolidados(
                inicio, fin, sucursalId);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        Long cantidadVentas = 0L;

        if (totalesRaw != null && !totalesRaw.isEmpty()) {
            Object[] row = totalesRaw.get(0);
            totalIngresos = toBigDecimal(row[0]);
            totalIva = toBigDecimal(row[1]);
            cantidadVentas = toLong(row[2]);
        }

        // Verificar si hay ventas en el período
        if (cantidadVentas == 0) {
            throw new ResourceNotFoundException(
                    "No se encontraron ventas en el período consultado");
        }

        // 2. Calcular subtotal neto y descuentos
        BigDecimal subtotalNeto = totalIngresos.subtract(totalIva)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDescuentos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // 3. Obtener desglose por período
        List<Object[]> periodosRaw = obtenerPeriodosAgrupados(
                periodicidad, inicio, fin, sucursalId);

        List<PeriodoVentaDTO> periodos = new ArrayList<>();
        for (Object[] row : periodosRaw) {
            BigDecimal periodoIngresos = toBigDecimal(row[1]);
            BigDecimal periodoIva = toBigDecimal(row[2]);
            BigDecimal periodoSubtotal = periodoIngresos.subtract(periodoIva)
                    .setScale(2, RoundingMode.HALF_UP);

            PeriodoVentaDTO dto = PeriodoVentaDTO.builder()
                    .periodo((String) row[0])
                    .totalIngresos(periodoIngresos.setScale(2, RoundingMode.HALF_UP))
                    .totalIva(periodoIva.setScale(2, RoundingMode.HALF_UP))
                    .subtotalNeto(periodoSubtotal)
                    .totalDescuentos(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .cantidadVentas(toLong(row[3]))
                    .build();

            periodos.add(dto);
        }

        // 4. Construir respuesta
        return ReporteVentasConsolidadasDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .periodicidad(periodicidad)
                .sucursalId(sucursalId)
                .totalIngresos(totalIngresos.setScale(2, RoundingMode.HALF_UP))
                .totalIva(totalIva.setScale(2, RoundingMode.HALF_UP))
                .subtotalNeto(subtotalNeto)
                .totalDescuentos(totalDescuentos)
                .cantidadVentas(cantidadVentas)
                .periodos(periodos)
                .build();
    }

    /**
     * Genera un resumen narrativo de las ventas utilizando IA (Google Gemini).
     * 
     * @param fechaInicio  Fecha inicial
     * @param fechaFin     Fecha final
     * @param periodicidad Periodicidad del reporte base a analizar
     * @param sucursalId   Opcional
     * @return El resumen ejecutivo generado por la IA
     */
    public ResumenInteligenteResponseDTO generarResumenEjecutivo(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Periodicidad periodicidad,
            Integer sucursalId) {

        // 1. Obtener la data dura (Ventas Consolidadas)
        ReporteVentasConsolidadasDTO reporteBase = generarReporteConsolidado(
                fechaInicio, fechaFin, periodicidad, sucursalId);

        // 2. Obtener el top de productos (ej. top 5 para dar contexto extra)
        List<TopProductoResponseDTO> topProductos = obtenerTopRotacion(fechaInicio, fechaFin, 5);

        // 3. Construir el Prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append(
                "Actúa como un analista financiero experto de una cadena de farmacias y elabora un resumen ejecutivo narrativo.\n");
        prompt.append("A continuación te presento los datos de ventas consolidadas del ").append(fechaInicio)
                .append(" al ").append(fechaFin).append(".\n");
        prompt.append("Total Ingresos: $").append(reporteBase.getTotalIngresos()).append("\n");
        prompt.append("Subtotal Neto: $").append(reporteBase.getSubtotalNeto()).append("\n");
        prompt.append("Cantidad de Ventas: ").append(reporteBase.getCantidadVentas()).append("\n\n");

        prompt.append("Desglose de ingresos por periodo (Agrupación ").append(periodicidad).append("):\n");
        for (PeriodoVentaDTO p : reporteBase.getPeriodos()) {
            prompt.append("- Periodo ").append(p.getPeriodo())
                    .append(": Ingresos $").append(p.getTotalIngresos())
                    .append(" (").append(p.getCantidadVentas()).append(" ventas)\n");
        }
        prompt.append("\n");

        prompt.append("Top 5 productos más vendidos en el mismo periodo:\n");
        for (TopProductoResponseDTO prod : topProductos) {
            prompt.append("- ").append(prod.getNombreProducto())
                    .append(" (Cantidad: ").append(prod.getTotalVendido())
                    .append(", Ingresos generados: $").append(prod.getIngresoGenerado()).append(")\n");
        }

        prompt.append(
                "\nPor favor, redacta un análisis ejecutivo directo y altamente escaneable. Usa la siguiente estructura obligatoria: 1. Un breve párrafo introductorio. 2. Un subtítulo '### Hallazgos Clave' seguido de una lista de viñetas con los 3 datos más impactantes. 3. Un subtítulo '### Recomendación Comercial' seguido de una acción estratégica clara. Sé conciso y no uses texto de relleno.");

        // 4. Invocar a Gemini (forma sincrónica vía RestClient)
        String respuestaIA = geminiClientService.generateContentSync(prompt.toString());

        return ResumenInteligenteResponseDTO.builder()
                .resumenGenerado(respuestaIA)
                .reporteBase(reporteBase)
                .topProductos(topProductos)
                .build();
    }

    /**
     * Obtiene los datos agrupados según la periodicidad.
     */
    private List<Object[]> obtenerPeriodosAgrupados(
            Periodicidad periodicidad,
            LocalDateTime inicio,
            LocalDateTime fin,
            Integer sucursalId) {

        return switch (periodicidad) {
            case DIARIO -> ventaReporteRepository.obtenerAgrupadoPorDia(inicio, fin, sucursalId);
            case SEMANAL -> ventaReporteRepository.obtenerAgrupadoPorSemana(inicio, fin, sucursalId);
            case MENSUAL -> ventaReporteRepository.obtenerAgrupadoPorMes(inicio, fin, sucursalId);
        };
    }

    /**
     * Genera un reporte de productos de mayor rotación (Top Moving Products).
     *
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin    Fecha final del rango (inclusiva)
     * @param limite      Cantidad máxima de productos a retornar (por defecto 10)
     * @return Lista de DTOs con la información de los productos más vendidos
     */
    public List<TopProductoResponseDTO> obtenerTopRotacion(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer limite) {

        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Determinar el límite real
        int limit = (limite != null && limite > 0) ? limite : 10;

        // Convertir a LocalDateTime para las queries
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX); // 23:59:59.999999999

        log.debug("Generando reporte de Top {} productos de mayor rotación: {} a {}",
                limit, fechaInicio, fechaFin);

        // Consultar repositorio
        List<Object[]> resultados = ventaReporteRepository.obtenerTopProductosMayorRotacion(
                inicio, fin, limit);

        if (resultados == null || resultados.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron ventas para generar el ranking en el período consultado");
        }

        List<TopProductoResponseDTO> topProductos = new ArrayList<>();

        for (Object[] row : resultados) {
            String nombreProducto = (String) row[0];
            String presentacion = (String) row[1];
            Long totalVendido = toLong(row[2]);
            BigDecimal ingresoGenerado = toBigDecimal(row[3]).setScale(2, RoundingMode.HALF_UP);

            TopProductoResponseDTO dto = TopProductoResponseDTO.builder()
                    .nombreProducto(nombreProducto)
                    .presentacion(presentacion)
                    .totalVendido(totalVendido)
                    .ingresoGenerado(ingresoGenerado)
                    .build();

            topProductos.add(dto);
        }

        return topProductos;
    }

    /**
     * Genera un reporte consolidado de pagos para el rango dado.
     *
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin    Fecha final del rango (inclusiva)
     * @param sucursalId  Filtro opcional por sucursal (null = todas)
     * @return DTO con consolidado agrupado por método de pago
     */
    public com.legacy.pharmacy.reportes.dto.ConsolidadoPagosResponseDTO generarConsolidadoPagos(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer sucursalId) {

        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX); // 23:59:59.999999999

        log.debug("Generando reporte Consolidado de Pagos: {} a {}, sucursal={}",
                fechaInicio, fechaFin, sucursalId);

        // Consultar repositorio para agrupación
        List<Object[]> reporteIngresosRaw = ventaReporteRepository.obtenerIngresosPorMetodoPago(
                inicio, fin, sucursalId);

        if (reporteIngresosRaw == null || reporteIngresosRaw.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron ventas para calcular el consolidado de pagos en el período consultado");
        }

        BigDecimal granTotal = BigDecimal.ZERO;
        List<com.legacy.pharmacy.reportes.dto.MetodoPagoDTO> metodosPago = new ArrayList<>();

        // Primera pasada para calcular el Gran Total y listar métodos
        for (Object[] row : reporteIngresosRaw) {
            String nombreMetodo = (String) row[0];
            Long cantidadVentas = toLong(row[1]);
            BigDecimal totalRecaudado = toBigDecimal(row[2]);

            granTotal = granTotal.add(totalRecaudado);

            com.legacy.pharmacy.reportes.dto.MetodoPagoDTO dto = com.legacy.pharmacy.reportes.dto.MetodoPagoDTO
                    .builder()
                    .nombreMetodo(nombreMetodo != null ? nombreMetodo : "DESCONOCIDO")
                    .cantidadVentas(cantidadVentas)
                    .totalRecaudado(totalRecaudado.setScale(2, RoundingMode.HALF_UP))
                    .build();
            metodosPago.add(dto);
        }

        // Segunda pasada para calcular porcentajes de participación
        if (granTotal.compareTo(BigDecimal.ZERO) > 0) {
            for (com.legacy.pharmacy.reportes.dto.MetodoPagoDTO metodo : metodosPago) {
                BigDecimal porcentaje = metodo.getTotalRecaudado()
                        .divide(granTotal, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
                metodo.setPorcentajeParticipacion(porcentaje);
            }
        } else {
            for (com.legacy.pharmacy.reportes.dto.MetodoPagoDTO metodo : metodosPago) {
                metodo.setPorcentajeParticipacion(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
        }

        // Obtener desglose de referencias de pago
        List<Object[]> detallesRaw = ventaReporteRepository.obtenerDetallesReferenciaPago(
                inicio, fin, sucursalId);

        List<com.legacy.pharmacy.reportes.dto.DetalleReferenciaPagoDTO> detallesReferencias = new ArrayList<>();
        if (detallesRaw != null && !detallesRaw.isEmpty()) {
            for (Object[] row : detallesRaw) {
                Long idVenta = toLong(row[0]);
                String referenciaPago = (String) row[1];
                BigDecimal monto = toBigDecimal(row[2]);
                LocalDateTime fechaVenta = toLocalDateTime(row[3]);
                String metodoPago = (String) row[4];

                com.legacy.pharmacy.reportes.dto.DetalleReferenciaPagoDTO detalle = com.legacy.pharmacy.reportes.dto.DetalleReferenciaPagoDTO
                        .builder()
                        .idVenta(idVenta)
                        .referenciaPago(referenciaPago)
                        .monto(monto.setScale(2, RoundingMode.HALF_UP))
                        .fechaVenta(fechaVenta)
                        .metodoPago(metodoPago)
                        .build();

                detallesReferencias.add(detalle);
            }
        }

        return com.legacy.pharmacy.reportes.dto.ConsolidadoPagosResponseDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .sucursalId(sucursalId)
                .granTotal(granTotal.setScale(2, RoundingMode.HALF_UP))
                .metodosPago(metodosPago)
                .detallesReferencias(detallesReferencias)
                .build();
    }

    // ==========================================
    // Utilidades de conversión segura
    // ==========================================

    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd)
            return bd;
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null)
            return 0L;
        if (value instanceof Long l)
            return l;
        if (value instanceof Number n)
            return n.longValue();
        return Long.parseLong(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null)
            return null;
        if (value instanceof LocalDateTime ldt)
            return ldt;
        if (value instanceof java.sql.Timestamp ts)
            return ts.toLocalDateTime();
        if (value instanceof java.sql.Date d)
            return d.toLocalDate().atStartOfDay();
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }
}
