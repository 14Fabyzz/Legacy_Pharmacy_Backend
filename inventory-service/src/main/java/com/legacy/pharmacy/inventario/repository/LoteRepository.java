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

        // ─────────────────────────────────────────────────────────────────────────────
        // QUERIES DE SEMAFORIZACIÓN FARMACÉUTICA (Estándar Legal Colombia)
        // ROJO : vencidos o vencen en <= 90 días → fecha <= limiteRojo
        // AMARILLO: vencen entre 91 y 180 días → fecha > limiteRojo AND fecha <=
        // limiteAmarillo
        // VERDE : vencen en > 180 días → solo COUNT (optimización de memoria)
        //
        // Todas usan Interface Projection (LoteAlertaDTO) con JOIN explícito para
        // traer únicamente los 6 campos necesarios en un único round-trip,
        // eliminando over-fetching y el lazy N+1 sobre la relación Lote → Producto.
        // ─────────────────────────────────────────────────────────────────────────────

        /**
         * SEMÁFORO ROJO — Lotes vencidos o que vencen en <= 90 días, con stock > 0.
         * Cubre tanto lotes ya vencidos (fecha < hoy) como los próximos a vencer
         * dentro de la ventana crítica (hoy <= fecha <= limiteRojo = hoy + 90 días).
         *
         * @param hoy        Fecha actual (LocalDate.now()). Parámetro de documentación;
         *                   la condición usa únicamente limiteRojo.
         * @param limiteRojo hoy.plusDays(90)
         */
        @Query("SELECT l.id AS id, p.nombreComercial AS nombreProducto, " +
                        "l.numeroLote AS lote, l.fechaVencimiento AS fecha, " +
                        "l.cantidadActual AS cantidad, p.imagenUrl AS imagenUrl " +
                        "FROM Lote l JOIN l.producto p " +
                        "WHERE l.fechaVencimiento <= :limiteRojo " +
                        "AND l.cantidadActual > 0 " +
                        "AND l.estado != 'DADO_DE_BAJA' " +
                        "ORDER BY l.fechaVencimiento ASC")
        List<LoteAlertaDTO> findLotesSemaforoRojo(@Param("hoy") LocalDate hoy,
                        @Param("limiteRojo") LocalDate limiteRojo);

        /**
         * SEMÁFORO AMARILLO — Lotes que vencen entre 91 y 180 días, con stock > 0.
         *
         * @param limiteRojo     hoy.plusDays(90) (exclusivo — inicio de la ventana
         *                       amarilla)
         * @param limiteAmarillo hoy.plusDays(180) (inclusivo — fin de la ventana
         *                       amarilla)
         */
        @Query("SELECT l.id AS id, p.nombreComercial AS nombreProducto, " +
                        "l.numeroLote AS lote, l.fechaVencimiento AS fecha, " +
                        "l.cantidadActual AS cantidad, p.imagenUrl AS imagenUrl " +
                        "FROM Lote l JOIN l.producto p " +
                        "WHERE l.fechaVencimiento > :limiteRojo " +
                        "AND l.fechaVencimiento <= :limiteAmarillo " +
                        "AND l.cantidadActual > 0 " +
                        "AND l.estado != 'DADO_DE_BAJA' " +
                        "ORDER BY l.fechaVencimiento ASC")
        List<LoteAlertaDTO> findLotesSemaforoAmarillo(@Param("limiteRojo") LocalDate limiteRojo,
                        @Param("limiteAmarillo") LocalDate limiteAmarillo);

        /**
         * SEMÁFORO VERDE — COUNT de lotes que vencen en > 180 días, con stock > 0.
         * Se expone solo el conteo para la tarjeta resumida del Dashboard.
         * Evita cargar a memoria cientos de lotes saludables que no se muestran en
         * detalle.
         *
         * @param limiteAmarillo hoy.plusDays(180)
         */
        @Query("SELECT COUNT(l) FROM Lote l " +
                        "WHERE l.fechaVencimiento > :limiteAmarillo " +
                        "AND l.cantidadActual > 0 " +
                        "AND l.estado != 'DADO_DE_BAJA'")
        long countLotesSemaforoVerde(@Param("limiteAmarillo") LocalDate limiteAmarillo);

}