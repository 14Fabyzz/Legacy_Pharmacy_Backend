-- ============================================================================
-- MICROSERVICIO INVENTARIO - LEGACY PHARMACY
-- BASE DE DATOS: legacy03
-- ============================================================================

-- 1. CONFIGURACIÓN INICIAL Y CREACIÓN DE BASE DE DATOS
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET TIME_ZONE = '+00:00';

-- ----------------------------------------------------------------------------
-- CRÍTICO: Creación y Selección de la Base de Datos 'legacy03'
-- ----------------------------------------------------------------------------
DROP DATABASE IF EXISTS legacy03;
CREATE DATABASE legacy03 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE legacy03;
-- ----------------------------------------------------------------------------

START TRANSACTION;

-- ============================================================================
-- PARTE 1: TABLAS PRINCIPALES
-- ============================================================================

-- TABLA: CATEGORIAS
CREATE TABLE IF NOT EXISTS categorias (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activa TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_categoria_nombre_no_vacio CHECK (TRIM(nombre) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_categorias_activa ON categorias(activa);

-- TABLA: LABORATORIOS
CREATE TABLE IF NOT EXISTS laboratorios (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            nombre VARCHAR(150) NOT NULL,
    pais VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    activo TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_laboratorio_nombre_pais (nombre, pais),
    CONSTRAINT chk_laboratorio_nombre_no_vacio CHECK (TRIM(nombre) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_laboratorios_activo ON laboratorios(activo);
CREATE INDEX idx_laboratorios_nombre ON laboratorios(nombre);

-- TABLA: PRINCIPIOS_ACTIVOS
CREATE TABLE IF NOT EXISTS principios_activos (
                                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                                  nombre VARCHAR(200) NOT NULL UNIQUE,
    descripcion TEXT,
    activo TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_principio_activo_nombre_no_vacio CHECK (TRIM(nombre) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_principios_activos_nombre ON principios_activos(nombre);
CREATE INDEX idx_principios_activos_activo ON principios_activos(activo);

-- TABLA: PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         codigo_interno VARCHAR(50) NOT NULL UNIQUE,
    codigo_barras VARCHAR(100) UNIQUE,
    nombre_comercial VARCHAR(200) NOT NULL,
    concentracion VARCHAR(100),
    presentacion VARCHAR(100),
    registro_invima VARCHAR(100),
    categoria_id INT NOT NULL,
    laboratorio_id INT NOT NULL,
    principio_activo_id INT,
    precio_compra_referencia DECIMAL(10, 2),
    precio_venta_base DECIMAL(10, 2) NOT NULL,
    iva_porcentaje DECIMAL(5, 2) DEFAULT 0.00,
    margen_minimo_porcentaje DECIMAL(5, 2) DEFAULT 40.00,
    stock_minimo INT DEFAULT 10,
    es_controlado TINYINT(1) DEFAULT 0,
    refrigerado TINYINT(1) DEFAULT 0,
    estado ENUM('ACTIVO', 'DESCONTINUADO', 'AGOTADO') DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    CONSTRAINT fk_producto_laboratorio FOREIGN KEY (laboratorio_id) REFERENCES laboratorios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_producto_principio_activo FOREIGN KEY (principio_activo_id) REFERENCES principios_activos(id) ON DELETE SET NULL,

    CONSTRAINT chk_producto_precio_venta_positivo CHECK (precio_venta_base > 0),
    CONSTRAINT chk_producto_stock_minimo CHECK (stock_minimo >= 0),
    CONSTRAINT chk_producto_iva CHECK (iva_porcentaje >= 0 AND iva_porcentaje <= 100),
    CONSTRAINT chk_producto_margen_minimo CHECK (margen_minimo_porcentaje >= 0),
    CONSTRAINT chk_producto_nombre_no_vacio CHECK (TRIM(nombre_comercial) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_productos_nombre ON productos(nombre_comercial);
CREATE INDEX idx_productos_codigo_barras ON productos(codigo_barras);
CREATE INDEX idx_productos_codigo_interno ON productos(codigo_interno);
CREATE INDEX idx_productos_principio_activo ON productos(principio_activo_id);
CREATE INDEX idx_productos_categoria ON productos(categoria_id);
CREATE INDEX idx_productos_laboratorio ON productos(laboratorio_id);
CREATE INDEX idx_productos_estado ON productos(estado);

-- TABLA: LOTES
CREATE TABLE IF NOT EXISTS lotes (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     producto_id INT NOT NULL,
                                     numero_lote VARCHAR(50) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    cantidad_actual INT NOT NULL DEFAULT 0,
    costo_compra DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_lote_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_producto_lote (producto_id, numero_lote),
    CONSTRAINT chk_lote_cantidad_positiva CHECK (cantidad_actual >= 0),
    CONSTRAINT chk_lote_costo_positivo CHECK (costo_compra > 0),
    CONSTRAINT chk_lote_fecha_vencimiento CHECK (fecha_vencimiento > '2020-01-01')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_lotes_producto ON lotes(producto_id);
CREATE INDEX idx_lotes_vencimiento ON lotes(fecha_vencimiento);
CREATE INDEX idx_lotes_cantidad ON lotes(cantidad_actual);

-- TABLA: SUCURSALES
CREATE TABLE IF NOT EXISTS sucursales (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          nombre VARCHAR(150) NOT NULL UNIQUE,
    ciudad VARCHAR(100) NOT NULL,
    direccion TEXT,
    activa TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_sucursal_nombre_no_vacio CHECK (TRIM(nombre) != ''),
    CONSTRAINT chk_sucursal_ciudad_no_vacio CHECK (TRIM(ciudad) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sucursales_activa ON sucursales(activa);
CREATE INDEX idx_sucursales_ciudad ON sucursales(ciudad);

-- TABLA: MOVIMIENTOS
CREATE TABLE IF NOT EXISTS movimientos (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           lote_id INT NOT NULL,
                                           tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'AJUSTE', 'TRASLADO', 'VENCIDO', 'DEVOLUCION') NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_responsable VARCHAR(100) NOT NULL,
    venta_id INT,
    sucursal_id INT,
    observaciones TEXT,

    CONSTRAINT fk_movimiento_lote FOREIGN KEY (lote_id) REFERENCES lotes(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimiento_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE SET NULL,
    CONSTRAINT chk_movimiento_cantidad CHECK (cantidad != 0),
    CONSTRAINT chk_movimiento_usuario_no_vacio CHECK (TRIM(usuario_responsable) != '')
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_movimientos_lote ON movimientos(lote_id);
CREATE INDEX idx_movimientos_tipo ON movimientos(tipo_movimiento);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha_movimiento);

-- TABLA: HISTORIAL_PRECIOS
CREATE TABLE IF NOT EXISTS historial_precios (
                                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                                 producto_id INT NOT NULL,
                                                 precio_anterior DECIMAL(10, 2) NOT NULL,
    precio_nuevo DECIMAL(10, 2) NOT NULL,
    costo_promedio_momento DECIMAL(10, 2),
    margen_anterior DECIMAL(5, 2),
    margen_nuevo DECIMAL(5, 2),
    motivo TEXT,
    usuario_responsable VARCHAR(100) NOT NULL,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historial_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT chk_historial_precios_positivos CHECK (precio_anterior > 0 AND precio_nuevo > 0)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_historial_precios_producto ON historial_precios(producto_id);

-- ============================================================================
-- PARTE 2: TRIGGERS
-- ============================================================================

DELIMITER $$

-- Trigger: Actualizar precio referencia
CREATE TRIGGER trg_actualizar_precio_referencia
    AFTER INSERT ON lotes
    FOR EACH ROW
BEGIN
    UPDATE productos
    SET precio_compra_referencia = NEW.costo_compra
    WHERE id = NEW.producto_id;
    END$$

    -- Trigger: Validar cantidad disponible
    CREATE TRIGGER trg_validar_cantidad_disponible
        BEFORE INSERT ON movimientos
        FOR EACH ROW
    BEGIN
        DECLARE v_cantidad_actual INT;
    IF NEW.cantidad < 0 THEN
        SELECT cantidad_actual INTO v_cantidad_actual
        FROM lotes WHERE id = NEW.lote_id;

        IF v_cantidad_actual + NEW.cantidad < 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Stock insuficiente para realizar la operación';
    END IF;
END IF;
END$$

-- Trigger: Actualizar cantidad en lote
CREATE TRIGGER trg_actualizar_cantidad_lote
    AFTER INSERT ON movimientos
    FOR EACH ROW
BEGIN
    UPDATE lotes
    SET cantidad_actual = cantidad_actual + NEW.cantidad
    WHERE id = NEW.lote_id;
    END$$

    DELIMITER ;

-- ============================================================================
-- PARTE 3: PROCEDIMIENTOS ALMACENADOS
-- ============================================================================

DELIMITER $$

    -- Procedimiento: Actualizar Precio
    CREATE PROCEDURE actualizar_precio_producto(
        IN p_producto_id INT, IN p_precio_nuevo DECIMAL(10, 2), IN p_usuario VARCHAR(100), IN p_motivo TEXT
    )
    BEGIN
    DECLARE v_precio_anterior DECIMAL(10, 2);
    DECLARE v_costo_promedio DECIMAL(10, 2);
    DECLARE v_margen_anterior DECIMAL(5, 2);
    DECLARE v_margen_nuevo DECIMAL(5, 2);

    IF p_precio_nuevo <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El precio debe ser mayor a cero';
END IF;

SELECT p.precio_venta_base, IFNULL((SELECT SUM(l.costo_compra * l.cantidad_actual) / SUM(l.cantidad_actual) FROM lotes l WHERE l.producto_id = p.id AND l.cantidad_actual > 0), p.precio_compra_referencia)
INTO v_precio_anterior, v_costo_promedio
FROM productos p WHERE p.id = p_producto_id;

IF v_costo_promedio IS NOT NULL AND v_costo_promedio > 0 THEN
        SET v_margen_anterior = ((v_precio_anterior - v_costo_promedio) / v_costo_promedio) * 100;
        SET v_margen_nuevo = ((p_precio_nuevo - v_costo_promedio) / v_costo_promedio) * 100;
END IF;

UPDATE productos SET precio_venta_base = p_precio_nuevo WHERE id = p_producto_id;

INSERT INTO historial_precios (producto_id, precio_anterior, precio_nuevo, costo_promedio_momento, margen_anterior, margen_nuevo, motivo, usuario_responsable)
VALUES (p_producto_id, v_precio_anterior, p_precio_nuevo, v_costo_promedio, v_margen_anterior, v_margen_nuevo, IFNULL(p_motivo, 'Ajuste de precio'), p_usuario);

SELECT 'Precio actualizado exitosamente' AS mensaje;
END$$

-- Procedimiento: Entrada Mercancía
CREATE PROCEDURE registrar_entrada_mercancia(
    IN p_producto_id INT, IN p_numero_lote VARCHAR(50), IN p_cantidad INT, IN p_costo_compra DECIMAL(10, 2),
    IN p_fecha_vencimiento DATE, IN p_usuario VARCHAR(100), IN p_sucursal_id INT, IN p_observaciones TEXT
)
BEGIN
    DECLARE v_lote_id INT;
    DECLARE v_lote_existe INT;

    IF p_cantidad <= 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cantidad debe ser mayor a cero'; END IF;
    IF p_costo_compra <= 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El costo de compra debe ser mayor a cero'; END IF;

SELECT COUNT(*), MAX(id) INTO v_lote_existe, v_lote_id
FROM lotes WHERE producto_id = p_producto_id AND numero_lote = p_numero_lote;

IF v_lote_existe > 0 THEN
UPDATE lotes SET cantidad_actual = cantidad_actual + p_cantidad WHERE id = v_lote_id;
ELSE
        INSERT INTO lotes (producto_id, numero_lote, fecha_vencimiento, cantidad_actual, costo_compra)
        VALUES (p_producto_id, p_numero_lote, p_fecha_vencimiento, p_cantidad, p_costo_compra);
        SET v_lote_id = LAST_INSERT_ID();
END IF;

INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, observaciones)
VALUES (v_lote_id, 'ENTRADA', p_cantidad, p_usuario, p_sucursal_id, IFNULL(p_observaciones, 'Entrada de mercancía - Compra'));

SELECT v_lote_id AS lote_id, 'Entrada registrada exitosamente' AS mensaje;
END$$

-- Procedimiento: Salida Mercancía
CREATE PROCEDURE registrar_salida_mercancia(
    IN p_producto_id INT, IN p_cantidad INT, IN p_usuario VARCHAR(100),
    IN p_sucursal_id INT, IN p_venta_id INT, IN p_observaciones TEXT
)
BEGIN
    DECLARE v_cantidad_pendiente INT DEFAULT p_cantidad;
    DECLARE v_lote_id INT;
    DECLARE v_numero_lote VARCHAR(50);
    DECLARE v_cantidad_lote INT;
    DECLARE v_costo_unitario DECIMAL(10, 2);
    DECLARE v_cantidad_a_descontar INT;
    DECLARE done INT DEFAULT FALSE;

    DECLARE cur_lotes CURSOR FOR
SELECT id, numero_lote, cantidad_actual, costo_compra FROM lotes
WHERE producto_id = p_producto_id AND cantidad_actual > 0
ORDER BY fecha_vencimiento ASC, created_at ASC;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF p_cantidad <= 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cantidad debe ser mayor a cero'; END IF;
    IF (SELECT IFNULL(SUM(cantidad_actual), 0) FROM lotes WHERE producto_id = p_producto_id) < p_cantidad THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para el producto';
END IF;

    CREATE TEMPORARY TABLE IF NOT EXISTS tmp_salida (lote_id INT, numero_lote VARCHAR(50), cantidad_vendida INT, costo_unitario DECIMAL(10, 2), subtotal DECIMAL(10, 2));
TRUNCATE TABLE tmp_salida;

OPEN cur_lotes;
read_loop: LOOP
        FETCH cur_lotes INTO v_lote_id, v_numero_lote, v_cantidad_lote, v_costo_unitario;
        IF done OR v_cantidad_pendiente = 0 THEN LEAVE read_loop; END IF;

        SET v_cantidad_a_descontar = LEAST(v_cantidad_lote, v_cantidad_pendiente);

INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, venta_id, observaciones)
VALUES (v_lote_id, 'SALIDA', -v_cantidad_a_descontar, p_usuario, p_sucursal_id, p_venta_id, IFNULL(p_observaciones, 'Venta'));

INSERT INTO tmp_salida VALUES (v_lote_id, v_numero_lote, v_cantidad_a_descontar, v_costo_unitario, v_cantidad_a_descontar * v_costo_unitario);
SET v_cantidad_pendiente = v_cantidad_pendiente - v_cantidad_a_descontar;
END LOOP;
CLOSE cur_lotes;

SELECT * FROM tmp_salida;
DROP TEMPORARY TABLE IF EXISTS tmp_salida;
END$$

-- Procedimiento: Ajustar Inventario
CREATE PROCEDURE ajustar_inventario(IN p_lote_id INT, IN p_cantidad_nueva INT, IN p_usuario VARCHAR(100), IN p_motivo TEXT)
BEGIN
    DECLARE v_cantidad_actual INT;
    DECLARE v_diferencia INT;
SELECT cantidad_actual INTO v_cantidad_actual FROM lotes WHERE id = p_lote_id;

IF v_cantidad_actual IS NULL THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lote no encontrado'; END IF;
    SET v_diferencia = p_cantidad_nueva - v_cantidad_actual;

    IF v_diferencia != 0 THEN
        INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, observaciones)
        VALUES (p_lote_id, 'AJUSTE', v_diferencia, p_usuario, CONCAT('Ajuste de inventario: ', ABS(v_diferencia), ' unidades. Motivo: ', p_motivo));
SELECT 'Inventario ajustado exitosamente' AS mensaje;
ELSE
SELECT 'No hay cambios en el inventario' AS mensaje;
END IF;
END$$

-- Procedimiento: Reporte Rentabilidad
CREATE PROCEDURE reporte_rentabilidad_producto(IN p_producto_id INT, IN p_fecha_desde DATE, IN p_fecha_hasta DATE)
BEGIN
SELECT p.id AS producto_id, p.nombre_comercial AS nombre_producto, SUM(ABS(m.cantidad)) AS cantidad_vendida,
       ROUND(SUM(ABS(m.cantidad) * l.costo_compra), 2) AS costo_total,
       ROUND(SUM(ABS(m.cantidad) * p.precio_venta_base), 2) AS ingreso_total,
       ROUND(SUM(ABS(m.cantidad) * (p.precio_venta_base - l.costo_compra)), 2) AS ganancia_total,
       ROUND(AVG((p.precio_venta_base - l.costo_compra) / l.costo_compra * 100), 2) AS margen_promedio
FROM movimientos m JOIN lotes l ON m.lote_id = l.id JOIN productos p ON l.producto_id = p.id
WHERE m.tipo_movimiento = 'SALIDA'
  AND (p_producto_id IS NULL OR p.id = p_producto_id)
  AND (p_fecha_desde IS NULL OR DATE(m.fecha_movimiento) >= p_fecha_desde)
      AND (p_fecha_hasta IS NULL OR DATE(m.fecha_movimiento) <= p_fecha_hasta)
GROUP BY p.id, p.nombre_comercial ORDER BY ganancia_total DESC;
END$$

DELIMITER ;

-- ============================================================================
-- PARTE 4: VISTAS
-- ============================================================================

-- VISTA: Stock Consolidado
CREATE OR REPLACE VIEW v_stock_productos AS
SELECT p.id AS producto_id, p.codigo_interno, p.codigo_barras, p.nombre_comercial, pa.nombre AS principio_activo,
       p.concentracion, p.presentacion, l.nombre AS laboratorio, c.nombre AS categoria,
       IFNULL(SUM(lt.cantidad_actual), 0) AS stock_total, p.stock_minimo, p.precio_venta_base, p.estado,
       CASE WHEN IFNULL(SUM(lt.cantidad_actual), 0) = 0 THEN 'SIN_STOCK'
            WHEN IFNULL(SUM(lt.cantidad_actual), 0) <= p.stock_minimo THEN 'BAJO'
            ELSE 'OK' END AS nivel_stock
FROM productos p
         LEFT JOIN lotes lt ON p.id = lt.producto_id AND lt.cantidad_actual > 0
         LEFT JOIN laboratorios l ON p.laboratorio_id = l.id
         LEFT JOIN categorias c ON p.categoria_id = c.id
         LEFT JOIN principios_activos pa ON p.principio_activo_id = pa.id
WHERE p.estado = 'ACTIVO' GROUP BY p.id, l.nombre, c.nombre, pa.nombre;

-- VISTA: Próximos a Vencer
CREATE OR REPLACE VIEW v_productos_proximos_vencer AS
SELECT p.id AS producto_id, p.nombre_comercial, lt.numero_lote, lt.fecha_vencimiento, lt.cantidad_actual,
       DATEDIFF(lt.fecha_vencimiento, CURDATE()) AS dias_para_vencer,
       CASE WHEN lt.fecha_vencimiento < CURDATE() THEN 'VENCIDO'
            WHEN DATEDIFF(lt.fecha_vencimiento, CURDATE()) <= 90 THEN 'PROXIMO' ELSE 'OK' END AS estado_vencimiento
FROM lotes lt JOIN productos p ON lt.producto_id = p.id
WHERE lt.cantidad_actual > 0 AND p.estado = 'ACTIVO' ORDER BY lt.fecha_vencimiento ASC;

-- VISTA: Detalle Completo
CREATE OR REPLACE VIEW v_productos_detalle AS
SELECT p.id, p.codigo_interno, p.nombre_comercial, pa.nombre AS principio_activo, l.nombre AS laboratorio,
       IFNULL(SUM(lt.cantidad_actual), 0) AS stock_total, MIN(lt.fecha_vencimiento) AS proximo_vencimiento,
       p.precio_venta_base
FROM productos p
         LEFT JOIN lotes lt ON p.id = lt.producto_id AND lt.cantidad_actual > 0
         LEFT JOIN laboratorios l ON p.laboratorio_id = l.id
         LEFT JOIN principios_activos pa ON p.principio_activo_id = pa.id
GROUP BY p.id, l.nombre, pa.nombre;

-- VISTA: Movimientos Detalle
CREATE OR REPLACE VIEW v_movimientos_detalle AS
SELECT m.id, m.fecha_movimiento, m.tipo_movimiento, m.cantidad, p.nombre_comercial, lt.numero_lote, m.observaciones
FROM movimientos m JOIN lotes lt ON m.lote_id = lt.id JOIN productos p ON lt.producto_id = p.id
ORDER BY m.fecha_movimiento DESC;

-- VISTA: Control Precios
CREATE OR REPLACE VIEW v_control_precios AS
SELECT p.id, p.nombre_comercial, p.precio_venta_base AS precio_actual,
       ROUND(IFNULL((SELECT SUM(l2.costo_compra * l2.cantidad_actual) / SUM(l2.cantidad_actual) FROM lotes l2 WHERE l2.producto_id = p.id AND l2.cantidad_actual > 0), p.precio_compra_referencia), 2) AS costo_promedio,
       p.margen_minimo_porcentaje
FROM productos p WHERE p.estado = 'ACTIVO';

-- VISTA: Historial Precios
CREATE OR REPLACE VIEW v_historial_precios AS
SELECT h.id, p.nombre_comercial, h.precio_anterior, h.precio_nuevo, h.usuario_responsable, h.fecha_cambio
FROM historial_precios h JOIN productos p ON h.producto_id = p.id ORDER BY h.fecha_cambio DESC;

-- ============================================================================
-- PARTE 5: DATOS DE EJEMPLO
-- ============================================================================

INSERT INTO categorias (nombre, descripcion) VALUES
                                                 ('Analgésicos', 'Alivio del dolor'), ('Antibióticos', 'Infecciones'), ('Antigripales', 'Gripe'), ('Vitaminas', 'Suplementos'),
                                                 ('Antiinflamatorios', 'Inflamación');

INSERT INTO laboratorios (nombre, pais) VALUES
                                            ('Genfar', 'Colombia'), ('Tecnoquímicas', 'Colombia'), ('Bayer', 'Alemania'), ('Pfizer', 'EEUU');

INSERT INTO principios_activos (nombre) VALUES
                                            ('Acetaminofén'), ('Ibuprofeno'), ('Amoxicilina'), ('Loratadina'), ('Vitamina C');

INSERT INTO productos (codigo_interno, codigo_barras, nombre_comercial, concentracion, presentacion, categoria_id, laboratorio_id, principio_activo_id, precio_compra_referencia, precio_venta_base, stock_minimo, registro_invima)
VALUES
    ('PROD-001', '77001', 'Dolex', '500mg', 'Caja x 20', 1, 1, 1, 4500, 8000, 50, 'INVIMA-001'),
    ('PROD-002', '77002', 'Advil', '400mg', 'Caja x 10', 5, 4, 2, 12000, 18000, 30, 'INVIMA-002'),
    ('PROD-003', '77003', 'Amoxal', '500mg', 'Caja x 21', 2, 1, 3, 15000, 25000, 20, 'INVIMA-003');

INSERT INTO sucursales (nombre, ciudad, direccion) VALUES ('Sucursal Centro', 'Bogotá', 'Cra 7 #12');

CALL registrar_entrada_mercancia(1, 'LOT-2024-001', 100, 4500, '2025-12-31', 'admin', 1, 'Compra inicial');
CALL registrar_entrada_mercancia(2, 'LOT-2024-002', 50, 12000, '2025-08-15', 'admin', 1, 'Compra');

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
SELECT 'INSTALACIÓN COMPLETADA EXITOSAMENTE EN BASE DE DATOS legacy03' AS Resultado;