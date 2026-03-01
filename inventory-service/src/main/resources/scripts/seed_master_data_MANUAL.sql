-- =============================================================================
-- SCRIPT DE DATOS MAESTROS — EJECUCIÓN ÚNICA MANUAL
-- =============================================================================
-- Propósito: Poblar tablas maestras (Laboratorios, Categorías, Principios Activos)
-- la PRIMERA VEZ que se crea una base de datos nueva o vacía.
--
-- ✅  SEGURO: Todos los INSERTs usan INSERT IGNORE o WHERE NOT EXISTS.
--     Nunca duplica ni borra datos existentes. Es idempotente.
--
-- Ejecución: Conectarse a legacy_pharmacy_inventory y ejecutar manualmente.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. CATEGORÍAS
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `categorias` (`nombre`, `activa`) VALUES ('GENERAL', 1);

-- -----------------------------------------------------------------------------
-- 2. LABORATORIOS (24 laboratorios maestros)
-- -----------------------------------------------------------------------------
INSERT INTO laboratorios (nombre, activo) SELECT 'GENERICO', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'GENERICO');
INSERT INTO laboratorios (nombre, activo) SELECT 'AG', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'AG');
INSERT INTO laboratorios (nombre, activo) SELECT 'GENFAR', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'GENFAR');
INSERT INTO laboratorios (nombre, activo) SELECT 'BAYER', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'BAYER');
INSERT INTO laboratorios (nombre, activo) SELECT 'OTC', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'OTC');
INSERT INTO laboratorios (nombre, activo) SELECT 'OPHARM', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'OPHARM');
INSERT INTO laboratorios (nombre, activo) SELECT 'ANGLOPHARMA', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'ANGLOPHARMA');
INSERT INTO laboratorios (nombre, activo) SELECT 'NACIONAL DE QUIMICOS', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'NACIONAL DE QUIMICOS');
INSERT INTO laboratorios (nombre, activo) SELECT 'DROGA BLANCA', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'DROGA BLANCA');
INSERT INTO laboratorios (nombre, activo) SELECT 'ECAR', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'ECAR');
INSERT INTO laboratorios (nombre, activo) SELECT 'LABQUIFAR', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'LABQUIFAR');
INSERT INTO laboratorios (nombre, activo) SELECT 'LAPROFF', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'LAPROFF');
INSERT INTO laboratorios (nombre, activo) SELECT 'COASPHARMA', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'COASPHARMA');
INSERT INTO laboratorios (nombre, activo) SELECT 'MEMPHIS', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'MEMPHIS');
INSERT INTO laboratorios (nombre, activo) SELECT 'CUIDADO DEL BEBE', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'CUIDADO DEL BEBE');
INSERT INTO laboratorios (nombre, activo) SELECT 'HARTUNG', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'HARTUNG');
INSERT INTO laboratorios (nombre, activo) SELECT 'JUHNIOS', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'JUHNIOS');
INSERT INTO laboratorios (nombre, activo) SELECT 'AFR SAS', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'AFR SAS');
INSERT INTO laboratorios (nombre, activo) SELECT 'ABBOTT', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'ABBOTT');
INSERT INTO laboratorios (nombre, activo) SELECT 'ALIKIN', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'ALIKIN');
INSERT INTO laboratorios (nombre, activo) SELECT 'TOP GLOVE', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'TOP GLOVE');
INSERT INTO laboratorios (nombre, activo) SELECT 'OSA', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'OSA');
INSERT INTO laboratorios (nombre, activo) SELECT 'SIEGFRIED', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'SIEGFRIED');
INSERT INTO laboratorios (nombre, activo) SELECT 'NOVAMED', 1 WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'NOVAMED');

-- -----------------------------------------------------------------------------
-- 3. PRINCIPIOS ACTIVOS
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACICLOVIR');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACETATO DE ALUMINIO');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACIDO ACETILSALICILICO');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACIDO BORICO');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACIDO FOLICO');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACIDO FUSIDICO');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACEITE MINERAL');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACETILCISTEINA');
INSERT IGNORE INTO `principios_activos` (`nombre`) VALUES ('ACETAMINOFEN');

-- Verificación
SELECT 'Laboratorios' AS tabla, COUNT(*) AS registros FROM laboratorios
UNION ALL SELECT 'Categorías', COUNT(*) FROM categorias
UNION ALL SELECT 'Principios Activos', COUNT(*) FROM principios_activos;
