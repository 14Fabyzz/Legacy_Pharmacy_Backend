-- ACTUALIZACIÓN DE VISTA DE PRODUCTOS (Backend Search)
-- Ejecutar en Aiven MySQL

CREATE OR REPLACE VIEW v_stock_productos AS
SELECT 
    p.id AS producto_id,
    p.codigo_interno,
    p.codigo_barras,
    p.nombre_comercial,
    p.concentracion,
    p.presentacion,
    p.precio_venta_base,
    p.stock_minimo,
    p.es_fraccionable,          -- <--- NUEVO
    p.unidades_por_caja,        -- <--- NUEVO
    p.precio_venta_unidad,      -- <--- NUEVO
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
    p.concentracion, p.presentacion, p.precio_venta_base, 
    p.stock_minimo, p.es_fraccionable, p.unidades_por_caja, p.precio_venta_unidad,
    l.nombre, c.nombre, pa.nombre;
