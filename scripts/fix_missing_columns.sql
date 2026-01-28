-- SCRIPT DE CORRECCIÓN DE ESTRUCTURA (Faltan columnas de Sucursal y Referencias)
-- Ejecutar en Aiven MySQL

-- 1. Agregar sucursal_id a la tabla LOTES
SET @dbname = DATABASE();
SET @tablename = "lotes";
SET @columnname = "sucursal_id";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE lotes ADD COLUMN sucursal_id INT DEFAULT 1;"
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. Agregar sucursal_id a la tabla MOVIMIENTOS
SET @tablename2 = "movimientos";
SET @columnname2 = "sucursal_id";
SET @preparedStatement2 = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename2)
      AND (table_schema = @dbname)
      AND (column_name = @columnname2)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE movimientos ADD COLUMN sucursal_id INT DEFAULT 1;"
));
PREPARE alterIfNotExists2 FROM @preparedStatement2;
EXECUTE alterIfNotExists2;
DEALLOCATE PREPARE alterIfNotExists2;

-- 3. Agregar ref_venta_id a la tabla MOVIMIENTOS (usado en registrar_salida)
SET @columnname3 = "ref_venta_id";
SET @preparedStatement3 = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename2)
      AND (table_schema = @dbname)
      AND (column_name = @columnname3)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE movimientos ADD COLUMN ref_venta_id INT NULL;"
));
PREPARE alterIfNotExists3 FROM @preparedStatement3;
EXECUTE alterIfNotExists3;
DEALLOCATE PREPARE alterIfNotExists3;

SELECT 'Estructura corregida exitosamente' as mensaje;
