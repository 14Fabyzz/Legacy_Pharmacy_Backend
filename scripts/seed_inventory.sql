USE legacy03;

INSERT INTO categorias (nombre, descripcion, activa) 
SELECT 'Medicamentos', 'Categoria General', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Medicamentos');

INSERT INTO laboratorios (nombre, pais, activo)
SELECT 'Genfar', 'Colombia', true
WHERE NOT EXISTS (SELECT 1 FROM laboratorios WHERE nombre = 'Genfar');

INSERT INTO principios_activos (nombre, descripcion, activo)
SELECT 'Acetaminofen', 'Analgesico', true
WHERE NOT EXISTS (SELECT 1 FROM principios_activos WHERE nombre = 'Acetaminofen');
