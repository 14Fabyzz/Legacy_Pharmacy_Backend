-- Script para soportar unidades fraccionadas en el inventario
-- Ejecutar en la base de datos MySQL (Aiven)

ALTER TABLE productos
ADD COLUMN es_fraccionable TINYINT(1) DEFAULT 0,
ADD COLUMN unidades_por_caja INT DEFAULT 1,
ADD COLUMN precio_venta_unidad DECIMAL(38,2) DEFAULT NULL;
