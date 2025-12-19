package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.config.UserContext;                      // ← NUEVO
import com.legacy.pharmacy.inventario.dto.EntradaMercanciaDTO;
import com.legacy.pharmacy.inventario.dto.StockDTO;                            // ← NUEVO
import com.legacy.pharmacy.inventario.entity.Producto;                         // ← NUEVO
import com.legacy.pharmacy.inventario.repository.LoteRepository;
import com.legacy.pharmacy.inventario.repository.ProductoRepository;           // ← NUEVO
import lombok.extern.slf4j.Slf4j;                                              // ← NUEVO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;                             // ← NUEVO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j                                                                         // ← NUEVO
@Service
public class InventarioService {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ProductoRepository productoRepository;                             // ← NUEVO

    @Autowired
    private JdbcTemplate jdbcTemplate;                                         // ← NUEVO

    @Transactional
    public Map<String, Object> registrarEntrada(EntradaMercanciaDTO entrada) {
        // Llamamos al repositorio y retornamos lo que responda la base de datos
        return loteRepository.registrarEntrada(
                entrada.getProductoId(),
                entrada.getNumeroLote(),
                entrada.getCantidad(),
                entrada.getCostoCompra(),
                entrada.getFechaVencimiento(),
                entrada.getUsuarioResponsable() != null ? entrada.getUsuarioResponsable() : "SISTEMA",
                entrada.getSucursalId(),
                entrada.getObservaciones()
        );
    }

    // --- MÉTODO DE SALIDA ---
    @Transactional
    public List<Map<String, Object>> registrarSalida(com.legacy.pharmacy.inventario.dto.SalidaMercanciaDTO salida) {
        // Llamamos al SP. Este nos devolverá la lista de lotes afectados.
        return loteRepository.registrarSalida(
                salida.getProductoId(),
                salida.getCantidad(),
                "VENDEDOR_APP", // Aquí podrías poner el usuario logueado
                salida.getSucursalId(),
                salida.getVentaId(),
                salida.getObservaciones()
        );
    }

    // =========================================================================
    // =========================================================================
    // TODO LO DE ABAJO ES NUEVO - PARA INTEGRACIÓN CON MS-VENTAS
    // =========================================================================
    // =========================================================================

    /**
     * Consultar stock disponible de un producto
     * Este método será llamado por MS-Ventas antes de crear una venta
     */
    public StockDTO consultarStock(Integer productoId) {
        log.info("Consultando stock del producto {} - Usuario: {}",
                productoId, UserContext.getUsername());

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        // ⭐ CORREGIDO: cantidad_actual en lugar de cantidad
        Integer disponible = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(cantidad_actual), 0) " +
                        "FROM lotes " +
                        "WHERE producto_id = ? " +
                        "AND cantidad_actual > 0 " +
                        "AND fecha_vencimiento > CURDATE()",
                Integer.class,
                productoId
        );

        // Determinar estado del stock
        String estado;
        if (disponible == null || disponible == 0) {
            estado = "SIN_STOCK";
        } else if (disponible <= producto.getStockMinimo()) {
            estado = "STOCK_BAJO";
        } else {
            estado = "STOCK_OK";
        }

        // Crear y llenar el DTO de respuesta
        StockDTO stock = new StockDTO();
        stock.setProductoId(producto.getId());
        stock.setNombreProducto(producto.getNombreComercial());
        stock.setCantidadDisponible(disponible != null ? disponible : 0);
        stock.setCantidadMinima(producto.getStockMinimo());
        stock.setEstado(estado);
        stock.setDisponibleParaVenta(
                disponible != null && disponible > 0 && "ACTIVO".equals(producto.getEstado())
        );

        log.debug("Stock consultado: disponible={}, estado={}", disponible, estado);

        return stock;
    }

    /**
     * Descontar inventario después de una venta
     * Este método será llamado por MS-Ventas después de crear una venta exitosa
     *
     * Usa tu procedimiento almacenado existente: sp_registrar_salida
     */
    @Transactional
    public void descontarInventario(Integer productoId, Integer cantidad, String motivo) {
        log.info("Descontando {} unidades del producto {} - Motivo: {} - Usuario: {}",
                cantidad, productoId, motivo, UserContext.getUsername());

        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();

        if (userId == null) {
            throw new RuntimeException("No se puede descontar inventario: usuario no identificado");
        }

        // Verificar que el producto existe
        productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        try {
            // Llamar a tu procedimiento almacenado existente: sp_registrar_salida
            // Ajusta los parámetros según tu SP (estos son los que veo en tu código)
            loteRepository.registrarSalida(
                    productoId,
                    cantidad,
                    username != null ? username : "SISTEMA",
                    1, // sucursal_id - ajusta según necesites
                    null, // venta_id - NULL porque MS-Ventas tiene su propio ID
                    motivo
            );

            log.info("Inventario descontado exitosamente: producto={}, cantidad={}",
                    productoId, cantidad);

        } catch (Exception e) {
            log.error("Error al descontar inventario: {}", e.getMessage());
            throw new RuntimeException("Error al descontar inventario: " + e.getMessage(), e);
        }
    }

    /**
     * Devolver inventario cuando se anula una venta
     * Este método será llamado por MS-Ventas cuando se anule una venta
     *
     * Usa tu procedimiento almacenado existente: sp_registrar_entrada
     */
    /**
     * Devolver inventario usando un ajuste directo
     * No usa procedimientos almacenados para evitar duplicación
     */
    @Transactional
    public void devolverInventario(Integer productoId, Integer cantidad, String motivo) {
        log.info("Devolviendo {} unidades del producto {} - Motivo: {} - Usuario: {}",
                cantidad, productoId, motivo, UserContext.getUsername());

        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();

        if (userId == null) {
            throw new RuntimeException("No se puede devolver inventario: usuario no identificado");
        }

        // Verificar que el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        try {
            // Obtener el lote más reciente del producto
            Integer loteId = jdbcTemplate.queryForObject(
                    "SELECT id FROM lotes " +
                            "WHERE producto_id = ? " +
                            "ORDER BY created_at DESC " +
                            "LIMIT 1",
                    Integer.class,
                    productoId
            );

            if (loteId == null) {
                throw new RuntimeException("No existe un lote para devolver inventario");
            }

            // Insertar movimiento de DEVOLUCION (cantidad positiva)
            jdbcTemplate.update(
                    "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, observaciones) " +
                            "VALUES (?, 'DEVOLUCION', ?, ?, 1, ?)",
                    loteId,
                    cantidad, // Cantidad positiva
                    username != null ? username : "SISTEMA",
                    motivo
            );

            // El trigger se encargará de actualizar la cantidad_actual

            log.info("Inventario devuelto exitosamente: producto={}, cantidad={}, lote={}",
                    productoId, cantidad, loteId);

        } catch (Exception e) {
            log.error("Error al devolver inventario: {}", e.getMessage());
            throw new RuntimeException("Error al devolver inventario: " + e.getMessage(), e);
        }
    }
}