USE vet_manada_woof;

-- ========================================
-- SP 1: GESTIONAR AGENDA (Crear/Actualizar)
-- ========================================
DROP PROCEDURE IF EXISTS sp_gestionar_agenda;
DELIMITER $$

CREATE PROCEDURE sp_gestionar_agenda(
    IN p_accion VARCHAR(10),
    IN p_id_agenda BIGINT,
    IN p_id_cliente BIGINT,
    IN p_id_mascota BIGINT,
    IN p_id_medio_solicitud INT,
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_duracion_estimada_min INT,
    IN p_id_estado INT,
    IN p_abono_inicial DECIMAL(10,2),
    IN p_observaciones VARCHAR(256),
    OUT p_id_resultado BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_existe_cita INT DEFAULT 0;
    DECLARE v_nuevo_codigo VARCHAR(16);
    DECLARE v_estado_actual INT;
    
    SET p_accion = UPPER(TRIM(p_accion));
    
    -- ========================================
    -- VALIDACIONES GENERALES
    -- ========================================
    IF p_accion NOT IN ('CREAR', 'ACTUALIZAR') THEN
        SET p_mensaje = 'ERROR: Acción no válida. Use "CREAR" o "ACTUALIZAR".';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM clientes WHERE id = p_id_cliente) THEN
        SET p_mensaje = 'ERROR: Cliente no existe.';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
        LEAVE main_block;
    END IF;
    
    IF p_id_medio_solicitud IS NOT NULL AND NOT EXISTS (SELECT 1 FROM medio_solicitud WHERE id = p_id_medio_solicitud) THEN
        SET p_mensaje = 'ERROR: Medio de solicitud no existe.';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM estado_agenda WHERE id = p_id_estado) THEN
        SET p_mensaje = 'ERROR: Estado de agenda no existe.';
        LEAVE main_block;
    END IF;
    
    IF p_fecha < CURDATE() THEN
        SET p_mensaje = 'ERROR: La fecha no puede ser anterior a hoy.';
        LEAVE main_block;
    END IF;
    
    IF p_duracion_estimada_min < 0 THEN
        SET p_mensaje = 'ERROR: La duración no puede ser negativa.';
        LEAVE main_block;
    END IF;
    
    IF p_abono_inicial < 0 THEN
        SET p_mensaje = 'ERROR: El abono no puede ser negativo.';
        LEAVE main_block;
    END IF;
    
    -- ========================================
    -- CREAR NUEVA CITA
    -- ========================================
    IF p_accion = 'CREAR' THEN
        
        -- Validar duplicados: misma mascota, misma fecha y hora
        SELECT COUNT(*) INTO v_existe_cita
        FROM agenda
        WHERE id_mascota = p_id_mascota
          AND fecha = p_fecha
          AND hora = p_hora
          AND id_estado NOT IN (4, 6); -- Excluir CANCELADA y NO ASISTIÓ
        
        IF v_existe_cita > 0 THEN
            SET p_mensaje = 'ERROR: Ya existe una cita para esta mascota en la misma fecha y hora.';
            LEAVE main_block;
        END IF;
        
        -- Generar código único usando UUID (más seguro que MAX(id))
        SET v_nuevo_codigo = CONCAT('AG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        -- Verificar que el código no exista (por si acaso)
        WHILE EXISTS (SELECT 1 FROM agenda WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('AG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO agenda (
            codigo,
            id_cliente,
            id_mascota,
            id_medio_solicitud,
            fecha,
            hora,
            duracion_estimada_min,
            abono_inicial,
            total_cita,
            id_estado,
            observaciones
        ) VALUES (
            v_nuevo_codigo,
            p_id_cliente,
            p_id_mascota,
            p_id_medio_solicitud,
            p_fecha,
            p_hora,
            p_duracion_estimada_min,
            p_abono_inicial,
            0, -- Se actualizará al agregar servicios
            p_id_estado,
            p_observaciones
        );
        
        SET p_id_resultado = LAST_INSERT_ID();
        SET p_codigo = v_nuevo_codigo;
        SET p_mensaje = CONCAT('Cita ', v_nuevo_codigo, ' creada exitosamente.');
    
    -- ========================================
    -- ACTUALIZAR CITA EXISTENTE
    -- ========================================
    ELSEIF p_accion = 'ACTUALIZAR' THEN
        
        IF p_id_agenda IS NULL THEN
            SET p_mensaje = 'ERROR: Debe proporcionar el ID de la cita a actualizar.';
            LEAVE main_block;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
            SET p_mensaje = 'ERROR: Cita no existe.';
            LEAVE main_block;
        END IF;
        
        -- Obtener estado actual
        SELECT id_estado INTO v_estado_actual FROM agenda WHERE id = p_id_agenda;
        
        -- No permitir actualizar citas canceladas o ya atendidas
        IF v_estado_actual IN (4, 5) THEN -- CANCELADA, ATENDIDA
            SET p_mensaje = 'ERROR: No se puede actualizar una cita cancelada o atendida.';
            LEAVE main_block;
        END IF;
        
        -- Validar duplicados: misma mascota, misma fecha y hora (excluyendo la cita actual)
        SELECT COUNT(*) INTO v_existe_cita
        FROM agenda
        WHERE id_mascota = p_id_mascota
          AND fecha = p_fecha
          AND hora = p_hora
          AND id != p_id_agenda
          AND id_estado NOT IN (4, 6);
        
        IF v_existe_cita > 0 THEN
            SET p_mensaje = 'ERROR: Ya existe otra cita para esta mascota en la misma fecha y hora.';
            LEAVE main_block;
        END IF;
        
        UPDATE agenda SET
            id_cliente = p_id_cliente,
            id_mascota = p_id_mascota,
            id_medio_solicitud = p_id_medio_solicitud,
            fecha = p_fecha,
            hora = p_hora,
            duracion_estimada_min = p_duracion_estimada_min,
            id_estado = p_id_estado,
            observaciones = p_observaciones
        WHERE id = p_id_agenda;
        
        SELECT codigo INTO p_codigo FROM agenda WHERE id = p_id_agenda;
        SET p_id_resultado = p_id_agenda;
        SET p_mensaje = CONCAT('Cita ', p_codigo, ' actualizada exitosamente.');
    
    END IF;
    
END$$
DELIMITER ;

-- ========================================
-- SP 2: GESTIONAR INGRESO SERVICIO (Crear/Actualizar/Eliminar lógico)
-- ========================================
DROP PROCEDURE IF EXISTS sp_gestionar_ingreso_servicio;
DELIMITER $$

CREATE PROCEDURE sp_gestionar_ingreso_servicio(
    IN p_accion VARCHAR(10),
    IN p_id_ingreso BIGINT,
    IN p_id_agenda BIGINT,
    IN p_id_servicio INT,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_cantidad INT,
    IN p_duracion_min INT,
    IN p_valor_servicio DECIMAL(10,2),
    IN p_observaciones VARCHAR(128),
    OUT p_id_resultado BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_nuevo_total_cita DECIMAL(10,2),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    DECLARE v_estado_cita INT;
    DECLARE v_existe_servicio INT DEFAULT 0;
    
    SET p_accion = UPPER(TRIM(p_accion));
    
    -- ========================================
    -- VALIDACIONES GENERALES
    -- ========================================
    IF p_accion NOT IN ('CREAR', 'ACTUALIZAR', 'ELIMINAR') THEN
        SET p_mensaje = 'ERROR: Acción no válida. Use "CREAR", "ACTUALIZAR" o "ELIMINAR".';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Cita no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar estado de la cita
    SELECT id_estado INTO v_estado_cita FROM agenda WHERE id = p_id_agenda;
    
    IF v_estado_cita IN (4, 5) THEN -- CANCELADA, ATENDIDA
        SET p_mensaje = 'ERROR: No se pueden agregar servicios a una cita cancelada o atendida.';
        LEAVE main_block;
    END IF;
    
    -- ========================================
    -- CREAR NUEVO SERVICIO
    -- ========================================
    IF p_accion = 'CREAR' THEN
        
        IF NOT EXISTS (SELECT 1 FROM servicios WHERE id = p_id_servicio) THEN
            SET p_mensaje = 'ERROR: Servicio no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_colaborador IS NOT NULL AND NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
            SET p_mensaje = 'ERROR: Colaborador no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_veterinario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM veterinarios WHERE id = p_id_veterinario) THEN
            SET p_mensaje = 'ERROR: Veterinario no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_colaborador IS NOT NULL AND p_id_veterinario IS NOT NULL THEN
            SET p_mensaje = 'ERROR: No puede asignar colaborador y veterinario al mismo tiempo.';
            LEAVE main_block;
        END IF;
        
        IF p_cantidad <= 0 THEN
            SET p_mensaje = 'ERROR: La cantidad debe ser mayor a 0.';
            LEAVE main_block;
        END IF;
        
        IF p_valor_servicio <= 0 THEN
            SET p_mensaje = 'ERROR: El valor del servicio debe ser mayor a 0.';
            LEAVE main_block;
        END IF;
        
        -- Validar duplicados: mismo servicio en la misma cita (opcional, depende de tu lógica)
        -- Si quieres permitir duplicados, comenta esto
        SELECT COUNT(*) INTO v_existe_servicio
        FROM ingresos_servicios
        WHERE id_agenda = p_id_agenda
          AND id_servicio = p_id_servicio;
        
        IF v_existe_servicio > 0 THEN
            SET p_mensaje = 'ERROR: Este servicio ya fue agregado a la cita. Use ACTUALIZAR para modificarlo.';
            LEAVE main_block;
        END IF;
        
        -- Generar código único
        SET v_nuevo_codigo = CONCAT('IS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        WHILE EXISTS (SELECT 1 FROM ingresos_servicios WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('IS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO ingresos_servicios (
            codigo,
            id_agenda,
            id_servicio,
            id_colaborador,
            id_veterinario,
            cantidad,
            duracion_min,
            valor_servicio,
            observaciones
        ) VALUES (
            v_nuevo_codigo,
            p_id_agenda,
            p_id_servicio,
            p_id_colaborador,
            p_id_veterinario,
            p_cantidad,
            p_duracion_min,
            p_valor_servicio,
            p_observaciones
        );
        
        SET p_id_resultado = LAST_INSERT_ID();
        SET p_codigo = v_nuevo_codigo;
        
        -- Actualizar total de la cita
        UPDATE agenda SET
            total_cita = (
                SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                FROM ingresos_servicios
                WHERE id_agenda = p_id_agenda
            )
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        
        SET p_mensaje = CONCAT('Servicio ', v_nuevo_codigo, ' agregado exitosamente.');
    
    -- ========================================
    -- ACTUALIZAR SERVICIO EXISTENTE
    -- ========================================
    ELSEIF p_accion = 'ACTUALIZAR' THEN
        
        IF p_id_ingreso IS NULL THEN
            SET p_mensaje = 'ERROR: Debe proporcionar el ID del servicio a actualizar.';
            LEAVE main_block;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM ingresos_servicios WHERE id = p_id_ingreso) THEN
            SET p_mensaje = 'ERROR: Servicio no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_colaborador IS NOT NULL AND NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
            SET p_mensaje = 'ERROR: Colaborador no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_veterinario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM veterinarios WHERE id = p_id_veterinario) THEN
            SET p_mensaje = 'ERROR: Veterinario no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_cantidad <= 0 THEN
            SET p_mensaje = 'ERROR: La cantidad debe ser mayor a 0.';
            LEAVE main_block;
        END IF;
        
        IF p_valor_servicio <= 0 THEN
            SET p_mensaje = 'ERROR: El valor del servicio debe ser mayor a 0.';
            LEAVE main_block;
        END IF;
        
        UPDATE ingresos_servicios SET
            id_colaborador = p_id_colaborador,
            id_veterinario = p_id_veterinario,
            cantidad = p_cantidad,
            duracion_min = p_duracion_min,
            valor_servicio = p_valor_servicio,
            observaciones = p_observaciones
        WHERE id = p_id_ingreso;
        
        SELECT codigo INTO p_codigo FROM ingresos_servicios WHERE id = p_id_ingreso;
        SET p_id_resultado = p_id_ingreso;
        
        -- Actualizar total de la cita
        UPDATE agenda SET
            total_cita = (
                SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                FROM ingresos_servicios
                WHERE id_agenda = p_id_agenda
            )
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        
        SET p_mensaje = CONCAT('Servicio ', p_codigo, ' actualizado exitosamente.');
    
    -- ========================================
    -- ELIMINAR SERVICIO (Elimina físicamente, ajusta según tu necesidad)
    -- ========================================
    ELSEIF p_accion = 'ELIMINAR' THEN
        
        IF p_id_ingreso IS NULL THEN
            SET p_mensaje = 'ERROR: Debe proporcionar el ID del servicio a eliminar.';
            LEAVE main_block;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM ingresos_servicios WHERE id = p_id_ingreso) THEN
            SET p_mensaje = 'ERROR: Servicio no existe.';
            LEAVE main_block;
        END IF;
        
        SELECT codigo INTO p_codigo FROM ingresos_servicios WHERE id = p_id_ingreso;
        
        DELETE FROM ingresos_servicios WHERE id = p_id_ingreso;
        
        SET p_id_resultado = p_id_ingreso;
        
        -- Actualizar total de la cita
        UPDATE agenda SET
            total_cita = (
                SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                FROM ingresos_servicios
                WHERE id_agenda = p_id_agenda
            )
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        
        SET p_mensaje = CONCAT('Servicio ', p_codigo, ' eliminado exitosamente.');
    
    END IF;
    
END$$
DELIMITER ;

-- ========================================
-- SP 3: REGISTRAR PAGO AGENDA (Crear/Eliminar)
-- ========================================
DROP PROCEDURE IF EXISTS sp_gestionar_pago_agenda;
DELIMITER $$

CREATE PROCEDURE sp_gestionar_pago_agenda(
    IN p_accion VARCHAR(10),
    IN p_id_pago BIGINT,
    IN p_id_agenda BIGINT,
    IN p_id_medio_pago INT,
    IN p_id_usuario INT,
    IN p_monto DECIMAL(10,2),
    IN p_observaciones VARCHAR(128),
    OUT p_id_resultado BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_total_abonado DECIMAL(10,2),
    OUT p_saldo_pendiente DECIMAL(10,2),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    DECLARE v_total_cita DECIMAL(10,2);
    DECLARE v_abono_actual DECIMAL(10,2);
    DECLARE v_nuevo_abono DECIMAL(10,2);
    
    SET p_accion = UPPER(TRIM(p_accion));
    
    -- ========================================
    -- VALIDACIONES GENERALES
    -- ========================================
    IF p_accion NOT IN ('CREAR', 'ELIMINAR') THEN
        SET p_mensaje = 'ERROR: Acción no válida. Use "CREAR" o "ELIMINAR".';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Cita no existe.';
        LEAVE main_block;
    END IF;
    
    -- ========================================
    -- CREAR NUEVO PAGO
    -- ========================================
    IF p_accion = 'CREAR' THEN
        
        IF NOT EXISTS (SELECT 1 FROM medios_pago WHERE id = p_id_medio_pago) THEN
            SET p_mensaje = 'ERROR: Medio de pago no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_id_usuario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id_usuario) THEN
            SET p_mensaje = 'ERROR: Usuario no existe.';
            LEAVE main_block;
        END IF;
        
        IF p_monto <= 0 THEN
            SET p_mensaje = 'ERROR: El monto debe ser mayor a 0.';
            LEAVE main_block;
        END IF;
        
        -- Obtener total y abono actual de la cita
        SELECT total_cita, abono_inicial INTO v_total_cita, v_abono_actual
        FROM agenda WHERE id = p_id_agenda;
        
        -- Calcular nuevo abono
        SET v_nuevo_abono = v_abono_actual + p_monto;
        
        -- Validar que no se pague más del total
        IF v_nuevo_abono > v_total_cita THEN
            SET p_mensaje = CONCAT('ERROR: El monto excede el total. Total: S/ ', v_total_cita, ', Ya abonado: S/ ', v_abono_actual);
            LEAVE main_block;
        END IF;
        
        -- Generar código único
        SET v_nuevo_codigo = CONCAT('PA-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        WHILE EXISTS (SELECT 1 FROM agenda_pagos WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('PA-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO agenda_pagos (
            codigo,
            id_agenda,
            id_medio_pago,
            id_usuario,
            monto,
            fecha_pago,
            observaciones
        ) VALUES (
            v_nuevo_codigo,
            p_id_agenda,
            p_id_medio_pago,
            p_id_usuario,
            p_monto,
            NOW(),
            p_observaciones
        );
        
        SET p_id_resultado = LAST_INSERT_ID();
        SET p_codigo = v_nuevo_codigo;
        
        -- Actualizar abono en la agenda
        UPDATE agenda SET
            abono_inicial = v_nuevo_abono
        WHERE id = p_id_agenda;
        
        SET p_total_abonado = v_nuevo_abono;
        SET p_saldo_pendiente = v_total_cita - v_nuevo_abono;
        
        SET p_mensaje = CONCAT('Pago ', v_nuevo_codigo, ' registrado exitosamente.');
    
    -- ========================================
    -- ELIMINAR PAGO
    -- ========================================
    ELSEIF p_accion = 'ELIMINAR' THEN
        
        IF p_id_pago IS NULL THEN
            SET p_mensaje = 'ERROR: Debe proporcionar el ID del pago a eliminar.';
            LEAVE main_block;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM agenda_pagos WHERE id = p_id_pago) THEN
            SET p_mensaje = 'ERROR: Pago no existe.';
            LEAVE main_block;
        END IF;
        
        SELECT codigo, monto INTO p_codigo, p_monto FROM agenda_pagos WHERE id = p_id_pago;
        
        DELETE FROM agenda_pagos WHERE id = p_id_pago;
        
        SET p_id_resultado = p_id_pago;
        
        -- Recalcular abono total
        SELECT COALESCE(SUM(monto), 0) INTO v_nuevo_abono
        FROM agenda_pagos
        WHERE id_agenda = p_id_agenda;
        
        UPDATE agenda SET
            abono_inicial = v_nuevo_abono
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO v_total_cita FROM agenda WHERE id = p_id_agenda;
        
        SET p_total_abonado = v_nuevo_abono;
        SET p_saldo_pendiente = v_total_cita - v_nuevo_abono;
        
        SET p_mensaje = CONCAT('Pago ', p_codigo, ' eliminado exitosamente.');
    
    END IF;
    
END$$
DELIMITER ;