-- BLOQUE 04 PROCEDIMIENTOS ALMACENADOS CRUD
USE vet_manada_woof;

-- ========================================
-- SP: REGISTRAR_HISTORIA_CLINICA
-- Crea una nueva historia clínica para una mascota existente.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_historia_clinica;
DELIMITER $$

CREATE PROCEDURE registrar_historia_clinica (
    IN p_id_mascota BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_visita BIGINT,
    IN p_motivo_consulta VARCHAR(128),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_fecha DATE,
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_descripcion TEXT,
    IN p_observaciones TEXT,
    OUT p_id_historia BIGINT,
    OUT p_codigo_historia VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_estado_abierta INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_historia = NULL;
        SET p_codigo_historia = NULL;
        SET p_mensaje = 'ERROR: Falló el registro de historia clínica. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia de mascota activa
    IF NOT EXISTS (
        SELECT 1 FROM mascotas 
        WHERE id = p_id_mascota 
          AND id_estado = (SELECT id FROM estado_mascota WHERE nombre LIKE 'ACTIVA%')
    ) THEN
        SET p_mensaje = 'ERROR: Mascota no existe o no está activa.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar fecha
    IF p_fecha IS NULL THEN
        SET p_mensaje = 'ERROR: Fecha de atención obligatoria.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar horas
    IF p_hora_inicio IS NULL OR p_hora_fin IS NULL THEN
        SET p_mensaje = 'ERROR: Hora de inicio y fin obligatorias.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener estado "ABIERTA"
    SELECT id INTO v_estado_abierta 
    FROM estado_historia_clinica 
    WHERE nombre LIKE 'ABIERTA%' LIMIT 1;

    IF v_estado_abierta IS NULL THEN
        SET p_mensaje = 'ERROR: No existe estado "ABIERTA" en catálogo estado_historia_clinica.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Insertar registro
    INSERT INTO historia_clinica (
        codigo, id_mascota, id_colaborador, id_veterinario, id_visita,
        motivo_consulta, diagnostico, tratamiento, fecha, hora_inicio, hora_fin,
        descripcion, observaciones, id_estado
    )
    VALUES (
        NULL, p_id_mascota, p_id_colaborador, p_id_veterinario, p_id_visita,
        p_motivo_consulta, p_diagnostico, p_tratamiento, p_fecha, p_hora_inicio, p_hora_fin,
        p_descripcion, p_observaciones, v_estado_abierta
    );

    SET p_id_historia = LAST_INSERT_ID();
    SET p_codigo_historia = CONCAT('HIS', LPAD(p_id_historia, 6, '0'));

    -- Actualizar código generado
    UPDATE historia_clinica SET codigo = p_codigo_historia WHERE id = p_id_historia;

    SET p_mensaje = CONCAT('Historia clínica creada correctamente. Código: ', p_codigo_historia);

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_HISTORIA_CLINICA
-- Modifica los datos de una historia clínica existente.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_historia_clinica;
DELIMITER $$

CREATE PROCEDURE actualizar_historia_clinica (
    IN p_id_historia BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_visita BIGINT,
    IN p_motivo_consulta VARCHAR(128),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_fecha DATE,
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_descripcion TEXT,
    IN p_observaciones TEXT,
    IN p_id_estado INT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_codigo_historia VARCHAR(16);
    DECLARE v_estado_actual INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló actualización de historia clínica. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia) THEN
        SET p_mensaje = 'ERROR: Historia clínica no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener código y estado actual
    SELECT codigo, id_estado INTO v_codigo_historia, v_estado_actual
    FROM historia_clinica WHERE id = p_id_historia;

    -- Evitar modificar si está cerrada o anulada
    IF v_estado_actual IN (
        SELECT id FROM estado_historia_clinica WHERE nombre LIKE 'CERRADA%' OR nombre LIKE 'ANULADA%'
    ) THEN
        SET p_mensaje = CONCAT('ERROR: La historia clínica ', v_codigo_historia, ' no puede modificarse por su estado actual.');
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Actualizar datos
    UPDATE historia_clinica
    SET id_colaborador = p_id_colaborador,
        id_veterinario = p_id_veterinario,
        id_visita = p_id_visita,
        motivo_consulta = p_motivo_consulta,
        diagnostico = p_diagnostico,
        tratamiento = p_tratamiento,
        fecha = p_fecha,
        hora_inicio = p_hora_inicio,
        hora_fin = p_hora_fin,
        descripcion = p_descripcion,
        observaciones = p_observaciones,
        id_estado = p_id_estado
    WHERE id = p_id_historia;

    SET p_mensaje = CONCAT('Historia clínica ', v_codigo_historia, ' actualizada correctamente.');

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: CAMBIAR_ESTADO_HISTORIA_CLINICA
-- Permite cerrar, anular o reactivar una historia clínica.
-- ========================================
DROP PROCEDURE IF EXISTS cambiar_estado_historia_clinica;
DELIMITER $$

CREATE PROCEDURE cambiar_estado_historia_clinica (
    IN p_id_historia BIGINT,
    IN p_id_estado_nuevo INT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_codigo_historia VARCHAR(16);
    DECLARE v_estado_actual INT;
    DECLARE v_nombre_estado VARCHAR(64);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló el cambio de estado de historia clínica. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia) THEN
        SET p_mensaje = 'ERROR: Historia clínica no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar nuevo estado
    IF NOT EXISTS (SELECT 1 FROM estado_historia_clinica WHERE id = p_id_estado_nuevo) THEN
        SET p_mensaje = 'ERROR: Estado destino no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener código y estado actual
    SELECT codigo, id_estado INTO v_codigo_historia, v_estado_actual
    FROM historia_clinica WHERE id = p_id_historia;

    -- Evitar cambiar a mismo estado
    IF v_estado_actual = p_id_estado_nuevo THEN
        SET p_mensaje = CONCAT('Historia clínica ', v_codigo_historia, ' ya está en el estado seleccionado.');
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener nombre del nuevo estado
    SELECT nombre INTO v_nombre_estado FROM estado_historia_clinica WHERE id = p_id_estado_nuevo;

    -- Actualizar estado
    UPDATE historia_clinica SET id_estado = p_id_estado_nuevo WHERE id = p_id_historia;

    SET p_mensaje = CONCAT('Historia clínica ', v_codigo_historia, ' cambiada a estado: ', v_nombre_estado);

    COMMIT;
END$$
DELIMITER ;

