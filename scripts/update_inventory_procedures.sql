-- ACTUALIZACIÓN DE PROCEDIMIENTOS ALMACENADOS (Lógica Fraccionada)
-- Ejecutar en Aiven MySQL

DELIMITER //

-- =============================================
-- 1. REGISTRAR ENTRADA (COMPRA)
-- Recibe CAJAS, convierte a UNIDADES, calcula COSTO UNITARIO
-- =============================================
DROP PROCEDURE IF EXISTS registrar_entrada_mercancia //

CREATE PROCEDURE registrar_entrada_mercancia(
    IN p_producto_id INT,
    IN p_numero_lote VARCHAR(50),
    IN p_cantidad_comprada INT,  -- Cantidad en CAJAS (o unidades si unidades_por_caja=1)
    IN p_costo_compra DECIMAL(10, 2), -- Costo TOTAL de la compra
    IN p_fecha_vencimiento DATE,
    IN p_usuario VARCHAR(50),
    IN p_sucursal_id INT,
    IN p_observaciones TEXT
)
BEGIN
    DECLARE v_unidades_por_caja INT DEFAULT 1;
    DECLARE v_cantidad_real INT;
    DECLARE v_costo_unitario DECIMAL(18, 4);

    -- 1. Obtener factor de conversión del producto
    SELECT COALESCE(unidades_por_caja, 1) INTO v_unidades_por_caja
    FROM productos 
    WHERE id = p_producto_id;

    -- 2. Calcular cantidad real en unidades mínimas
    SET v_cantidad_real = p_cantidad_comprada * v_unidades_por_caja;

    -- 3. Calcular costo unitario (Costo Total / Unidades Totales)
    -- Evitar división por cero
    IF v_cantidad_real > 0 THEN
        SET v_costo_unitario = p_costo_compra / v_cantidad_real;
    ELSE
        SET v_costo_unitario = 0;
    END IF;

    -- 4. Insertar el Lote con las UNIDADES y COSTO UNITARIO
    -- Se corrigió para coincidir con la entidad (solo cantidad_actual)
    INSERT INTO lotes (
        producto_id, 
        numero_lote, 
        fecha_vencimiento, 
        cantidad_actual,  -- Usamos este valor como inicial también
        costo_compra, -- Guardamos el costo UNITARIO calculado
        sucursal_id
    ) VALUES (
        p_producto_id, 
        p_numero_lote, 
        p_fecha_vencimiento, 
        v_cantidad_real, -- Cantidad Actual = Cantidad Inicial calculada 
        v_costo_unitario, 
        p_sucursal_id
    );

    -- 5. Registrar Movimiento de Entrada
    INSERT INTO movimientos (
        lote_id, 
        tipo_movimiento, 
        cantidad, 
        usuario_responsable, 
        sucursal_id, 
        observaciones
    ) VALUES (
        LAST_INSERT_ID(), 
        'ENTRADA', 
        v_cantidad_real, 
        p_usuario, 
        p_sucursal_id, 
        p_observaciones
    );

    -- Retornar resultado
    SELECT 'OK' as estado, 'Lote registrado correctamente' as mensaje, 
           v_cantidad_real as cantidad_unidades, v_costo_unitario as costo_unitario;
END //

-- =============================================
-- 2. REGISTRAR SALIDA (VENTA)
-- Soporta flag para descontar por CAJA o UNIDAD
-- =============================================
DROP PROCEDURE IF EXISTS registrar_salida_mercancia //

CREATE PROCEDURE registrar_salida_mercancia(
    IN p_producto_id INT,
    IN p_cantidad INT, -- Cantidad solicitada (Cajas o Unidades según flag)
    IN p_usuario VARCHAR(50),
    IN p_sucursal_id INT,
    IN p_venta_id INT,
    IN p_observaciones TEXT,
    IN p_es_venta_por_caja BOOLEAN -- NUEVO PARAMETRO
)
BEGIN
    DECLARE v_cantidad_restante INT;
    DECLARE v_lote_id INT;
    DECLARE v_cantidad_lote INT;
    DECLARE v_descuento INT;
    DECLARE v_unidades_por_caja INT DEFAULT 1;
    DECLARE v_cantidad_a_descontar_total INT;
    
    -- Cursor para iterar lotes FIFO/FEFO (Primero vence, primero sale)
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_lotes CURSOR FOR 
        SELECT id, cantidad_actual 
        FROM lotes 
        WHERE producto_id = p_producto_id 
          AND cantidad_actual > 0 
          AND fecha_vencimiento > CURDATE()
        ORDER BY fecha_vencimiento ASC;
        
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 1. Determinar cuántas UNIDADES realmente vamos a descontar
    IF p_es_venta_por_caja = TRUE THEN
        -- Buscar factor de conversión
        SELECT COALESCE(unidades_por_caja, 1) INTO v_unidades_por_caja
        FROM productos 
        WHERE id = p_producto_id;
        
        SET v_cantidad_a_descontar_total = p_cantidad * v_unidades_por_caja;
    ELSE
        -- Venta por unidad (o producto no fraccionable tratado como unidad)
        SET v_cantidad_a_descontar_total = p_cantidad;
    END IF;

    SET v_cantidad_restante = v_cantidad_a_descontar_total;

    -- 2. Validar Stock Total disponible antes de empezar
    IF (SELECT SUM(cantidad_actual) FROM lotes WHERE producto_id = p_producto_id) < v_cantidad_restante THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para realizar la venta';
    END IF;

    -- 3. Iterar lotes y descontar
    OPEN cur_lotes;

    read_loop: LOOP
        FETCH cur_lotes INTO v_lote_id, v_cantidad_lote;
        
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF v_cantidad_restante > 0 THEN
            IF v_cantidad_lote >= v_cantidad_restante THEN
                -- El lote alcanza para todo lo que falta
                SET v_descuento = v_cantidad_restante;
            ELSE
                -- El lote se agota, tomamos todo lo que tiene
                SET v_descuento = v_cantidad_lote;
            END IF;

            -- Actualizar Lote
            UPDATE lotes 
            SET cantidad_actual = cantidad_actual - v_descuento 
            WHERE id = v_lote_id;

            -- Registrar Movimiento
            INSERT INTO movimientos (
                lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, ref_venta_id, observaciones
            ) VALUES (
                v_lote_id, 'SALIDA', v_descuento, p_usuario, p_sucursal_id, p_venta_id, p_observaciones
            );

            SET v_cantidad_restante = v_cantidad_restante - v_descuento;
        ELSE
            LEAVE read_loop;
        END IF;
    END LOOP;

    CLOSE cur_lotes;

    -- Validación final
    IF v_cantidad_restante > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error de consistencia: No se pudo descontar el total requerido.';
    END IF;

    -- Retornar info de lotes afectados (simplificado)
    SELECT id as lote_id, v_cantidad_a_descontar_total as total_descontado FROM lotes WHERE producto_id = p_producto_id limit 1; 

END //

DELIMITER ;
