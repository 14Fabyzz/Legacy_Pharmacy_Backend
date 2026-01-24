package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.config.UserContext;                      // ← NUEVO
import com.legacy.pharmacy.inventario.dto.DashboardAlertasDTO;
import com.legacy.pharmacy.inventario.dto.EntradaMercanciaDTO;
import com.legacy.pharmacy.inventario.dto.StockDTO;                            // ← NUEVO
import com.legacy.pharmacy.inventario.entity.Lote;
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

    // AGREGA ESTO A InventarioService.java

    @Transactional
    public Map<String, Object> registrarEntradaMasiva(List<EntradaMercanciaDTO> entradas) {
        int procesados = 0;

        for (EntradaMercanciaDTO dto : entradas) {
            // Reutilizamos la lógica que ya tienes para registrar uno solo
            registrarEntrada(dto);
            procesados++;
        }

        return Map.of(
                "mensaje", "Se han procesado correctamente los lotes.",
                "cantidadProcesada", procesados
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

    // 1. Buscas el producto
    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

    // 2. Calculas el disponible
    Integer disponible = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(cantidad_actual), 0) " +
                    "FROM lotes " +
                    "WHERE producto_id = ? " +
                    "AND cantidad_actual > 0 " +
                    "AND fecha_vencimiento > CURDATE()",
            Integer.class,
            productoId
    );

    // 3. Determinar estado del stock (Esta parte faltaba en tu resumen)
    String estado;
    if (disponible == null || disponible == 0) {
        estado = "SIN_STOCK";
    } else if (disponible <= producto.getStockMinimo()) {
        estado = "STOCK_BAJO";
    } else {
        estado = "STOCK_OK";
    }

    // 4. Crear y llenar el DTO de respuesta
    StockDTO stock = new StockDTO();
    stock.setProductoId(producto.getId());
    stock.setNombreProducto(producto.getNombreComercial());

    // --- NUEVO: Agregamos el precio para ventas ---
    stock.setPrecioVenta(producto.getPrecioVentaBase());
    // ---------------------------------------------

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

    // ==========================================
    // LÓGICA EXCLUSIVA PARA INTEGRACIÓN CON VENTAS
    // ==========================================

    // 1. Consultar Stock (Suma la cantidad actual de todos los lotes válidos)
    public Integer consultarStockActual(Integer productoId) {
        String sql = "SELECT COALESCE(SUM(cantidad_actual), 0) FROM lotes " +
                "WHERE producto_id = ? AND cantidad_actual > 0 AND fecha_vencimiento > CURRENT_DATE";

        return jdbcTemplate.queryForObject(sql, Integer.class, productoId);
    }

    // 2. Descontar Inventario (Lógica FIFO/FEFO automática)
    @Transactional
    public void descontarInventarioVenta(Integer productoId, Integer cantidad) {
        // Verificar stock primero
        Integer stock = consultarStockActual(productoId);
        if (stock < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + stock);
        }

        // Llamamos a tu SP existente o lógica de descuento
        // Asumiendo que usas el repositorio de Lotes que ya tenías:
        loteRepository.registrarSalida(
                productoId,
                cantidad,
                "MS-VENTAS", // Usuario responsable
                1, // Sucursal Default
                null,
                "VENTA_EXTERNA"
        );
    }

    // 3. Reponer Inventario (Devolución)
    @Transactional
    public void reponerInventarioDevolucion(Integer productoId, Integer cantidad) {
        // Buscamos el último lote activo para sumarle ahí (simplificado)
        // O insertamos un movimiento de entrada
        String sqlLote = "SELECT id FROM lotes WHERE producto_id = ? ORDER BY fecha_vencimiento DESC LIMIT 1";
        try {
            Integer loteId = jdbcTemplate.queryForObject(sqlLote, Integer.class, productoId);

            // Insertamos el movimiento de retorno
            String sqlInsert = "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, observaciones) " +
                    "VALUES (?, 'DEVOLUCION', ?, 'MS-VENTAS', 1, 'Devolución de cliente')";

            jdbcTemplate.update(sqlInsert, loteId, cantidad);

            // NOTA: Asegúrate de que tu base de datos tenga un Trigger que actualice
            // la tabla 'lotes' cuando se inserta en 'movimientos'.
            // Si no tienes trigger, debes hacer el update manual aquí:
            // jdbcTemplate.update("UPDATE lotes SET cantidad_actual = cantidad_actual + ? WHERE id = ?", cantidad, loteId);

        } catch (Exception e) {
            throw new RuntimeException("No se encontró lote para procesar la devolución");
        }
    }

    @Transactional(readOnly = true) // <--- ESTO ES VITAL PARA QUE NO FALLE
    public DashboardAlertasDTO obtenerDashboardAlertas() {
        LocalDate hoy = LocalDate.now();

        // 1. OBTENER VENCIDOS
        List<Lote> lotesVencidos = loteRepository.findByFechaVencimientoBeforeAndCantidadActualGreaterThan(hoy, 0);

        List<Map<String, Object>> listaVencidos = lotesVencidos.stream()
                .map(l -> Map.<String, Object>of(
                        "id", l.getId(),
                        "producto", l.getProducto().getNombreComercial(), // Requiere transacción activa
                        "lote", l.getNumeroLote(),
                        "fecha", l.getFechaVencimiento(),
                        "cantidad", l.getCantidadActual()
                ))
                .collect(java.util.stream.Collectors.toList()); // <--- CORRECCIÓN DE TIPO

        // 2. OBTENER POR VENCER
        List<Lote> lotesPorVencer = loteRepository.findByFechaVencimientoBetweenAndCantidadActualGreaterThan(hoy, hoy.plusDays(30), 0);

        List<Map<String, Object>> listaPorVencer = lotesPorVencer.stream()
                .map(l -> Map.<String, Object>of(
                        "id", l.getId(),
                        "producto", l.getProducto().getNombreComercial(),
                        "lote", l.getNumeroLote(),
                        "fecha", l.getFechaVencimiento(),
                        "cantidad", l.getCantidadActual(),
                        "diasRestantes", java.time.temporal.ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento())
                ))
                .collect(java.util.stream.Collectors.toList()); // <--- CORRECCIÓN DE TIPO

        // 3. OBTENER STOCK BAJO
        List<Producto> productosBajoStock = productoRepository.findProductosBajoStock();

        List<Map<String, Object>> listaStockBajo = productosBajoStock.stream()
                .map(p -> {
                    Integer stockReal = consultarStockActual(p.getId());
                    return Map.<String, Object>of(
                            "id", p.getId(),
                            "nombre", p.getNombreComercial(),
                            "stockActual", stockReal,
                            "stockMinimo", p.getStockMinimo()
                    );
                })
                .collect(java.util.stream.Collectors.toList()); // <--- CORRECCIÓN DE TIPO

        // 4. CALCULAR SALUDABLES
        long totalProductos = productoRepository.count();
        long totalSaludables = Math.max(0, totalProductos - listaStockBajo.size());

        // 5. CONSTRUIR RESPUESTA
        return DashboardAlertasDTO.builder()
                .totalVencidos(listaVencidos.size())
                .totalPorVencer(listaPorVencer.size())
                .totalStockBajo(listaStockBajo.size())
                .totalSaludables(totalSaludables)
                .listaVencidos(listaVencidos)
                .listaPorVencer(listaPorVencer)
                .listaStockBajo(listaStockBajo)
                .build();
    }


}