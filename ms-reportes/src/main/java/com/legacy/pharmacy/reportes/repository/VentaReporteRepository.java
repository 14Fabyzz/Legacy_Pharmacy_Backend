package com.legacy.pharmacy.reportes.repository;
import java.util.List;
import com.legacy.pharmacy.reportes.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repositorio de consultas de ventas para reportes (solo lectura).
 * Usa queries nativas PostgreSQL para agregaciones.
 */
@Repository
public interface VentaReporteRepository extends JpaRepository<Venta, Long> {

    /**
     * Suma el total de ingresos (campo 'total') de ventas válidas en el periodo.
     * Excluye ventas ANULADAS y DEVUELTAS.
     */
    @Query(value = "SELECT COALESCE(SUM(v.total), 0) FROM ventas v " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA')", nativeQuery = true)
    BigDecimal obtenerTotalIngresos(@Param("inicio") LocalDateTime inicio,
                                    @Param("fin") LocalDateTime fin,
                                    @Param("sucursalId") Integer sucursalId);

    /**
     * Cuenta el número de transacciones válidas en el periodo.
     */
    @Query(value = "SELECT COUNT(v.id) FROM ventas v " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA')", nativeQuery = true)
    Long contarTransacciones(@Param("inicio") LocalDateTime inicio,
                              @Param("fin") LocalDateTime fin,
                              @Param("sucursalId") Integer sucursalId);

    /**
     * Suma las unidades vendidas de detalle_ventas asociadas a ventas válidas.
     */
    @Query(value = "SELECT COALESCE(SUM(dv.cantidad), 0) FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA')", nativeQuery = true)
    Long sumarUnidadesVendidas(@Param("inicio") LocalDateTime inicio,
                                @Param("fin") LocalDateTime fin,
                                @Param("sucursalId") Integer sucursalId);

    /**
     * Suma los subtotales de detalle_ventas (aproximación a ingresos desde el detalle).
     */
    @Query(value = "SELECT COALESCE(SUM(dv.subtotal), 0) FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA')", nativeQuery = true)
    BigDecimal sumarSubtotalesDetalle(@Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin,
                                       @Param("sucursalId") Integer sucursalId);

    // ==========================================
    // QUERIES REPORTES ANALITICOS
    // ==========================================

    @Query(value = "SELECT COALESCE(c.nombre || ' ' || c.apellido, c.nombre, 'Cliente Mostrador') as cliente, " +
            "SUM(v.total) as total, COUNT(v.id) as transacciones " +
            "FROM ventas v " +
            "LEFT JOIN clientes c ON v.cliente_id = c.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY c.id, c.nombre, c.apellido " +
            "ORDER BY total DESC", nativeQuery = true)
    List<java.util.Map<String, Object>> getVentasPorCliente(@Param("inicio") LocalDateTime inicio,
                                                           @Param("fin") LocalDateTime fin,
                                                           @Param("sucursalId") Integer sucursalId);

    @Query(value = "SELECT COALESCE(c.nombre || ' ' || c.apellido, c.nombre, 'Cliente Mostrador') as cliente, " +
            "dv.producto_nombre as producto, " +
            "SUM(dv.cantidad) as unidades, SUM(dv.subtotal) as total " +
            "FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "LEFT JOIN clientes c ON v.cliente_id = c.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY c.id, c.nombre, c.apellido, dv.producto_id, dv.producto_nombre " +
            "ORDER BY total DESC", nativeQuery = true)
    List<java.util.Map<String, Object>> getVentasClienteProducto(@Param("inicio") LocalDateTime inicio,
                                                                @Param("fin") LocalDateTime fin,
                                                                @Param("sucursalId") Integer sucursalId);

    @Query(value = "SELECT CAST(v.fecha_venta AS DATE) as fecha, " +
            "SUM(v.total) as total, COUNT(v.id) as transacciones " +
            "FROM ventas v " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY CAST(v.fecha_venta AS DATE) " +
            "ORDER BY fecha ASC", nativeQuery = true)
    List<java.util.Map<String, Object>> getConsolidadoVentas(@Param("inicio") LocalDateTime inicio,
                                                            @Param("fin") LocalDateTime fin,
                                                            @Param("sucursalId") Integer sucursalId);

    @Query(value = "SELECT TO_CHAR(v.fecha_venta, 'YYYY-MM') as mes, " +
            "SUM(v.total) as total, COUNT(v.id) as transacciones " +
            "FROM ventas v " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY TO_CHAR(v.fecha_venta, 'YYYY-MM') " +
            "ORDER BY mes ASC", nativeQuery = true)
    List<java.util.Map<String, Object>> getComparativoMensual(@Param("inicio") LocalDateTime inicio,
                                                             @Param("fin") LocalDateTime fin,
                                                             @Param("sucursalId") Integer sucursalId);

    // ==========================================
    // QUERIES REPORTES RENDIMIENTO INVENTARIO
    // ==========================================

    @Query(value = "SELECT dv.producto_nombre as producto, " +
            "COALESCE(dv.tipo_venta, 'UNIDAD') as presentacion, " +
            "SUM(dv.cantidad) as unidades, SUM(dv.subtotal) as ventas " +
            "FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "AND (:filtrarIds = false OR dv.producto_id IN (:productIds)) " +
            "GROUP BY dv.producto_id, dv.producto_nombre, dv.tipo_venta " +
            "ORDER BY ventas DESC LIMIT 10", nativeQuery = true)
    List<java.util.Map<String, Object>> getTop10Productos(@Param("inicio") LocalDateTime inicio,
                                                         @Param("fin") LocalDateTime fin,
                                                         @Param("sucursalId") Integer sucursalId,
                                                         @Param("filtrarIds") boolean filtrarIds,
                                                         @Param("productIds") List<Integer> productIds);

    @Query(value = "SELECT dv.producto_nombre as producto, " +
            "SUM(dv.cantidad) as unidades, SUM(dv.subtotal) as ventas " +
            "FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY dv.producto_id, dv.producto_nombre " +
            "ORDER BY unidades ASC LIMIT 15", nativeQuery = true)
    List<java.util.Map<String, Object>> getProductosBajaRotacion(@Param("inicio") LocalDateTime inicio,
                                                                @Param("fin") LocalDateTime fin,
                                                                @Param("sucursalId") Integer sucursalId);

    @Query(value = "SELECT dv.producto_nombre as producto, " +
            "SUM(dv.cantidad) as unidades, SUM(dv.subtotal) as ventas " +
            "FROM detalle_ventas dv " +
            "INNER JOIN ventas v ON dv.venta_id = v.id " +
            "WHERE v.fecha_venta BETWEEN :inicio AND :fin " +
            "AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId) " +
            "AND v.estado NOT IN ('ANULADA', 'DEVUELTA') " +
            "GROUP BY dv.producto_id, dv.producto_nombre " +
            "ORDER BY producto ASC", nativeQuery = true)
    List<java.util.Map<String, Object>> getComparativoProducto(@Param("inicio") LocalDateTime inicio,
                                                              @Param("fin") LocalDateTime fin,
                                                              @Param("sucursalId") Integer sucursalId);
}
