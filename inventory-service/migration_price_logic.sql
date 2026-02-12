-- =====================================================
-- SCRIPT DE MIGRACIÓN: MOTOR DE PRECIOS DETERMINISTA (CORREGIDO)
-- Fecha: 2026-02-09
-- Autor: Sistema de Gestión de Farmacia
-- Objetivo: Refactorizar tabla productos para cálculo automático de precios
-- =====================================================

-- =====================================================
-- PASO 1: AGREGAR COLUMNAS NUEVAS (Idempotente para MySQL)
-- =====================================================

-- Procedimiento para agregar columna si no existe
DELIMITER $$

DROP PROCEDURE IF EXISTS add_column_if_not_exists$$
CREATE PROCEDURE add_column_if_not_exists(
    IN table_name VARCHAR(128),
    IN column_name VARCHAR(128),
    IN column_definition VARCHAR(255)
)
BEGIN
    DECLARE column_count INT;
    
    SELECT COUNT(*) INTO column_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = table_name
      AND COLUMN_NAME = column_name;
    
    IF column_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', table_name, ' ADD COLUMN ', column_name, ' ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- Agregar columnas usando el procedimiento
CALL add_column_if_not_exists('productos', 'porcentaje_ganancia', 'DECIMAL(10,2) NULL COMMENT ''Margen de ganancia sobre el costo (porcentaje: 30 = 30%)''');
CALL add_column_if_not_exists('productos', 'precio_venta_total', 'DECIMAL(19,2) NULL COMMENT ''Precio final de venta incluyendo IVA (calculado automáticamente)''');

-- Limpiar procedimiento temporal
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- =====================================================
-- PASO 2: MIGRACIÓN DE DATOS EXISTENTES (Ingeniería Inversa)
-- =====================================================

-- 2.1. Calcular porcentaje de ganancia
UPDATE productos 
SET porcentaje_ganancia = CASE 
    WHEN precio_compra_referencia IS NOT NULL AND precio_compra_referencia > 0 THEN 
        ROUND(((precio_venta_base - precio_compra_referencia) / precio_compra_referencia) * 100, 2)
    ELSE 
        30.00
END
WHERE porcentaje_ganancia IS NULL;

-- 2.2. Calcular precio_venta_total
UPDATE productos 
SET precio_venta_total = ROUND(
    precio_venta_base * (1 + (COALESCE(iva_porcentaje, 0) / 100)), 
    2
)
WHERE precio_venta_total IS NULL;

-- =====================================================
-- PASO 3: ASEGURAR CONSISTENCIA DE DATOS
-- =====================================================

-- 3.1. Asegurar que IVA nunca sea NULL
UPDATE productos 
SET iva_porcentaje = 0.00 
WHERE iva_porcentaje IS NULL;

-- 3.2. Asegurar que precio_compra_referencia nunca sea NULL
UPDATE productos 
SET precio_compra_referencia = 0.00 
WHERE precio_compra_referencia IS NULL;

-- =====================================================
-- PASO 3.5: LIMPIEZA DE SEGURIDAD (CRÍTICO - ANTES DEL ALTER)
-- =====================================================
-- Objetivo: Evitar error "Data truncated" en el PASO 4.
-- Forzamos valores por defecto en cualquier fila que haya quedado NULL.

UPDATE productos 
SET porcentaje_ganancia = 30.00 
WHERE porcentaje_ganancia IS NULL;

UPDATE productos 
SET precio_venta_total = 0.00 
WHERE precio_venta_total IS NULL;

-- =====================================================
-- PASO 3.6: CORRECCIÓN DE precio_venta_total FALTANTE
-- =====================================================
-- Objetivo: Asegurar que precio_venta_total esté calculado para productos válidos.
-- IMPORTANTE: NO modificamos porcentaje_ganancia = 0 (regla de negocio: puede ser 0 por decisión del admin)
-- Solo recalculamos precio_venta_total si está en NULL o 0 pero precio_venta_base es válido.

UPDATE productos 
SET precio_venta_total = ROUND(
    precio_venta_base * (1 + (COALESCE(iva_porcentaje, 0) / 100)), 
    2
)
WHERE (precio_venta_total IS NULL OR precio_venta_total = 0.00)
  AND precio_venta_base IS NOT NULL 
  AND precio_venta_base > 0;


-- =====================================================
-- PASO 4: APLICAR CONSTRAINTS Y DEFAULTS
-- =====================================================
-- Ahora sí es seguro aplicar NOT NULL.

ALTER TABLE productos 
MODIFY COLUMN porcentaje_ganancia DECIMAL(10,2) NOT NULL DEFAULT 30.00;

ALTER TABLE productos 
MODIFY COLUMN precio_venta_total DECIMAL(19,2) NOT NULL DEFAULT 0.00;

ALTER TABLE productos 
MODIFY COLUMN iva_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 0.00;

ALTER TABLE productos 
MODIFY COLUMN precio_compra_referencia DECIMAL(19,2) NOT NULL DEFAULT 0.00;

-- =====================================================
-- PASO 5: VERIFICACIÓN (Al final de todo)
-- =====================================================

-- Ver estructura
DESCRIBE productos;

-- Verificar que no haya valores NULL (Deberían ser todos 0)
SELECT 
    COUNT(*) as total_productos,
    SUM(CASE WHEN porcentaje_ganancia IS NULL THEN 1 ELSE 0 END) as nulls_ganancia,
    SUM(CASE WHEN precio_venta_total IS NULL THEN 1 ELSE 0 END) as nulls_total
FROM productos;

-- Ver muestra de datos
SELECT 
    id,
    nombre_comercial,
    precio_compra_referencia AS costo,
    porcentaje_ganancia AS ganancia,
    precio_venta_total AS total_calculado
FROM productos
LIMIT 10;