-- =============================================================================
-- SCRIPT DE LOTES DE PRUEBA — SOLO PARA DESARROLLO/QA
-- =============================================================================
-- ⚠️  ADVERTENCIA: Este script es DESTRUCTIVO. Borra y re-crea los lotes simulados.
--     NUNCA ejecutar en PRODUCCIÓN. Solo en entornos de desarrollo/QA.
--
-- Ejecución manual en DBeaver/WorkBench:
--   1. Conectarse a la BD de inventario (legacy_pharmacy_inventory)
--   2. Ejecutar este script manualmente
--   3. Verificar los lotes con: SELECT * FROM lotes ORDER BY fecha_vencimiento;
-- =============================================================================

-- ⚠️  PELIGROSO: Borra todos los lotes existentes (solo para ambiente de pruebas)
DELETE FROM `lotes`;

-- -----------------------------------------------------------------------------
-- Lote 1: VENCIDO (Hace 1 año) → Para probar alertas de vencimiento
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `lotes` (`producto_id`, `numero_lote`, `fecha_vencimiento`, `cantidad_actual`, `costo_compra`)
SELECT id, CONCAT('L-EXP-', id), DATE_SUB(CURDATE(), INTERVAL 1 YEAR), 5, precio_compra_referencia
FROM productos WHERE estado = 'Activo' LIMIT 10;

-- -----------------------------------------------------------------------------
-- Lote 2: POR VENCER (En 3 meses) → Para probar alertas amarillas
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `lotes` (`producto_id`, `numero_lote`, `fecha_vencimiento`, `cantidad_actual`, `costo_compra`)
SELECT id, CONCAT('L-WARN-', id), DATE_ADD(CURDATE(), INTERVAL 3 MONTH), 20, precio_compra_referencia
FROM productos WHERE estado = 'Activo' LIMIT 10 OFFSET 10;

-- -----------------------------------------------------------------------------
-- Lote 3: VIGENTE (En 2 años) → Stock normal
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `lotes` (`producto_id`, `numero_lote`, `fecha_vencimiento`, `cantidad_actual`, `costo_compra`)
SELECT id, CONCAT('L-OK-', id), DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 50, precio_compra_referencia
FROM productos WHERE estado = 'Activo';

-- Verificación rápida
SELECT
    COUNT(*) AS total_lotes,
    SUM(CASE WHEN fecha_vencimiento < CURDATE() THEN 1 ELSE 0 END) AS lotes_vencidos,
    SUM(CASE WHEN fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 MONTH) THEN 1 ELSE 0 END) AS lotes_por_vencer,
    SUM(CASE WHEN fecha_vencimiento > DATE_ADD(CURDATE(), INTERVAL 3 MONTH) THEN 1 ELSE 0 END) AS lotes_vigentes
FROM lotes;
