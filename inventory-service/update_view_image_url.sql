-- =============================================================================
-- SCRIPT DE ACTUALIZACIÓN DE VISTA (v_stock_productos)
-- =============================================================================
-- Este script actualiza la vista utilizada por el Dashboard y Buscador
-- para incluir el nuevo campo 'imagen_url' de la tabla productos.
--
-- OPTIMIZACIÓN: Se reemplazó el GROUP BY masivo en la consulta principal por
-- una subconsulta pre-agregada sobre la tabla 'lotes'.
-- =============================================================================

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
    p.imagen_url, -- ✅ NUEVO CAMPO PROPAGADO
    l.nombre AS laboratorio_nombre,
    c.nombre AS categoria_nombre,
    pa.nombre AS principio_activo_nombre,
    COALESCE(lt_agg.stock_total, 0) AS stock_total,
    lt_agg.proximo_vencimiento AS proximo_vencimiento,
    CASE
        WHEN COALESCE(lt_agg.stock_total, 0) = 0 THEN 'SIN_STOCK'
        WHEN COALESCE(lt_agg.stock_total, 0) <= p.stock_minimo THEN 'BAJO'
        ELSE 'OPTIMO'
    END AS nivel_stock
FROM productos p
LEFT JOIN laboratorios l ON p.laboratorio_id = l.id
LEFT JOIN categorias c ON p.categoria_id = c.id
LEFT JOIN principios_activos pa ON p.principio_activo_id = pa.id
LEFT JOIN (
    -- Subconsulta pre-agregada: O(N) en lugar de agrupar todo al final
    SELECT producto_id, 
           SUM(cantidad_actual) AS stock_total, 
           MIN(fecha_vencimiento) AS proximo_vencimiento
    FROM lotes 
    WHERE cantidad_actual > 0
    GROUP BY producto_id
) lt_agg ON p.id = lt_agg.producto_id;
