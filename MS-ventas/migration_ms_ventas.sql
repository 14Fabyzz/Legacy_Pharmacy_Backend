-- =====================================================
-- SCRIPT DE MIGRACIÓN: MS-VENTAS (db_transacciones)
-- Fecha: 2026-01-30
-- Autor: Sistema de Gestión de Farmacia
-- =====================================================

-- =====================================================
-- 1. SOPORTE PARA PRECIOS DUALES (Caja vs Unidad)
-- =====================================================

-- Agregar columna para registrar el tipo de venta (Caja o Unidad)
-- Permite auditoría y trazabilidad de cómo se vendió cada producto
ALTER TABLE detalle_ventas
ADD COLUMN IF NOT EXISTS es_venta_por_caja BOOLEAN DEFAULT FALSE
COMMENT 'Indica si la venta fue por Caja (true) o por Unidad (false)';

-- Verificar que la columna se agregó correctamente
-- En PostgreSQL:
-- \d detalle_ventas

-- =====================================================
-- 2. DATOS SEMILLA: CLIENTE GENÉRICO
-- =====================================================

-- Cliente por defecto para ventas de mostrador (cuantía menor)
-- IMPORTANTE: NO se puede usar para medicamentos controlados
INSERT INTO clientes (id, nombre, numero_documento, tipo_documento, email) 
VALUES (1, 'Cliente Mostrador / Cuantía Menor', '222222222222', 'CC', 'sin_email@farmacia.com')
ON CONFLICT (id) DO NOTHING;

-- Ajustar secuencia para evitar conflictos en IDs futuros
SELECT setval('clientes_id_seq', (SELECT GREATEST(MAX(id), 1) FROM clientes), true);

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Verificar que el cliente genérico existe
SELECT * FROM clientes WHERE id = 1;

-- Verificar estructura de detalle_ventas
SELECT column_name, data_type, column_default, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'detalle_ventas' 
  AND column_name = 'es_venta_por_caja';

-- =====================================================
-- NOTAS IMPORTANTES
-- =====================================================

/*
1. Este script es IDEMPOTENTE (se puede ejecutar múltiples veces sin errores)
2. Usa ADD COLUMN IF NOT EXISTS para evitar errores si ya existe
3. Usa ON CONFLICT DO NOTHING para el cliente genérico
4. Compatible con PostgreSQL

CONFIGURACIÓN REQUERIDA:
- En application.properties debe estar:
  ventas.cliente-generico-id=1

REGLAS DE NEGOCIO:
- es_venta_por_caja se usa para auditoría
- Cliente ID=1 NO puede usarse para medicamentos controlados
- La validación está en VentaServiceImpl.java
*/
