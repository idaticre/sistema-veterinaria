USE vet_manada_woof;

-- ========================================
-- SP 1: CREAR HISTORIA CLÍNICA (una vez por mascota)
-- ========================================
DROP PROCEDURE IF EXISTS sp_crear_historia_clinica;
DELIMITER $$

CREATE PROCEDURE sp_crear_historia_clinica(
    IN p_id_mascota BIGINT,
    IN p_observaciones_generales TEXT,
    OUT p_id_historia BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    
    SET p_accion = 'CREAR';
    
    -- Validar que la mascota existe
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar que la mascota no tenga ya una historia clínica
    IF EXISTS (SELECT 1 FROM historia_clinica WHERE id_mascota = p_id_mascota) THEN
        SELECT id, codigo INTO p_id_historia, p_codigo
        FROM historia_clinica 
        WHERE id_mascota = p_id_mascota;
        
        SET p_mensaje = CONCAT('ADVERTENCIA: La mascota ya tiene historia clínica: ', p_codigo);
        LEAVE main_block;
    END IF;
    
    -- Generar código único
    SET v_nuevo_codigo = CONCAT('HIS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    
    WHILE EXISTS (SELECT 1 FROM historia_clinica WHERE codigo = v_nuevo_codigo) DO
        SET v_nuevo_codigo = CONCAT('HIS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    END WHILE;
    
    -- Insertar historia clínica
    INSERT INTO historia_clinica (
        codigo,
        id_mascota,
        fecha_apertura,
        observaciones_generales,
        activa
    ) VALUES (
        v_nuevo_codigo,
        p_id_mascota,
        CURDATE(),
        p_observaciones_generales,
        1
    );
    
    SET p_id_historia = LAST_INSERT_ID();
    SET p_codigo = v_nuevo_codigo;
    SET p_mensaje = CONCAT('Historia clínica ', v_nuevo_codigo, ' creada exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- SP 2: REGISTRAR ATENCIÓN MÉDICA (cada consulta)
-- ========================================
DROP PROCEDURE IF EXISTS sp_registrar_atencion_medica;
DELIMITER $$

CREATE PROCEDURE sp_registrar_atencion_medica(
    IN p_id_historia_clinica BIGINT,
    IN p_id_agenda BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_fecha_atencion DATE,
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_motivo_consulta VARCHAR(256),
    IN p_anamnesis TEXT,
    IN p_examen_fisico TEXT,
    IN p_signos_vitales VARCHAR(256),
    IN p_peso_kg DECIMAL(6,2),
    IN p_temperatura_c DECIMAL(4,2),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_observaciones TEXT,
    IN p_proximo_control DATE,
    OUT p_id_registro BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    DECLARE v_estado_abierta INT;
    
    -- Validar que la historia existe
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia_clinica) THEN
        SET p_mensaje = 'ERROR: Historia clínica no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar que la historia está activa
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia_clinica AND activa = 1) THEN
        SET p_mensaje = 'ERROR: Historia clínica no está activa.';
        LEAVE main_block;
    END IF;
    
    -- Validar agenda si se proporciona
    IF p_id_agenda IS NOT NULL AND NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Agenda no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar veterinario si se proporciona
    IF p_id_veterinario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM veterinarios WHERE id = p_id_veterinario) THEN
        SET p_mensaje = 'ERROR: Veterinario no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar colaborador si se proporciona
    IF p_id_colaborador IS NOT NULL AND NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
        SET p_mensaje = 'ERROR: Colaborador no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar fecha
    IF p_fecha_atencion IS NULL THEN
        SET p_mensaje = 'ERROR: Fecha de atención es obligatoria.';
        LEAVE main_block;
    END IF;
    
    -- Obtener estado "ABIERTA"
    SELECT id INTO v_estado_abierta 
    FROM estado_historia_clinica 
    WHERE nombre LIKE 'ABIERTA%' 
    LIMIT 1;
    
    IF v_estado_abierta IS NULL THEN
        SET v_estado_abierta = 1; -- Fallback al ID 1
    END IF;
    
    -- Generar código único
    SET v_nuevo_codigo = CONCAT('REG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    
    WHILE EXISTS (SELECT 1 FROM historia_clinica_registros WHERE codigo = v_nuevo_codigo) DO
        SET v_nuevo_codigo = CONCAT('REG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    END WHILE;
    
    -- Insertar registro de atención
    INSERT INTO historia_clinica_registros (
        codigo,
        id_historia_clinica,
        id_agenda,
        id_veterinario,
        id_colaborador,
        fecha_atencion,
        hora_inicio,
        hora_fin,
        motivo_consulta,
        anamnesis,
        examen_fisico,
        signos_vitales,
        peso_kg,
        temperatura_c,
        diagnostico,
        tratamiento,
        observaciones,
        proximo_control,
        id_estado
    ) VALUES (
        v_nuevo_codigo,
        p_id_historia_clinica,
        p_id_agenda,
        p_id_veterinario,
        p_id_colaborador,
        p_fecha_atencion,
        p_hora_inicio,
        p_hora_fin,
        p_motivo_consulta,
        p_anamnesis,
        p_examen_fisico,
        p_signos_vitales,
        p_peso_kg,
        p_temperatura_c,
        p_diagnostico,
        p_tratamiento,
        p_observaciones,
        p_proximo_control,
        v_estado_abierta
    );
    
    SET p_id_registro = LAST_INSERT_ID();
    SET p_codigo = v_nuevo_codigo;
    SET p_mensaje = CONCAT('Registro de atención ', v_nuevo_codigo, ' creado exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- SP 3: ACTUALIZAR ATENCIÓN MÉDICA
-- ========================================
DROP PROCEDURE IF EXISTS sp_actualizar_atencion_medica;
DELIMITER $$

CREATE PROCEDURE sp_actualizar_atencion_medica(
    IN p_id_registro BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_hora_fin TIME,
    IN p_motivo_consulta VARCHAR(256),
    IN p_anamnesis TEXT,
    IN p_examen_fisico TEXT,
    IN p_signos_vitales VARCHAR(256),
    IN p_peso_kg DECIMAL(6,2),
    IN p_temperatura_c DECIMAL(4,2),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_observaciones TEXT,
    IN p_proximo_control DATE,
    IN p_id_estado INT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_estado_actual INT;
    
    -- Validar que el registro existe
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
        LEAVE main_block;
    END IF;
    
    -- Obtener estado actual y código
    SELECT id_estado, codigo INTO v_estado_actual, p_codigo
    FROM historia_clinica_registros 
    WHERE id = p_id_registro;
    
    -- Evitar modificar si está cerrado o anulado
    IF v_estado_actual IN (
        SELECT id FROM estado_historia_clinica 
        WHERE nombre IN ('CERRADA', 'ANULADA')
    ) THEN
        SET p_mensaje = CONCAT('ERROR: Registro ', p_codigo, ' no puede modificarse (estado cerrado/anulado).');
        LEAVE main_block;
    END IF;
    
    -- Validar veterinario si se proporciona
    IF p_id_veterinario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM veterinarios WHERE id = p_id_veterinario) THEN
        SET p_mensaje = 'ERROR: Veterinario no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar estado si se proporciona
    IF p_id_estado IS NOT NULL AND NOT EXISTS (SELECT 1 FROM estado_historia_clinica WHERE id = p_id_estado) THEN
        SET p_mensaje = 'ERROR: Estado no válido.';
        LEAVE main_block;
    END IF;
    
    -- Actualizar registro
    UPDATE historia_clinica_registros SET
        id_veterinario = COALESCE(p_id_veterinario, id_veterinario),
        id_colaborador = COALESCE(p_id_colaborador, id_colaborador),
        hora_fin = COALESCE(p_hora_fin, hora_fin),
        motivo_consulta = COALESCE(p_motivo_consulta, motivo_consulta),
        anamnesis = COALESCE(p_anamnesis, anamnesis),
        examen_fisico = COALESCE(p_examen_fisico, examen_fisico),
        signos_vitales = COALESCE(p_signos_vitales, signos_vitales),
        peso_kg = COALESCE(p_peso_kg, peso_kg),
        temperatura_c = COALESCE(p_temperatura_c, temperatura_c),
        diagnostico = COALESCE(p_diagnostico, diagnostico),
        tratamiento = COALESCE(p_tratamiento, tratamiento),
        observaciones = COALESCE(p_observaciones, observaciones),
        proximo_control = COALESCE(p_proximo_control, proximo_control),
        id_estado = COALESCE(p_id_estado, id_estado)
    WHERE id = p_id_registro;
    
    SET p_mensaje = CONCAT('Registro ', p_codigo, ' actualizado exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- SP 4: SUBIR ARCHIVO CLÍNICO
-- ========================================
DROP PROCEDURE IF EXISTS sp_subir_archivo_clinico;
DELIMITER $$

CREATE PROCEDURE sp_subir_archivo_clinico(
    IN p_id_registro_atencion BIGINT,
    IN p_id_tipo_archivo INT,
    IN p_nombre_archivo VARCHAR(128),
    IN p_extension_archivo VARCHAR(32),
    IN p_descripcion VARCHAR(256),
    OUT p_id_archivo BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    
    -- Validar que el registro existe
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro_atencion) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar tipo de archivo si se proporciona
    IF p_id_tipo_archivo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM tipos_archivo_clinico WHERE id = p_id_tipo_archivo) THEN
        SET p_mensaje = 'ERROR: Tipo de archivo no existe.';
        LEAVE main_block;
    END IF;
    
    -- Validar nombre de archivo
    IF p_nombre_archivo IS NULL OR TRIM(p_nombre_archivo) = '' THEN
        SET p_mensaje = 'ERROR: Nombre de archivo es obligatorio.';
        LEAVE main_block;
    END IF;
    
    -- Generar código único
    SET v_nuevo_codigo = CONCAT('ARC-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    
    WHILE EXISTS (SELECT 1 FROM historia_clinica_archivos WHERE codigo = v_nuevo_codigo) DO
        SET v_nuevo_codigo = CONCAT('ARC-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    END WHILE;
    
    -- Insertar archivo
    INSERT INTO historia_clinica_archivos (
        codigo,
        id_registro_atencion,
        id_tipo_archivo,
        nombre_archivo,
        extension_archivo,
        descripcion,
        fecha_subida
    ) VALUES (
        v_nuevo_codigo,
        p_id_registro_atencion,
        p_id_tipo_archivo,
        p_nombre_archivo,
        p_extension_archivo,
        p_descripcion,
        NOW()
    );
    
    SET p_id_archivo = LAST_INSERT_ID();
    SET p_codigo = v_nuevo_codigo;
    SET p_mensaje = CONCAT('Archivo ', v_nuevo_codigo, ' subido exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- SP 5: CONSULTAR HISTORIAL COMPLETO DE MASCOTA
-- ========================================
DROP PROCEDURE IF EXISTS sp_consultar_historial_mascota;
DELIMITER $$

CREATE PROCEDURE sp_consultar_historial_mascota(
    IN p_id_mascota BIGINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_id_historia BIGINT;
    
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
    ELSE
        -- Obtener ID de historia
        SELECT id INTO v_id_historia 
        FROM historia_clinica 
        WHERE id_mascota = p_id_mascota;
        
        IF v_id_historia IS NULL THEN
            SET p_mensaje = 'ADVERTENCIA: Mascota no tiene historia clínica creada.';
        ELSE
            SET p_mensaje = 'Consulta exitosa.';
            
            -- ========================================
            -- RESULTADO 1: INFO GENERAL MASCOTA + HISTORIA
            -- ========================================
            SELECT 
                m.id AS mascota_id,
                m.nombre AS mascota_nombre,
                m.especie,
                m.raza,
                m.fecha_nacimiento,
                m.sexo,
                m.color,
                m.chip_identificacion,
                
                hc.id AS historia_id,
                hc.codigo AS historia_codigo,
                hc.fecha_apertura,
                hc.observaciones_generales,
                hc.activa,
                
                (SELECT COUNT(*) 
                 FROM historia_clinica_registros 
                 WHERE id_historia_clinica = hc.id) AS total_atenciones,
                 
                (SELECT MAX(fecha_atencion) 
                 FROM historia_clinica_registros 
                 WHERE id_historia_clinica = hc.id) AS ultima_atencion
                
            FROM mascotas m
            INNER JOIN historia_clinica hc ON hc.id_mascota = m.id
            WHERE m.id = p_id_mascota;
            
            -- ========================================
            -- RESULTADO 2: TODOS LOS REGISTROS DE ATENCIÓN
            -- ========================================
            SELECT 
                reg.id AS registro_id,
                reg.codigo AS registro_codigo,
                reg.fecha_atencion,
                reg.hora_inicio,
                reg.hora_fin,
                reg.motivo_consulta,
                reg.anamnesis,
                reg.examen_fisico,
                reg.signos_vitales,
                reg.peso_kg,
                reg.temperatura_c,
                reg.diagnostico,
                reg.tratamiento,
                reg.observaciones,
                reg.proximo_control,
                reg.fecha_registro,
                
                a.codigo AS agenda_codigo,
                a.fecha AS fecha_cita,
                a.hora AS hora_cita,
                
                CONCAT(vet.nombres, ' ', vet.apellidos) AS veterinario_nombre,
                vet.cmp AS veterinario_cmp,
                
                CONCAT(col.nombres, ' ', col.apellidos) AS colaborador_nombre,
                
                ehc.nombre AS estado,
                
                -- Servicios realizados en esa atención
                (SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ')
                 FROM ingresos_servicios ins
                 INNER JOIN servicios s ON ins.id_servicio = s.id
                 WHERE ins.id_agenda = reg.id_agenda) AS servicios_realizados,
                
                -- Total de la cita
                (SELECT a.total_cita
                 FROM agenda a
                 WHERE a.id = reg.id_agenda) AS total_cita,
                
                -- Total pagado
                (SELECT COALESCE(SUM(ap.monto), 0)
                 FROM agenda_pagos ap
                 WHERE ap.id_agenda = reg.id_agenda) AS total_pagado,
                 
                -- Cantidad de archivos
                (SELECT COUNT(*)
                 FROM historia_clinica_archivos
                 WHERE id_registro_atencion = reg.id) AS total_archivos
                
            FROM historia_clinica_registros reg
            LEFT JOIN agenda a ON reg.id_agenda = a.id
            LEFT JOIN veterinarios vet ON reg.id_veterinario = vet.id
            LEFT JOIN colaboradores col ON reg.id_colaborador = col.id
            LEFT JOIN estado_historia_clinica ehc ON reg.id_estado = ehc.id
            WHERE reg.id_historia_clinica = v_id_historia
            ORDER BY reg.fecha_atencion DESC, reg.hora_inicio DESC;
            
            -- ========================================
            -- RESULTADO 3: ARCHIVOS MÉDICOS
            -- ========================================
            SELECT 
                hca.id AS archivo_id,
                hca.codigo AS archivo_codigo,
                hca.nombre_archivo,
                hca.extension_archivo,
                hca.descripcion,
                hca.fecha_subida,
                
                tac.nombre AS tipo_archivo,
                tac.descripcion AS tipo_descripcion,
                
                reg.codigo AS registro_codigo,
                reg.fecha_atencion,
                reg.motivo_consulta
                
            FROM historia_clinica_archivos hca
            INNER JOIN historia_clinica_registros reg ON hca.id_registro_atencion = reg.id
            LEFT JOIN tipos_archivo_clinico tac ON hca.id_tipo_archivo = tac.id
            WHERE reg.id_historia_clinica = v_id_historia
            ORDER BY hca.fecha_subida DESC;
        END IF;
    END IF;
END$$
DELIMITER ;

-- ========================================
-- SP 6: CONSULTAR UN REGISTRO ESPECÍFICO
-- ========================================
DROP PROCEDURE IF EXISTS sp_consultar_registro_atencion;
DELIMITER $$

CREATE PROCEDURE sp_consultar_registro_atencion(
    IN p_id_registro BIGINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
    ELSE
        SET p_mensaje = 'Consulta exitosa.';
        
        -- RESULTADO 1: Datos del registro
        SELECT 
            reg.id,
            reg.codigo,
            reg.fecha_atencion,
            reg.hora_inicio,
            reg.hora_fin,
            reg.motivo_consulta,
            reg.anamnesis,
            reg.examen_fisico,
            reg.signos_vitales,
            reg.peso_kg,
            reg.temperatura_c,
            reg.diagnostico,
            reg.tratamiento,
            reg.observaciones,
            reg.proximo_control,
            reg.fecha_registro,
            
            hc.codigo AS historia_codigo,
            
            m.id AS mascota_id,
            m.nombre AS mascota_nombre,
            m.especie,
            m.raza,
            
            CONCAT(c.nombres, ' ', c.apellidos) AS cliente_nombre,
            c.telefono AS cliente_telefono,
            
            a.codigo AS agenda_codigo,
            
            CONCAT(vet.nombres, ' ', vet.apellidos) AS veterinario_nombre,
            
            ehc.nombre AS estado
            
        FROM historia_clinica_registros reg
        INNER JOIN historia_clinica hc ON reg.id_historia_clinica = hc.id
        INNER JOIN mascotas m ON hc.id_mascota = m.id
        INNER JOIN clientes c ON m.id_cliente = c.id
        LEFT JOIN agenda a ON reg.id_agenda = a.id
        LEFT JOIN veterinarios vet ON reg.id_veterinario = vet.id
        LEFT JOIN estado_historia_clinica ehc ON reg.id_estado = ehc.id
        WHERE reg.id = p_id_registro;
        
        -- RESULTADO 2: Archivos de este registro
        SELECT 
            hca.id,
            hca.codigo,
            hca.nombre_archivo,
            hca.extension_archivo,
            hca.descripcion,
            hca.fecha_subida,
            
            tac.nombre AS tipo_archivo
            
        FROM historia_clinica_archivos hca
        LEFT JOIN tipos_archivo_clinico tac ON hca.id_tipo_archivo = tac.id
        WHERE hca.id_registro_atencion = p_id_registro
        ORDER BY hca.fecha_subida;
    END IF;
    
END$$
DELIMITER ;

-- ========================================
-- SP 7: ELIMINAR ARCHIVO CLÍNICO
-- ========================================
DROP PROCEDURE IF EXISTS sp_eliminar_archivo_clinico;
DELIMITER $$

CREATE PROCEDURE sp_eliminar_archivo_clinico(
    IN p_id_archivo BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_archivos WHERE id = p_id_archivo) THEN
        SET p_mensaje = 'ERROR: Archivo no existe.';
        LEAVE main_block;
    END IF;
    
    SELECT codigo INTO p_codigo FROM historia_clinica_archivos WHERE id = p_id_archivo;
    
    DELETE FROM historia_clinica_archivos WHERE id = p_id_archivo;
    
    SET p_mensaje = CONCAT('Archivo ', p_codigo, ' eliminado exitosamente.');
    
END$$
DELIMITER ;