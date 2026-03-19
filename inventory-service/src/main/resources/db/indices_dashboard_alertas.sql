-- =============================================================================
-- ÍNDICES RECOMENDADOS — Optimización Dashboard de Alertas
-- Ejecutar en Aiven MySQL una sola vez (verificar que no existan antes)
-- =============================================================================

-- 1. Índice compuesto para las queries de vencidos y por vencer.
--    Cubre el filtro WHERE fecha_vencimiento < :hoy (o BETWEEN) Y cantidad_actual > 0.
--    Permite que MySQL use un Index Range Scan en lugar de Full Table Scan.
CREATE INDEX IF NOT EXISTS idx_lotes_vencimiento_cantidad
    ON lotes (fecha_vencimiento, cantidad_actual);

-- 2. Índice compuesto para el LEFT JOIN de la query de stock bajo.
--    Cubre: JOIN ON producto_id = ? AND cantidad_actual > 0 usado en el GROUP BY.
CREATE INDEX IF NOT EXISTS idx_lotes_producto_cantidad
    ON lotes (producto_id, cantidad_actual);

-- 3. Índice en productos.estado para el filtro WHERE estado = 'ACTIVO'.
--    Bajo cardinalidad (pocos valores distintos), pero útil si la tabla crece mucho.
CREATE INDEX IF NOT EXISTS idx_productos_estado
    ON productos (estado);

-- =============================================================================
-- VERIFICACIÓN — Ejecutar después de crear los índices
-- Debe mostrar los índices creados con el nombre correcto
-- =============================================================================
-- SHOW INDEX FROM lotes WHERE Key_name IN ('idx_lotes_vencimiento_cantidad', 'idx_lotes_producto_cantidad');
-- SHOW INDEX FROM productos WHERE Key_name = 'idx_productos_estado';
