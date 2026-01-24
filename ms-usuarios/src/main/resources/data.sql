-- Insertar roles predeterminados si no existen
INSERT INTO roles (nombre, descripcion)
SELECT 'ADMINISTRADOR', 'Usuario con acceso completo al sistema'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMINISTRADOR');

INSERT INTO roles (nombre, descripcion)
SELECT 'VENDEDOR', 'Usuario con acceso para realizar ventas'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'VENDEDOR');

INSERT INTO roles (nombre, descripcion)
SELECT 'SISTEMA', 'Usuario del sistema para operaciones automatizadas'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'SISTEMA');

-- Usuario administrador por defecto (password: Admin123!)
-- Solo se crea si no existe ningún usuario
INSERT INTO usuarios (nombre_completo, cedula, login, password_hash, rol_id, estado, intentos_fallidos, fecha_creacion, fecha_actualizacion)
SELECT
    'Administrador del Sistema',
    '1234567890',
    'admin',
    '$2a$10$2.lYAVbuxi/XDW/FxmGfD.NQdZRTU9KKLFqNgtaaBvSriGGC9Hawi',
    (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'),
    'ACTIVO',
    0,
    NOW(),
    NOW()
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE login = 'admin');