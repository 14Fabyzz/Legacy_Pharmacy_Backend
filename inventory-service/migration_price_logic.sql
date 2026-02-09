-- =====================================================
-- MIGRACIÓN: MOTOR DE PRECIOS DETERMINISTA
-- Fecha: 2026-02-09
-- Descripción: Refactorización para cálculo automático de precios
-- =====================================================

-- =====================================================
-- 1. AGREGAR NUEVAS COLUMNAS
-- =====================================================

-- Columna para porcentaje de ganancia (margen)
ALTER TABLE productos
ADD COLUMN IF NOT EXISTS porcentaje_ganancia DECIMAL(10,2) DEFAULT 30.00
COMMENT 'Porcentaje de ganancia sobre el costo de compra';

-- Columna para precio de venta total (Base + IVA)
ALTER TABLE productos
ADD COLUMN IF NOT EXISTS precio_venta_total DECIMAL(19,2) DEFAULT 0.00
COMMENT 'Precio final de venta con IVA incluido (calculado)';

-- =====================================================
-- 2. MIGRACIÓN DE DATOS EXISTENTES
-- =====================================================

-- Para productos que NO tienen precio_compra_referencia, 
-- calculamos el costo trabajando hacia atrás desde precio_venta_base
-- Asumimos: precio_venta_base = precio_compra_referencia * 1.30 (30% ganancia)
UPDATE productos
SET precio_compra_referencia = ROUND(precio_venta_base / 1.30, 2)
WHERE precio_compra_referencia IS NULL 
  AND precio_venta_base IS NOT NULL
  AND precio_venta_base > 0;

-- Para productos que aún no tienen precio_compra_referencia, usar precio_venta_base
UPDATE productos
SET precio_compra_referencia = precio_venta_base
WHERE precio_compra_referencia IS NULL;

-- Asegurar que todos tengan un valor mínimo
UPDATE productos
SET precio_compra_referencia = 0
WHERE precio_compra_referencia IS NULL;

-- Calcular precio_venta_base (Costo + Ganancia) para todos los productos
UPDATE productos
SET precio_venta_base = ROUND(
    precio_compra_referencia * (1 + (porcentaje_ganancia / 100)), 
    2
)
WHERE precio_compra_referencia IS NOT NULL;

-- Calcular precio_venta_total (Base + IVA) para todos los productos
UPDATE productos
SET precio_venta_total = ROUND(
    precio_venta_base * (1 + (COALESCE(iva_porcentaje, 0) / 100)), 
    2
)
WHERE precio_venta_base IS NOT NULL;

-- Recalcular precio_venta_unidad para productos fraccionables con redondeo a cincuentena
UPDATE productos
SET precio_venta_unidad = CEILING(
    (precio_venta_total / unidades_por_caja) / 50
) * 50
WHERE es_fraccionable = TRUE 
  AND unidades_por_caja > 1
  AND precio_venta_total IS NOT NULL;

-- Para productos NO fraccionables, precio_venta_unidad = precio_venta_total
UPDATE productos
SET precio_venta_unidad = precio_venta_total
WHERE (es_fraccionable = FALSE OR unidades_por_caja <= 1)
  AND precio_venta_total IS NOT NULL;

-- Recalcular precio_venta_blister para productos que tienen unidades_por_blister
UPDATE productos
SET precio_venta_blister = precio_venta_unidad * unidades_por_blister
WHERE unidades_por_blister IS NOT NULL
  AND unidades_por_blister > 0
  AND precio_venta_unidad IS NOT NULL;

-- =====================================================
-- 3. VALIDACIONES
-- =====================================================

-- Verificar que precio_compra_referencia no sea NULL
SELECT 
    COUNT(*) as productos_sin_costo,
    'ADVERTENCIA: Hay productos sin costo de compra' as mensaje
FROM productos
WHERE precio_compra_referencia IS NULL
HAVING COUNT(*) > 0;

-- Verificar que las nuevas columnas no tengan NULL
SELECT 
    COUNT(*) as productos_sin_porcentaje,
    'ADVERTENCIA: Hay productos sin porcentaje de ganancia' as mensaje
FROM productos
WHERE porcentaje_ganancia IS NULL
HAVING COUNT(*) > 0;

SELECT 
    COUNT(*) as productos_sin_precio_total,
    'ADVERTENCIA: Hay productos sin precio total' as mensaje
FROM productos
WHERE precio_venta_total IS NULL
HAVING COUNT(*) > 0;

-- =====================================================
-- 4. REPORTE DE VERIFICACIÓN
-- =====================================================

-- Mostrar algunos ejemplos de productos con precios calculados
SELECT 
    id,
    codigo_interno,
    nombre_comercial,
    precio_compra_referencia as costo,
    porcentaje_ganancia as margen,
    precio_venta_base as base,
    iva_porcentaje as iva,
    precio_venta_total as total,
    es_fraccionable,
    unidades_por_caja,
    precio_venta_unidad as precio_unidad,
    unidades_por_blister,
    precio_venta_blister as precio_blister
FROM productos
ORDER BY id
LIMIT 10;

-- Mostrar estadísticas generales
SELECT 
    COUNT(*) as total_productos,
    COUNT(CASE WHEN precio_compra_referencia > 0 THEN 1 END) as con_costo,
    COUNT(CASE WHEN porcentaje_ganancia > 0 THEN 1 END) as con_margen,
    COUNT(CASE WHEN precio_venta_total > 0 THEN 1 END) as con_precio_total,
    COUNT(CASE WHEN es_fraccionable = TRUE THEN 1 END) as fraccionables,
    AVG(porcentaje_ganancia) as margen_promedio
FROM productos;

-- =====================================================
-- NOTAS IMPORTANTES
-- =====================================================

/*
CAMPOS AFECTADOS:

1. precio_compra_referencia (EXISTENTE)
   - Ahora es el INPUT principal del costo
   - Migrado desde precio_venta_base para productos sin valor

2. porcentaje_ganancia (NUEVO)
   - DEFAULT 30.00
   - INPUT del margen de ganancia

3. precio_venta_base (EXISTENTE - AHORA CALCULADO)
   - Antes: Input manual
   - Ahora: Calculado = precio_compra_referencia * (1 + ganancia)

4. precio_venta_total (NUEVO)
   - Calculado = precio_venta_base * (1 + IVA)

5. precio_venta_unidad (EXISTENTE - AHORA CON REDONDEO)
   - Calculado con redondeo al techo de cincuentena
   - Ejemplo: 773.50 → 800

6. precio_venta_blister (EXISTENTE)
   - Calculado = precio_venta_unidad * unidades_por_blister

COMPATIBILIDAD:
- MySQL 8.0+
- Idempotente (puede ejecutarse múltiples veces)
- Usa IF NOT EXISTS para evitar errores

PRÓXIMOS PASOS:
1. Ejecutar este script en la base de datos
2. Actualizar entidad Producto.java
3. Actualizar ProductoDTO.java
4. Actualizar ProductoService.java
*/
