package com.legacy.pharmacy.reportes.repository;

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
}
