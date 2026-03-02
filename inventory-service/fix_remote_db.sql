-- =============================================================================
-- FIX PARA BASE DE DATOS REMOTA (AIVEN)
-- =============================================================================
-- Ejecuta este script en TU BASE DE DATOS DE AIVEN (la que usa Docker).
-- =============================================================================

-- 1. Asegurar que la tabla 'productos' tenga las columnas de imagen
-- Si estas columnas ya existen, esta parte dará un error leve que puedes ignorar,
-- pero es CRÍTICO que existan para que la vista funcione.
ALTER TABLE productos ADD COLUMN imagen_url VARCHAR(255) NULL;
ALTER TABLE productos ADD COLUMN imagen_id VARCHAR(255) NULL;

-- 2. Recrear la Vista con sintaxis MySQL estándar (Backticks)
CREATE OR REPLACE VIEW `v_stock_productos` AS
SELECT
    p.id AS producto_id,
    p.codigo_interno,
    p.codigo_barras,
    p.nombre_comercial,
    p.concentracion,
    p.presentacion,
    p.precio_venta_base,
    p.precio_venta_total,
    p.precio_venta_unidad,
    p.precio_venta_blister,
    p.iva_porcentaje,
    p.stock_minimo,
    p.es_fraccionable,
    p.unidades_por_caja,
    p.refrigerado,
    p.es_controlado,
    p.imagen_url, -- ✅ CAMPO CLAVE
    l.nombre AS laboratorio_nombre,
    c.nombre AS categoria_nombre,
    pa.nombre AS principio_activo_nombre,
    COALESCE(SUM(lt.cantidad_actual), 0) AS stock_total,
    MIN(lt.fecha_vencimiento) AS proximo_vencimiento,
    CASE
        WHEN COALESCE(SUM(lt.cantidad_actual), 0) = 0 THEN 'SIN_STOCK'
        WHEN COALESCE(SUM(lt.cantidad_actual), 0) <= p.stock_minimo THEN 'BAJO'
        ELSE 'OPTIMO'
    END AS nivel_stock
FROM productos p
LEFT JOIN laboratorios l ON p.laboratorio_id = l.id
LEFT JOIN categorias c ON p.categoria_id = c.id
LEFT JOIN principios_activos pa ON p.principio_activo_id = pa.id
LEFT JOIN lotes lt ON p.id = lt.producto_id AND lt.cantidad_actual > 0
GROUP BY 
    p.id, p.codigo_interno, p.codigo_barras, p.nombre_comercial, 
    p.concentracion, p.presentacion, p.precio_venta_base, p.precio_venta_total,
    p.precio_venta_unidad, p.precio_venta_blister, p.iva_porcentaje,
    p.stock_minimo, p.es_fraccionable, p.unidades_por_caja,
    p.refrigerado, p.es_controlado, p.imagen_url, -- ✅ IMPORTANTE EN GROUP BY
    l.nombre, c.nombre, pa.nombre;
