package com.legacy.pharmacy.inventario.repository;

import com.legacy.pharmacy.inventario.dto.LoteAlertaDTO;
import com.legacy.pharmacy.inventario.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {

        /// Método para buscar lotes (útil para consultas normales)
        List<Lote> findByProductoIdAndCantidadActualGreaterThanOrderByFechaVencimientoAsc(Integer productoId,
                        Integer cantidadMinima);

        // --- EL MÉTODO DE ENTRADA CORREGIDO ---
        @org.springframework.data.jpa.repository.query.Procedure(procedureName = "registrar_entrada_mercancia")
        void registrarEntrada(
                        @Param("p_producto_id") Integer productoId,
                        @Param("p_numero_lote") String numeroLote,
                        @Param("p_cantidad") Integer cantidad,
                        @Param("p_costo_compra") BigDecimal costoCompra,
                        @Param("p_fecha_vencimiento") LocalDate fechaVencimiento,
                        @Param("p_usuario") String usuario,
                        @Param("p_sucursal_id") Integer sucursalId,
                        @Param("p_observaciones") String observaciones);

        // --- NUEVO MÉTODO PARA SALIDAS ---
        @Query(value = "CALL registrar_salida_mercancia(:p_producto_id, :p_cantidad, :p_usuario, :p_sucursal_id, :p_venta_id, :p_observaciones, :p_es_venta_por_caja)", nativeQuery = true)
        List<Map<String, Object>> registrarSalida(
                        @Param("p_producto_id") Integer productoId,
                        @Param("p_cantidad") Integer cantidad,
                        @Param("p_usuario") String usuario,
                        @Param("p_sucursal_id") Integer sucursalId,
                        @Param("p_venta_id") Integer ventaId,
                        @Param("p_observaciones") String observaciones,
                        @Param("p_es_venta_por_caja") Boolean esVentaPorCaja);

        // Buscar lotes de un producto específico
        List<Lote> findByProductoId(Integer productoId);

        // Buscar lotes vencidos (fecha menor a hoy) y que tengan saldo (> 0)
        List<Lote> findByFechaVencimientoBeforeAndCantidadActualGreaterThan(LocalDate fecha, Integer cantidad);

        // Buscar próximos a vencer (Entre hoy y X días) con saldo
        List<Lote> findByFechaVencimientoBetweenAndCantidadActualGreaterThan(LocalDate inicio, LocalDate fin,
                        Integer cantidad);

        // ─────────────────────────────────────────────────────────────────────────
        // QUERIES OPTIMIZADAS PARA DASHBOARD — usan JOIN para traer solo los campos
        // necesarios en un único round-trip (eliminan over-fetching + lazy N+1)
        // ─────────────────────────────────────────────────────────────────────────

        /**
         * Lotes VENCIDOS con stock > 0.
         * Usa Interface Projection para proyectar solo 6 campos sin cargar entidades
         * completas ni disparar lazy-loads adicionales sobre Producto.
         */
        @Query("SELECT l.id AS id, p.nombreComercial AS nombreProducto, " +
                        "l.numeroLote AS lote, l.fechaVencimiento AS fecha, " +
                        "l.cantidadActual AS cantidad, p.imagenUrl AS imagenUrl " +
                        "FROM Lote l JOIN l.producto p " +
                        "WHERE l.fechaVencimiento < :hoy AND l.cantidadActual > 0")
        List<LoteAlertaDTO> findLotesVencidosParaDashboard(@Param("hoy") LocalDate hoy);

        /**
         * Lotes POR VENCER (ventana de 30 días) con stock > 0.
         * Mismo patrón de Projection que la query de vencidos.
         */
        @Query("SELECT l.id AS id, p.nombreComercial AS nombreProducto, " +
                        "l.numeroLote AS lote, l.fechaVencimiento AS fecha, " +
                        "l.cantidadActual AS cantidad, p.imagenUrl AS imagenUrl " +
                        "FROM Lote l JOIN l.producto p " +
                        "WHERE l.fechaVencimiento BETWEEN :inicio AND :fin AND l.cantidadActual > 0")
        List<LoteAlertaDTO> findLotesPorVencerParaDashboard(@Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin);

}