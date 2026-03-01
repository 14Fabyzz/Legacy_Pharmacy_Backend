package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.config.UserContext; // ← NUEVO
import com.legacy.pharmacy.inventario.dto.DashboardAlertasDTO;
import com.legacy.pharmacy.inventario.dto.EntradaMercanciaDTO;
import com.legacy.pharmacy.inventario.dto.StockDTO; // ← NUEVO
import com.legacy.pharmacy.inventario.entity.Lote;
import com.legacy.pharmacy.inventario.entity.Producto; // ← NUEVO
import com.legacy.pharmacy.inventario.repository.LoteRepository;
import com.legacy.pharmacy.inventario.repository.ProductoRepository; // ← NUEVO
import lombok.extern.slf4j.Slf4j; // ← NUEVO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate; // ← NUEVO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

@Slf4j // ← NUEVO
@Service
public class InventarioService {

        @Autowired
        private LoteRepository loteRepository;

        @Autowired
        private ProductoRepository productoRepository; // ← NUEVO

        @Autowired
        private JdbcTemplate jdbcTemplate; // ← NUEVO

        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public Map<String, Object> registrarEntrada(EntradaMercanciaDTO entrada) {
                // 1. Validar producto y obtener datos de conversión
                Producto producto = productoRepository.findById(entrada.getProductoId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Producto no encontrado: " + entrada.getProductoId()));

                log.info("DEBUG_STOCK: ProductoId={}, UnidadesPorCaja={}, EsFraccionable={}, CantidadEntrada={} cajas",
                                producto.getId(), producto.getUnidadesPorCaja(), producto.getEsFraccionable(),
                                entrada.getCantidad());

                // ── CONVERSIÓN: Cajas → Unidades (para el stock del lote) ──────────────
                // entrada.getCantidad() siempre representa CAJAS.
                // cantidadReal es la cantidad en unidades mínimas (pastillas) que entra al
                // stock.
                int cajasSolicitadas = entrada.getCantidad();
                int cantidadReal = cajasSolicitadas;
                if (Boolean.TRUE.equals(producto.getEsFraccionable()) && producto.getUnidadesPorCaja() != null
                                && producto.getUnidadesPorCaja() > 1) {
                        cantidadReal = cajasSolicitadas * producto.getUnidadesPorCaja();
                }
                log.info("DEBUG_STOCK: {} cajas → {} unidades en stock", cajasSolicitadas, cantidadReal);

                // ── COSTO POR CAJA (para el cálculo de precios de venta) ─────────────
                // costoCompra del DTO = costo TOTAL del pedido (suma de todas las cajas).
                // Dividimos entre las CAJAS para obtener el costo de UNA CAJA.
                // recalcularPrecios() usa este valor (costo/caja) para calcular:
                // precioVentaBase = costoCaja × margen (PVP caja sin IVA)
                // precioVentaTotal = precioVentaBase × IVA (PVP caja con IVA)
                // precioVentaUnidad = precioVentaTotal / uds/caja (PVP pastilla)
                BigDecimal costoPorCaja = BigDecimal.ZERO;
                if (cajasSolicitadas > 0 && entrada.getCostoCompra() != null) {
                        costoPorCaja = entrada.getCostoCompra()
                                        .divide(BigDecimal.valueOf(cajasSolicitadas), 4, RoundingMode.HALF_UP);
                }
                log.info("PRECIO: costoTotal={}, cajas={}, costoPorCaja={}",
                                entrada.getCostoCompra(), cajasSolicitadas, costoPorCaja);

                // ── ACTUALIZAR PRECIO DE REFERENCIA Y RECALCULAR PRECIOS ─────────────
                if (costoPorCaja.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal costoAnterior = producto.getPrecioCompraReferencia();
                        if (costoAnterior == null || costoAnterior.compareTo(costoPorCaja) != 0) {
                                log.info("PRECIO: Actualizando costo de referencia. Anterior={}, Nuevo={}",
                                                costoAnterior, costoPorCaja);
                                producto.setPrecioCompraReferencia(costoPorCaja);
                                producto.recalcularPrecios();
                                productoRepository.save(producto);
                                log.info("PRECIO: Precios recalculados → Base(caja)={}, Total(caja)={}, Unidad={}, Blister={}",
                                                producto.getPrecioVentaBase(),
                                                producto.getPrecioVentaTotal(),
                                                producto.getPrecioVentaUnidad(),
                                                producto.getPrecioVentaBlister());
                        } else {
                                log.info("PRECIO: Sin cambio en costo (costoPorCaja={}). No se recalcularán precios.",
                                                costoPorCaja);
                        }
                }

                String sqlInsertLote = "INSERT INTO lotes (producto_id, numero_lote, fecha_vencimiento, cantidad_actual, costo_compra, sucursal_id) VALUES (?, ?, ?, ?, ?, ?)";

                KeyHolder keyHolder = new GeneratedKeyHolder();

                int finalCantidadReal = cantidadReal; // Para lambda
                BigDecimal finalCostoPorCaja = costoPorCaja; // Para lambda

                log.info("INSERTAR LOTE: cantidad={} unidades, costoReferenciaCaja={}", finalCantidadReal,
                                finalCostoPorCaja);

                jdbcTemplate.update(connection -> {
                        java.sql.PreparedStatement ps = connection.prepareStatement(sqlInsertLote,
                                        java.sql.Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, entrada.getProductoId());
                        ps.setString(2, entrada.getNumeroLote());
                        ps.setObject(3, entrada.getFechaVencimiento());
                        ps.setInt(4, 0); // ← Se inicia en 0, el trigger lo actualizará al insertar movimiento
                        ps.setBigDecimal(5, finalCostoPorCaja); // ← costo_compra por CAJA (referencia)
                        ps.setObject(6, entrada.getSucursalId()); // ← setObject para manejar null
                        return ps;
                }, keyHolder);

                Number loteId = keyHolder.getKey();

                // Insertar Movimiento
                // TAREA 3: Snapshot Saldo Historico (Entrada)
                // En una entrada nueva (o sobre lote existente), el saldo DESPUES del
                // movimiento es cantidadReal (si es nuevo)
                // o cantidadActual + cantidadReal.
                // Dado que acabamos de insertar el LOTE con cantidad 0 y el trigger
                // actualizará,
                // la "foto" lógica es que este lote nace con 'cantidadReal'.
                int saldoFoto = cantidadReal;

                String sqlMov = "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(sqlMov,
                                loteId,
                                "ENTRADA",
                                cantidadReal,
                                saldoFoto, // saldo_historico
                                entrada.getUsuarioResponsable() != null ? entrada.getUsuarioResponsable() : "SISTEMA",
                                entrada.getSucursalId(),
                                entrada.getObservaciones());

                return java.util.Map.of("estado", "OK", "mensaje", "Entrada registrada (Direct JDBC)");
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
                                "cantidadProcesada", procesados);
        }

        // --- MÉTODO DE SALIDA ---
        // --- MÉTODO DE SALIDA ---
        @Transactional
        public List<Map<String, Object>> registrarSalida(com.legacy.pharmacy.inventario.dto.SalidaMercanciaDTO salida) {
                // 1. Obtener producto para factores de conversión
                Producto producto = productoRepository.findById(salida.getProductoId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Producto no encontrado: " + salida.getProductoId()));

                // 2. Determinar Tipo de Venta (Prioridad: Enum > Boolean > Default)
                com.legacy.pharmacy.inventario.enums.TipoVenta tipo = salida.getTipoVenta();
                if (tipo == null) {
                        @SuppressWarnings("deprecation") // Backwards compatibility until MS-ventas migration
                        Boolean esVentaPorCaja = salida.getEsVentaPorCaja();
                        if (Boolean.TRUE.equals(esVentaPorCaja)) {
                                tipo = com.legacy.pharmacy.inventario.enums.TipoVenta.CAJA;
                        } else {
                                tipo = com.legacy.pharmacy.inventario.enums.TipoVenta.UNIDAD;
                        }
                }

                // 3. Calcular cantidad total en UNIDADES (Pastillas)
                int cantidadTotalUnidades = salida.getCantidad();

                switch (tipo) {
                        case CAJA:
                                if (producto.getUnidadesPorCaja() != null && producto.getUnidadesPorCaja() > 0) {
                                        cantidadTotalUnidades = salida.getCantidad() * producto.getUnidadesPorCaja();
                                }
                                break;
                        case BLISTER:
                                if (producto.getUnidadesPorBlister() != null && producto.getUnidadesPorBlister() > 0) {
                                        cantidadTotalUnidades = salida.getCantidad() * producto.getUnidadesPorBlister();
                                } else {
                                        throw new RuntimeException(
                                                        "El producto no tiene configuradas unidades por blister.");
                                }
                                break;
                        case UNIDAD:
                        default:
                                // Ya está en unidades
                                break;
                }

                // 4. LÓGICA FEFO EN JAVA (Reemplaza al SP)
                com.legacy.pharmacy.inventario.entity.TipoMovimiento tipoMov = com.legacy.pharmacy.inventario.entity.TipoMovimiento.SALIDA; // O
                                                                                                                                            // VENTA
                                                                                                                                            // según
                                                                                                                                            // tu
                                                                                                                                            // enum

                // Buscamos lotes ordenados por vencimiento (FEFO)
                List<Lote> lotes = loteRepository
                                .findByProductoIdAndCantidadActualGreaterThanOrderByFechaVencimientoAsc(
                                                salida.getProductoId(), 0);

                int cantidadPendiente = cantidadTotalUnidades;
                List<Map<String, Object>> lotesAfectados = new java.util.ArrayList<>();

                for (Lote lote : lotes) {
                        if (cantidadPendiente <= 0)
                                break;

                        int cantidadADescontar = Math.min(lote.getCantidadActual(), cantidadPendiente);

                        // --- ACTUALIZACIÓN ATÓMICA (Concurrency Safe) ---
                        // "UPDATE lotes SET cantidad_actual = cantidad_actual - ? WHERE id = ? AND
                        // cantidad_actual >= ?"
                        String sqlUpdate = "UPDATE lotes SET cantidad_actual = cantidad_actual - ? WHERE id = ? AND cantidad_actual >= ?";
                        int filasAfectadas = jdbcTemplate.update(sqlUpdate, cantidadADescontar, lote.getId(),
                                        cantidadADescontar);

                        if (filasAfectadas > 0) {
                                // Éxito: Registro Movimiento
                                String sqlMov = "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";

                                // TAREA 3: Snapshot Saldo Historico (Salida)
                                // La cantidad actual del lote (antes del update) era
                                // 'lote.getCantidadActual()'.
                                // Hemos restado 'cantidadADescontar'.
                                // El saldo que queda es:
                                int saldoFoto = lote.getCantidadActual() - cantidadADescontar;

                                // Cantidad negativa para salidas en historial, o positiva si usas tipo 'SALIDA'
                                // Usaremos negativo para consistencia matemática si así lo prefieres,
                                // pero tu enum tiene TIPO. Usaré positivo con tipo SALIDA.
                                jdbcTemplate.update(sqlMov,
                                                lote.getId(),
                                                "SALIDA",
                                                cantidadADescontar, // Cantidad positiva en BD según tu código previo
                                                                    // (aunque el comentario decía negativo, el código
                                                                    // usaba positivo)
                                                saldoFoto, // saldo_historico
                                                salida.getUsuarioResponsable() != null ? salida.getUsuarioResponsable()
                                                                : "VENDEDOR",
                                                salida.getSucursalId(),
                                                salida.getObservaciones());

                                cantidadPendiente -= cantidadADescontar;

                                // Agregar a respuesta
                                lotesAfectados.add(java.util.Map.of(
                                                "lote_id", lote.getId(),
                                                "cantidad_descontada", cantidadADescontar,
                                                "nuevo_saldo", lote.getCantidadActual() - cantidadADescontar // Estimado
                                ));
                        } else {
                                // Falló la concurrencia (alguien ganó el stock), reintentamos loop (next lote)
                                log.warn("Concurrencia detectada en Lote ID {}. Reintentando con siguiente lote.",
                                                lote.getId());
                        }
                }

                if (cantidadPendiente > 0) {
                        throw new RuntimeException(
                                        "Stock insuficiente para completar la venta. Faltaron: " + cantidadPendiente);
                }

                return lotesAfectados;
        }

        // =========================================================================
        // =========================================================================
        // TODO LO DE ABAJO ES NUEVO - PARA INTEGRACIÓN CON MS-VENTAS
        // =========================================================================
        // =========================================================================

        /**
         * Consultar stock disponible de un producto
         * Este método será llamado por MS-Ventas antes de crear una venta
         * 
         * @param productoId ID del producto
         * @param sucursalId ID de la sucursal (opcional, para filtrar stock por
         *                   sucursal)
         */
        public StockDTO consultarStock(Integer productoId, Integer sucursalId) {
                log.info("Consultando stock del producto {} en sucursal {} - Usuario: {}",
                                productoId, sucursalId != null ? sucursalId : "TODAS", UserContext.getUsername());

                // 1. Buscas el producto
                Producto producto = productoRepository.findById(productoId)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                // 2. Calculas el disponible desde lotes
                String sql = "SELECT COALESCE(SUM(cantidad_actual), 0) " +
                                "FROM lotes " +
                                "WHERE producto_id = ? " +
                                "AND cantidad_actual > 0 " +
                                "AND fecha_vencimiento > CURDATE()";

                // DIAGNOSTICO: Imprimir todos los lotes
                List<Lote> allLotes = loteRepository
                                .findByProductoIdAndCantidadActualGreaterThanOrderByFechaVencimientoAsc(productoId, -1);
                for (Lote l : allLotes) {
                        log.info("LOTE_DUMP: ID={}, Cant={}, Venc={}, ProdId={}", l.getId(), l.getCantidadActual(),
                                        l.getFechaVencimiento(), l.getProducto().getId());
                }

                Integer disponible;

                // ✅ CAMBIO TAREA 355: STOCK GLOBAL
                // Ignoramos el sucursalId para que la venta encuentre stock de CUALQUIER
                // sucursal.
                // El log lo mantenemos para saber quién preguntó.
                if (sucursalId != null) {
                        log.info("Ignorando filtro sucursalId={} para usar Stock Global.", sucursalId);
                }

                disponible = jdbcTemplate.queryForObject(sql, Integer.class, productoId);
                log.debug("Query Stock Global ejecutada. Resultado: {}", disponible);

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
                stock.setTipo(producto.getTipo() != null ? producto.getTipo().name() : "TANGIBLE");

                // --- CONFIGURACIÓN DE PRECIOS Y FRACCIONAMIENTO ---
                // Estos datos permiten al MS-Ventas calcular correctamente el precio según
                // si la venta es por Caja o por Unidad
                stock.setPrecioVentaBase(producto.getPrecioVentaBase()); // Base sin impuestos
                stock.setPrecioVentaTotal(producto.getPrecioVentaTotal()); // Total con impuestos
                stock.setIvaPorcentaje(producto.getIvaPorcentaje());
                stock.setPrecioVentaUnidad(producto.getPrecioVentaUnidad());
                stock.setPrecioVentaBlister(producto.getPrecioVentaBlister());
                stock.setEsFraccionable(producto.getEsFraccionable());
                stock.setUnidadesPorCaja(producto.getUnidadesPorCaja());
                stock.setUnidadesPorBlister(producto.getUnidadesPorBlister()); // Informativo para UX
                stock.setEsControlado(producto.getEsControlado()); // Control legal
                // ----------------------------------------------

                stock.setCantidadDisponible(disponible != null ? disponible : 0);
                stock.setCantidadMinima(producto.getStockMinimo());
                stock.setEstado(estado);
                stock.setDisponibleParaVenta(
                                disponible != null && disponible > 0 && "ACTIVO".equals(producto.getEstado()));

                log.debug("Stock consultado: disponible={}, estado={}", disponible, estado);

                return stock;
        }

        /**
         * Descontar inventario después de una venta
         * Este método será llamado por MS-Ventas después de crear una venta exitosa
         *
         * Usa tu procedimiento almacenado existente: sp_registrar_salida
         */
        /**
         * Descontar inventario después de una venta
         * Ahora soporta venta por Caja o Unidad
         */
        /**
         * Descontar inventario después de una venta
         * Ahora soporta TipoVenta (CAJA, BLISTER, UNIDAD)
         */
        @Transactional
        public void descontarInventario(Integer productoId, Integer cantidad, String motivo,
                        com.legacy.pharmacy.inventario.enums.TipoVenta tipoVenta) {
                log.info("DIAGNOSTICO: Iniciando descuento. ProductoId={}, Cantidad={}, TipoVenta={}, Motivo={}",
                                productoId, cantidad, tipoVenta, motivo);

                Long userId = UserContext.getUserId();
                String username = UserContext.getUsername();

                if (userId == null) {
                        throw new RuntimeException("No se puede descontar inventario: usuario no identificado");
                }

                // Verificar que el producto existe
                Producto p = productoRepository.findById(productoId)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                // LÓGICA DELEGADA A JAVA PARA ELIMINAR PROCS (FEFO + ATOMICIDAD)
                // ---------------------------------------------------------------

                // 1. Calcular Total Unidades reales
                int cantidadReal = cantidad;

                // Conversión según TipoVenta
                if (tipoVenta != null) {
                        switch (tipoVenta) {
                                case CAJA:
                                        if (p.getUnidadesPorCaja() != null && p.getUnidadesPorCaja() > 0) {
                                                cantidadReal = cantidad * p.getUnidadesPorCaja();
                                        }
                                        break;
                                case BLISTER:
                                        if (p.getUnidadesPorBlister() != null && p.getUnidadesPorBlister() > 0) {
                                                cantidadReal = cantidad * p.getUnidadesPorBlister();
                                        } else {
                                                throw new RuntimeException(
                                                                "El producto no tiene configuración para venta por Blister.");
                                        }
                                        break;
                                case UNIDAD:
                                default:
                                        // Ya es unidad
                                        break;
                        }
                }

                log.info("Conversión de Stock: {} {} -> {} Unidades Totales", cantidad, tipoVenta, cantidadReal);

                // 2. Ejecutar descuento FEFO
                List<Lote> lotes = loteRepository
                                .findByProductoIdAndCantidadActualGreaterThanOrderByFechaVencimientoAsc(productoId, 0);

                int pendiente = cantidadReal;
                log.info("DIAGNOSTICO: Lotes encontrados par el producto {}: {}", productoId, lotes.size());
                if (lotes.isEmpty()) {
                        log.error("DIAGNOSTICO CRITICO: No se encontraron lotes con stock para el producto {}. El inventario no se descontará.",
                                        productoId);
                }

                for (Lote lote : lotes) {
                        if (pendiente <= 0)
                                break;

                        // Desacoplar para evitar conflictos con Hibernate (CRÍTICO)
                        entityManager.detach(lote);

                        int aDescontar = Math.min(lote.getCantidadActual(), pendiente);

                        log.info("Insertando movimiento de SALIDA para Lote ID {}. Cantidad enviada a BD: {}",
                                        lote.getId(), -aDescontar);

                        try {
                                // 1. NO HACEMOS UPDATE MANUAL: Eliminamos el jdbcUpdate a la tabla 'lotes'.
                                // 2. DELEGAMOS AL TRIGGER: Insertamos en 'movimientos' con número NEGATIVO
                                // (-aDescontar).
                                // 3. El trigger 'trg_validar_cantidad_disponible' validará la operación.
                                // 4. El trigger 'trg_actualizar_cantidad_lote' hará la resta matemática
                                // automáticamente.

                                // TAREA 3: Snapshot Saldo Historico (Salida FEFO Delegada)
                                int saldoFoto = lote.getCantidadActual() - aDescontar;

                                jdbcTemplate.update(
                                                "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                                lote.getId(), "SALIDA", -aDescontar, saldoFoto, username, 1, motivo);

                                pendiente -= aDescontar;

                        } catch (Exception e) {
                                // Si el trigger aborta la operación por falta de stock, caerá aquí.
                                log.warn("Concurrencia o rechazo de BD en Lote ID {}: {}", lote.getId(),
                                                e.getMessage());
                        }
                }

                if (pendiente > 0) {
                        throw new RuntimeException("Stock insuficiente (Concurrencia o falta de inventario). Faltaron: "
                                        + pendiente);
                }

                // CRITICAL FIX: Limpiar el contexto de persistencia para evitar que Hibernate
                // sobrescriba nuestros cambios JDBC con la entidad 'vieja' (que tiene cant=30)
                // al momento del commit/flush.
                entityManager.clear();

                log.info("Descuento inventario completado. Total Unidades: {}", cantidadReal);
        }

        // Sobrecarga para mantener compatibilidad si alguien llama sin el tipo (asume
        // UNIDAD)
        @Transactional
        public void descontarInventario(Integer productoId, Integer cantidad, String motivo) {
                descontarInventario(productoId, cantidad, motivo,
                                com.legacy.pharmacy.inventario.enums.TipoVenta.UNIDAD);
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
        public void devolverInventario(Integer productoId, Integer cantidad, String motivo,
                        com.legacy.pharmacy.inventario.enums.TipoVenta tipoVenta, String destinoProducto) {
                log.info("Devolviendo {} unidades (Tipo:{}) del producto {} - Motivo: {} - Destino: {} - Usuario: {}",
                                cantidad, tipoVenta, productoId, motivo, destinoProducto, UserContext.getUsername());

                Long userId = UserContext.getUserId();
                String username = UserContext.getUsername();

                if (userId == null) {
                        throw new RuntimeException("No se puede devolver inventario: usuario no identificado");
                }

                // Verificar que el producto existe
                Producto producto = productoRepository.findById(productoId)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                // 1. CALCULAR CANTIDAD REAL (CONVERSIÓN DE CAJAS/BLISTERS A UNIDADES)
                int cantidadReal = cantidad;
                if (tipoVenta != null) {
                        switch (tipoVenta) {
                                case CAJA:
                                        if (producto.getUnidadesPorCaja() != null
                                                        && producto.getUnidadesPorCaja() > 0) {
                                                cantidadReal = cantidad * producto.getUnidadesPorCaja();
                                        }
                                        break;
                                case BLISTER:
                                        if (producto.getUnidadesPorBlister() != null
                                                        && producto.getUnidadesPorBlister() > 0) {
                                                cantidadReal = cantidad * producto.getUnidadesPorBlister();
                                        }
                                        break;
                                case UNIDAD:
                                default:
                                        break;
                        }
                }
                log.info("Conversión de Devolución: {} {} -> {} Unidades Totales", cantidad, tipoVenta, cantidadReal);

                try {
                        // Obtener el lote más reciente del producto
                        Integer loteId = jdbcTemplate.queryForObject(
                                        "SELECT id FROM lotes " +
                                                        "WHERE producto_id = ? " +
                                                        "ORDER BY created_at DESC " +
                                                        "LIMIT 1",
                                        Integer.class,
                                        productoId);

                        if (loteId == null) {
                                throw new RuntimeException("No existe un lote para devolver inventario");
                        }

                        // Obtener cantidad actual para la foto (antes de la devolución)
                        Integer cantidadActual = jdbcTemplate.queryForObject(
                                        "SELECT cantidad_actual FROM lotes WHERE id = ?", Integer.class, loteId);
                        int saldoFoto = (cantidadActual != null ? cantidadActual : 0) + cantidadReal;

                        // Insertar movimiento de DEVOLUCION (cantidad positiva)
                        String obsDev = motivo;
                        if (destinoProducto != null && !destinoProducto.trim().isEmpty()) {
                                obsDev += " | Destino: " + destinoProducto;
                        }

                        jdbcTemplate.update(
                                        "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) "
                                                        +
                                                        "VALUES (?, 'DEVOLUCION', ?, ?, ?, 1, ?)",
                                        loteId,
                                        cantidadReal, // Cantidad positiva ya en unidades
                                        saldoFoto,
                                        username != null ? username : "SISTEMA",
                                        obsDev);

                        // Si el destino NO es inventario disponible, retirarlo como MERMA/CUARENTENA
                        if (destinoProducto != null && (destinoProducto.equalsIgnoreCase("MERMA")
                                        || destinoProducto.equalsIgnoreCase("CUARENTENA"))) {
                                int saldoDespuesAjuste = saldoFoto - cantidadReal;
                                jdbcTemplate.update(
                                                "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) "
                                                                +
                                                                "VALUES (?, 'AJUSTE_NEGATIVO', ?, ?, ?, 1, ?)",
                                                loteId,
                                                -cantidadReal, // Movimiento negativo
                                                saldoDespuesAjuste,
                                                username != null ? username : "SISTEMA",
                                                "Traslado automático a " + destinoProducto + " tras devolución");
                        }

                        log.info("Inventario devuelto exitosamente: producto={}, unidades={}, lote={}",
                                        productoId, cantidadReal, loteId);

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

                // Reutilizamos el método robusto que acabamos de crear
                // "VENTA_EXTERNA" será el motivo
                // Reutilizamos el método robusto que acabamos de crear
                // "VENTA_EXTERNA" será el motivo
                descontarInventario(productoId, cantidad, "VENTA_EXTERNA",
                                com.legacy.pharmacy.inventario.enums.TipoVenta.UNIDAD);
        }

        @Transactional
        public void reponerInventarioDevolucion(Integer productoId, Integer cantidad,
                        com.legacy.pharmacy.inventario.enums.TipoVenta tipoVenta, String destinoProducto) {
                // Obtenemos producto para el factor de conversión
                Producto producto = productoRepository.findById(productoId)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                // 1. CALCULAR CANTIDAD REAL (CONVERSIÓN DE CAJAS/BLISTERS A UNIDADES)
                int cantidadReal = cantidad;
                if (tipoVenta != null) {
                        switch (tipoVenta) {
                                case CAJA:
                                        if (producto.getUnidadesPorCaja() != null
                                                        && producto.getUnidadesPorCaja() > 0) {
                                                cantidadReal = cantidad * producto.getUnidadesPorCaja();
                                        }
                                        break;
                                case BLISTER:
                                        if (producto.getUnidadesPorBlister() != null
                                                        && producto.getUnidadesPorBlister() > 0) {
                                                cantidadReal = cantidad * producto.getUnidadesPorBlister();
                                        }
                                        break;
                                case UNIDAD:
                                default:
                                        break;
                        }
                }
                log.info("Conversión de Reposición: {} {} -> {} Unidades Totales", cantidad, tipoVenta, cantidadReal);

                // Buscamos el último lote activo para sumarle ahí (simplificado)
                // O insertamos un movimiento de entrada
                String sqlLote = "SELECT id FROM lotes WHERE producto_id = ? ORDER BY fecha_vencimiento DESC LIMIT 1";
                try {
                        Integer loteId = jdbcTemplate.queryForObject(sqlLote, Integer.class, productoId);

                        // Calculamos saldo foto (aproximado, ya que es query simple)
                        Integer cantidadActual = jdbcTemplate.queryForObject(
                                        "SELECT cantidad_actual FROM lotes WHERE id = ?", Integer.class, loteId);
                        int saldoFoto = (cantidadActual != null ? cantidadActual : 0) + cantidadReal;

                        // Insertamos el movimiento de retorno
                        String obs = "Devolución integral";
                        if (destinoProducto != null && !destinoProducto.trim().isEmpty()) {
                                obs += " | Destino: " + destinoProducto;
                        }

                        String sqlInsert = "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) "
                                        +
                                        "VALUES (?, 'DEVOLUCION', ?, ?, 'MS-VENTAS', 1, ?)";

                        jdbcTemplate.update(sqlInsert, loteId, cantidadReal, saldoFoto, obs);

                        if (destinoProducto != null && (destinoProducto.equalsIgnoreCase("MERMA")
                                        || destinoProducto.equalsIgnoreCase("CUARENTENA"))) {
                                int saldoDespuesAjuste = saldoFoto - cantidadReal;
                                jdbcTemplate.update(
                                                "INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, saldo_historico, usuario_responsable, sucursal_id, observaciones) "
                                                                + "VALUES (?, 'AJUSTE_NEGATIVO', ?, ?, 'MS-VENTAS', 1, ?)",
                                                loteId, -cantidadReal, saldoDespuesAjuste,
                                                "Traslado automático a " + destinoProducto + " tras devolución");
                        }

                } catch (Exception e) {
                        throw new RuntimeException("No se encontró lote para procesar la devolución");
                }
        }

        @Transactional(readOnly = true) // <--- ESTO ES VITAL PARA QUE NO FALLE
        public DashboardAlertasDTO obtenerDashboardAlertas() {
                LocalDate hoy = LocalDate.now();

                // 1. OBTENER VENCIDOS
                List<Lote> lotesVencidos = loteRepository.findByFechaVencimientoBeforeAndCantidadActualGreaterThan(hoy,
                                0);

                List<Map<String, Object>> listaVencidos = lotesVencidos.stream()
                                .map(l -> {
                                        Map<String, Object> map = new java.util.HashMap<>();
                                        map.put("id", l.getId());
                                        map.put("producto", l.getProducto().getNombreComercial());
                                        map.put("lote", l.getNumeroLote());
                                        map.put("fecha", l.getFechaVencimiento());
                                        map.put("cantidad", l.getCantidadActual());
                                        map.put("imagenUrl", l.getProducto().getImagenUrl()); // ✅ IMAGEN
                                        return map;
                                })
                                .collect(java.util.stream.Collectors.toList());

                // 2. OBTENER POR VENCER
                List<Lote> lotesPorVencer = loteRepository
                                .findByFechaVencimientoBetweenAndCantidadActualGreaterThan(hoy, hoy.plusDays(30), 0);

                List<Map<String, Object>> listaPorVencer = lotesPorVencer.stream()
                                .map(l -> {
                                        Map<String, Object> map = new java.util.HashMap<>();
                                        map.put("id", l.getId());
                                        map.put("producto", l.getProducto().getNombreComercial());
                                        map.put("lote", l.getNumeroLote());
                                        map.put("fecha", l.getFechaVencimiento());
                                        map.put("cantidad", l.getCantidadActual());
                                        map.put("diasRestantes", java.time.temporal.ChronoUnit.DAYS.between(hoy,
                                                        l.getFechaVencimiento()));
                                        map.put("imagenUrl", l.getProducto().getImagenUrl()); // ✅ IMAGEN
                                        return map;
                                })
                                .collect(java.util.stream.Collectors.toList());

                // 3. OBTENER STOCK BAJO
                List<Producto> productosBajoStock = productoRepository.findProductosBajoStock();

                List<Map<String, Object>> listaStockBajo = productosBajoStock.stream()
                                .map(p -> {
                                        Integer stockReal = consultarStockActual(p.getId());
                                        Map<String, Object> map = new java.util.HashMap<>();
                                        map.put("id", p.getId());
                                        map.put("nombre", p.getNombreComercial());
                                        map.put("stockActual", stockReal);
                                        map.put("stockMinimo", p.getStockMinimo());
                                        map.put("imagenUrl", p.getImagenUrl()); // ✅ IMAGEN
                                        return map;
                                })
                                .collect(java.util.stream.Collectors.toList());

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