package com.legacy.pharmacy.reportes.repository;

import com.legacy.pharmacy.reportes.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de consultas de ventas para reportes (solo lectura).
 * Usa queries nativas PostgreSQL para agregaciones con DATE_TRUNC.
 */
@Repository
public interface VentaReporteRepository extends JpaRepository<Venta, Long> {

  // ==========================================
  // Totales consolidados (con o sin sucursal)
  // ==========================================

  @Query(value = """
          SELECT
              COALESCE(SUM(v.total), 0) AS totalIngresos,
              COALESCE(SUM(v.total_iva), 0) AS totalIva,
              COUNT(v.id) AS cantidadVentas
          FROM ventas v
          WHERE v.estado = 'COMPLETADA'
            AND v.fecha_venta >= :fechaInicio
            AND v.fecha_venta < :fechaFin
            AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId)
      """, nativeQuery = true)
  List<Object[]> obtenerTotalesConsolidados(
      @Param("fechaInicio") LocalDateTime fechaInicio,
      @Param("fechaFin") LocalDateTime fechaFin,
      @Param("sucursalId") Integer sucursalId);

  // ==========================================
  // Agrupado por DÍA
  // ==========================================

  @Query(value = """
          SELECT
              TO_CHAR(DATE_TRUNC('day', v.fecha_venta), 'YYYY-MM-DD') AS periodo,
              COALESCE(SUM(v.total), 0) AS totalIngresos,
              COALESCE(SUM(v.total_iva), 0) AS totalIva,
              COUNT(v.id) AS cantidadVentas
          FROM ventas v
          WHERE v.estado = 'COMPLETADA'
            AND v.fecha_venta >= :fechaInicio
            AND v.fecha_venta < :fechaFin
            AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId)
          GROUP BY DATE_TRUNC('day', v.fecha_venta)
          ORDER BY DATE_TRUNC('day', v.fecha_venta)
      """, nativeQuery = true)
  List<Object[]> obtenerAgrupadoPorDia(
      @Param("fechaInicio") LocalDateTime fechaInicio,
      @Param("fechaFin") LocalDateTime fechaFin,
      @Param("sucursalId") Integer sucursalId);

  // ==========================================
  // Agrupado por SEMANA
  // ==========================================

  @Query(value = """
          SELECT
              TO_CHAR(DATE_TRUNC('week', v.fecha_venta), 'IYYY-\"W\"IW') AS periodo,
              COALESCE(SUM(v.total), 0) AS totalIngresos,
              COALESCE(SUM(v.total_iva), 0) AS totalIva,
              COUNT(v.id) AS cantidadVentas
          FROM ventas v
          WHERE v.estado = 'COMPLETADA'
            AND v.fecha_venta >= :fechaInicio
            AND v.fecha_venta < :fechaFin
            AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId)
          GROUP BY DATE_TRUNC('week', v.fecha_venta)
          ORDER BY DATE_TRUNC('week', v.fecha_venta)
      """, nativeQuery = true)
  List<Object[]> obtenerAgrupadoPorSemana(
      @Param("fechaInicio") LocalDateTime fechaInicio,
      @Param("fechaFin") LocalDateTime fechaFin,
      @Param("sucursalId") Integer sucursalId);

  // ==========================================
  // Agrupado por MES
  // ==========================================

  @Query(value = """
          SELECT
              TO_CHAR(DATE_TRUNC('month', v.fecha_venta), 'YYYY-MM') AS periodo,
              COALESCE(SUM(v.total), 0) AS totalIngresos,
              COALESCE(SUM(v.total_iva), 0) AS totalIva,
              COUNT(v.id) AS cantidadVentas
          FROM ventas v
          WHERE v.estado = 'COMPLETADA'
            AND v.fecha_venta >= :fechaInicio
            AND v.fecha_venta < :fechaFin
            AND (:sucursalId IS NULL OR v.sucursal_id = :sucursalId)
          GROUP BY DATE_TRUNC('month', v.fecha_venta)
          ORDER BY DATE_TRUNC('month', v.fecha_venta)
      """, nativeQuery = true)
  List<Object[]> obtenerAgrupadoPorMes(
      @Param("fechaInicio") LocalDateTime fechaInicio,
      @Param("fechaFin") LocalDateTime fechaFin,
      @Param("sucursalId") Integer sucursalId);

  // ==========================================
  // Top Productos (Mayor Rotación)
  // ==========================================

  @Query(value = """
          SELECT
              dv.producto_nombre as nombreProducto,
              dv.tipo_venta as presentacion,
              SUM(dv.cantidad) as totalVendido,
              SUM(dv.subtotal) as ingresoGenerado
          FROM detalle_ventas dv
          JOIN ventas v ON v.id = dv.venta_id
          WHERE v.estado = 'COMPLETADA'
            AND v.fecha_venta >= :fechaInicio
            AND v.fecha_venta <= :fechaFin
          GROUP BY dv.producto_id, dv.producto_nombre, dv.tipo_venta
          ORDER BY totalVendido DESC
          LIMIT :limite
      """, nativeQuery = true)
  List<Object[]> obtenerTopProductosMayorRotacion(
      @Param("fechaInicio") LocalDateTime fechaInicio,
      @Param("fechaFin") LocalDateTime fechaFin,
      @Param("limite") int limite);
}
