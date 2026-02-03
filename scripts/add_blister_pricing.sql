-- ALERTA: Ejecutar esto en la base de datos MySQL (Inventory Service)

-- 1. Agregar columna para precio de blister
ALTER TABLE productos
ADD COLUMN precio_venta_blister DECIMAL(38,2) DEFAULT NULL;

-- 2. Calcular precio retroactivos para productos que ya tienen configuración de blister
-- Lógica: Precio Blister = Precio Unidad * Unidades Por Blister
SET SQL_SAFE_UPDATES = 0;

UPDATE productos
SET precio_venta_blister = precio_venta_unidad * unidades_por_blister
WHERE es_fraccionable = 1 
  AND unidades_por_blister IS NOT NULL 
  AND unidades_por_blister > 0
  AND precio_venta_unidad IS NOT NULL;

SET SQL_SAFE_UPDATES = 1;
