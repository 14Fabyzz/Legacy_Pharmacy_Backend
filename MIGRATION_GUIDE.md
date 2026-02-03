# 📋 Guía de Migración de Base de Datos

Esta guía detalla cómo aplicar las migraciones de base de datos para los microservicios actualizados.

## 🎯 Resumen de Cambios

### MS-Ventas (db_transacciones)
- ✅ Campo `es_venta_por_caja` en tabla `detalle_ventas`
- ✅ Cliente genérico (ID=1) para ventas de mostrador

### Inventory Service
- ✅ Campo `unidades_por_blister` en tabla `productos`
- ✅ Campo `tipo` (TANGIBLE/SERVICIO) en tabla `productos`

---

## 📂 Archivos de Migración

```
Legacy_Pharmacy_Backend/
├── MS-ventas/
│   ├── migration_ms_ventas.sql          ← Script para db_transacciones
│   └── src/main/resources/data.sql      ← Datos semilla (cliente genérico)
│
└── inventory-service/
    └── migration_inventory_service.sql  ← Script para MS Inventario
```

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Ejecución Manual con psql

#### 1. MS-Ventas (db_transacciones)

```bash
# Conectar a la base de datos
psql -U postgres -d db_transacciones

# Ejecutar script de migración
\i /ruta/completa/MS-ventas/migration_ms_ventas.sql

# Verificar cambios
\d detalle_ventas
SELECT * FROM clientes WHERE id = 1;
```

#### 2. Inventory Service

```bash
# Conectar a la base de datos
psql -U postgres -d nombre_bd_inventario

# Ejecutar script de migración
\i /ruta/completa/inventory-service/migration_inventory_service.sql

# Verificar cambios
\d productos
SELECT tipo, COUNT(*) FROM productos GROUP BY tipo;
```

### Opción 2: Ejecución desde Línea de Comandos

#### MS-Ventas

```bash
psql -U postgres -d db_transacciones -f MS-ventas/migration_ms_ventas.sql
```

#### Inventory Service

```bash
psql -U postgres -d nombre_bd_inventario -f inventory-service/migration_inventory_service.sql
```

### Opción 3: Usando DBeaver / PgAdmin

1. Abrir DBeaver o PgAdmin
2. Conectarse a la base de datos correspondiente
3. Abrir el archivo `.sql`
4. Ejecutar el script completo
5. Verificar en la pestaña de resultados

---

## ⚠️ Notas Importantes

### Antes de Ejecutar

1. **Hacer Backup** de las bases de datos:
   ```bash
   pg_dump -U postgres db_transacciones > backup_ventas_$(date +%Y%m%d).sql
   pg_dump -U postgres nombre_bd_inventario > backup_inventario_$(date +%Y%m%d).sql
   ```

2. **Verificar Conexión**:
   ```bash
   psql -U postgres -d db_transacciones -c "SELECT version();"
   ```

3. **Verificar Permisos**: El usuario debe tener permisos para `ALTER TABLE` e `INSERT`

### Scripts Idempotentes

Los scripts están diseñados para ser **idempotentes**, es decir:
- Se pueden ejecutar múltiples veces sin errores
- Usan `IF NOT EXISTS` y `ON CONFLICT DO NOTHING`
- No duplican datos ni columnas

### Orden de Ejecución

```
1. ✅ Inventory Service (primero)
2. ✅ MS-Ventas (segundo)
```

**Importante**: Ejecutar Inventory Service primero porque MS-Ventas consume sus datos.

---

## ✅ Verificación Post-Migración

### MS-Ventas

```sql
-- 1. Verificar columna es_venta_por_caja
SELECT column_name, data_type, column_default 
FROM information_schema.columns 
WHERE table_name = 'detalle_ventas' 
  AND column_name = 'es_venta_por_caja';

-- 2. Verificar cliente genérico
SELECT id, nombre, numero_documento, tipo_documento 
FROM clientes 
WHERE id = 1;

-- RESULTADO ESPERADO:
-- id | nombre                           | numero_documento | tipo_documento
-- 1  | Cliente Mostrador / Cuantía Menor| 222222222222     | CC
```

### Inventory Service

```sql
-- 1. Verificar campos nuevos
SELECT column_name, data_type, column_default, is_nullable
FROM information_schema.columns 
WHERE table_name = 'productos' 
  AND column_name IN ('unidades_por_blister', 'tipo');

-- 2. Verificar distribución de tipos
SELECT tipo, COUNT(*) as cantidad
FROM productos
GROUP BY tipo;

-- RESULTADO ESPERADO:
-- tipo      | cantidad
-- TANGIBLE  | [número de productos existentes]
-- SERVICIO  | 0 (o más si creaste servicios)
```

---

## 🐛 Solución de Problemas

### Error: "relation already exists"
**Solución**: Normal, el script es idempotente. Continuar.

### Error: "permission denied"
**Solución**: 
```sql
GRANT ALL ON TABLE productos TO tu_usuario;
GRANT ALL ON TABLE detalle_ventas TO tu_usuario;
```

### Error: "column already exists"
**Solución**: Normal, la columna ya fue creada. Continuar.

### El cliente genérico no se crea
**Verificar**:
```sql
-- Ver si hay conflicto con ID 1
SELECT * FROM clientes WHERE id = 1;

-- Si existe otro cliente con ID 1, cambiar en:
-- - migration_ms_ventas.sql
-- - data.sql
-- - application.properties (ventas.cliente-generico-id)
```

---

## 🔄 Rollback (Emergencia)

### MS-Ventas

```sql
-- ADVERTENCIA: Esto elimina datos
ALTER TABLE detalle_ventas DROP COLUMN IF EXISTS es_venta_por_caja;
DELETE FROM clientes WHERE id = 1;
```

### Inventory Service

```sql
-- ADVERTENCIA: Esto elimina datos
ALTER TABLE productos DROP COLUMN IF EXISTS tipo;
ALTER TABLE productos DROP COLUMN IF EXISTS unidades_por_blister;
```

---

## 📞 Soporte

Si encuentras problemas durante la migración:

1. Verificar logs de PostgreSQL
2. Revisar permisos de usuario
3. Confirmar versión de PostgreSQL (recomendado: 12+)
4. Verificar que las tablas base existen

---

## ✨ Próximos Pasos

Después de ejecutar las migraciones:

1. ✅ Reiniciar microservicios
2. ✅ Verificar endpoints con Postman
3. ✅ Probar flujo completo de venta
4. ✅ Validar regla de medicamentos controlados

---

**Fecha**: 2026-01-30  
**Versión**: 1.0  
**Autor**: Sistema de Gestión de Farmacia
