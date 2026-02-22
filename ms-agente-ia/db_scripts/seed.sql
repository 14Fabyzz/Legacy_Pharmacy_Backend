-- Base de datos simulada para Agente 009
CREATE DATABASE IF NOT EXISTS legacy03;
USE legacy03;

-- 1. Tabla de Productos
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial VARCHAR(100) NOT NULL,
    principio_activo VARCHAR(100),
    laboratorio VARCHAR(50),
    precio_venta DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(50)
);

-- 2. Tabla de Lotes (Inventario)
CREATE TABLE IF NOT EXISTS lotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT,
    lote VARCHAR(20),
    fecha_vencimiento DATE,
    cantidad INT DEFAULT 0,
    ubicacion VARCHAR(20),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 3. Vista de Stock (REQUERIDA por Agente 009)
-- "v_stock_productos" con columnas: stock_total, nivel_stock
CREATE OR REPLACE VIEW v_stock_productos AS
SELECT 
    p.id,
    p.nombre_comercial,
    p.laboratorio,
    p.precio_venta,
    COALESCE(SUM(l.cantidad), 0) as stock_total,
    CASE 
        WHEN COALESCE(SUM(l.cantidad), 0) = 0 THEN 'SIN_STOCK'
        WHEN COALESCE(SUM(l.cantidad), 0) < 10 THEN 'BAJO'
        ELSE 'OK'
    END as nivel_stock
FROM productos p
LEFT JOIN lotes l ON p.id = l.producto_id
GROUP BY p.id, p.nombre_comercial, p.laboratorio, p.precio_venta;

-- 4. Vista de Vencimientos (REQUERIDA por Agente 009)
-- "v_semaforo_vencimientos" con columnas: dias_restantes, color_alerta, accion_sugerida
CREATE OR REPLACE VIEW v_semaforo_vencimientos AS
SELECT 
    l.id,
    p.nombre_comercial,
    l.lote,
    l.fecha_vencimiento,
    DATEDIFF(l.fecha_vencimiento, CURDATE()) as dias_restantes,
    CASE 
        WHEN DATEDIFF(l.fecha_vencimiento, CURDATE()) < 30 THEN 'ROJO'
        WHEN DATEDIFF(l.fecha_vencimiento, CURDATE()) < 90 THEN 'AMARILLO'
        ELSE 'VERDE'
    END as color_alerta,
    CASE 
        WHEN DATEDIFF(l.fecha_vencimiento, CURDATE()) < 30 THEN 'RETIRAR INMEDIATAMENTE'
        WHEN DATEDIFF(l.fecha_vencimiento, CURDATE()) < 90 THEN 'PONER EN OFERTA'
        ELSE 'NORMAL'
    END as accion_sugerida
FROM lotes l
JOIN productos p ON l.producto_id = p.id
WHERE l.cantidad > 0;

-- 5. Datos de Prueba (Seed Data)
INSERT INTO productos (nombre_comercial, principio_activo, laboratorio, precio_venta, categoria) VALUES
('Dolex 500mg', 'Acetaminofén', 'GSK', 2500.00, 'Analgésico'),
('Advil Max', 'Ibuprofeno', 'Pfizer', 3200.50, 'Analgésico'),
('Amoxidal 875', 'Amoxicilina', 'Roemmers', 15000.00, 'Antibiótico'),
('Aspirina 100', 'Ácido Acetilsalicílico', 'Bayer', 1200.00, 'Cardiovascular'),
('Gaviscon', 'Alginato de Sodio', 'Reckitt', 8500.00, 'Gastrointestinal');

INSERT INTO lotes (producto_id, lote, fecha_vencimiento, cantidad, ubicacion) VALUES
(1, 'L001', DATE_ADD(CURDATE(), INTERVAL 365 DAY), 100, 'ESTANTE-A1'),
(1, 'L002', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 5, 'ESTANTE-A1'), -- Vence pronto (ROJO)
(2, 'L003', DATE_ADD(CURDATE(), INTERVAL 60 DAY), 15, 'ESTANTE-A2'), -- Vence medio (AMARILLO)
(3, 'L004', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 50, 'BODEGA'),    -- Vence MUY pronto
(5, 'L005', DATE_ADD(CURDATE(), INTERVAL 500 DAY), 200, 'MOSTRADOR');
