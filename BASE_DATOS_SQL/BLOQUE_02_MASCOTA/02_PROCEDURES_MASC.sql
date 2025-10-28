-- BLOQUE 02 PROCEDIMIENTOS ALMACENADOS CRUD
USE vet_manada_woof;
-- ========================================
-- SP: REGISTRAR_MASCOTA
-- Registra una nueva mascota validando cliente, duplicados y consistencia de datos.
-- Genera código único y crea historia clínica inicial.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_mascota;
DELIMITER $$

CREATE PROCEDURE registrar_mascota (
    IN p_nombre VARCHAR(64),
    IN p_sexo CHAR(1),
    IN p_id_cliente BIGINT,
    IN p_id_raza INT,
    IN p_id_especie INT,
    IN p_fecha_nacimiento DATE,
    IN p_pelaje VARCHAR(16),
    IN p_id_tamano INT,
    IN p_id_etapa INT,
    IN p_esterilizado TINYINT,
    IN p_alergias VARCHAR(128),
    IN p_peso DECIMAL(6,2),
    IN p_chip TINYINT,
    IN p_pedigree TINYINT,
    IN p_factor_dea TINYINT,
    IN p_agresividad TINYINT,
    IN p_foto VARCHAR(255),
    OUT p_id_mascota BIGINT,
    OUT p_codigo_mascota VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_estado_activa INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_mascota = NULL;
        SET p_codigo_mascota = NULL;
        SET p_mensaje = 'ERROR: Falló el registro de mascota. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar cliente existente
    IF NOT EXISTS (SELECT 1 FROM clientes WHERE id = p_id_cliente) THEN
        SET p_mensaje = 'ERROR: Cliente no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar especie obligatoria
    IF p_id_especie IS NULL THEN
        SET p_mensaje = 'ERROR: Especie obligatoria.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar nombre obligatorio
    IF p_nombre IS NULL OR TRIM(p_nombre) = '' THEN
        SET p_mensaje = 'ERROR: Nombre de mascota obligatorio.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Prevenir duplicado (nombre + cliente + especie)
    IF EXISTS (
        SELECT 1 FROM mascotas 
        WHERE nombre = p_nombre 
          AND id_cliente = p_id_cliente 
          AND id_especie = p_id_especie
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe una mascota con el mismo nombre para este cliente y especie.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener estado "ACTIVA"
    SELECT id INTO v_estado_activa FROM estado_mascota WHERE nombre = 'ACTIVA' LIMIT 1;

    IF v_estado_activa IS NULL THEN
        SET p_mensaje = 'ERROR: No existe estado "ACTIVA" en catálogo estado_mascota.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Insertar mascota
    INSERT INTO mascotas (
        codigo, nombre, sexo, id_cliente, id_raza, id_especie,
        id_estado, fecha_nacimiento, pelaje, id_tamano, id_etapa,
        esterilizado, alergias, peso, chip, pedigree, factor_dea,
        agresividad, foto
    )
    VALUES (
        NULL, p_nombre, p_sexo, p_id_cliente, p_id_raza, p_id_especie,
        v_estado_activa, p_fecha_nacimiento, p_pelaje, p_id_tamano, p_id_etapa,
        p_esterilizado, p_alergias, p_peso, p_chip, p_pedigree, p_factor_dea,
        p_agresividad, p_foto
    );

    SET p_id_mascota = LAST_INSERT_ID();
    SET p_codigo_mascota = CONCAT('PET', LPAD(p_id_mascota, 6, '0'));

    -- Actualizar código generado
    UPDATE mascotas SET codigo = p_codigo_mascota WHERE id = p_id_mascota;

    -- Crear historia clínica inicial (estado = "Abierta" = 1)
    INSERT INTO historia_clinica (id_mascota, id_estado, fecha_apertura)
    VALUES (p_id_mascota, 1, NOW());

    SET p_mensaje = CONCAT('Mascota registrada correctamente con código: ', p_codigo_mascota);

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_MASCOTA
-- Actualiza los datos generales de una mascota,
-- incluyendo cambio de estado (eliminación lógica si aplica).
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_mascota;
DELIMITER $$

CREATE PROCEDURE actualizar_mascota (
    IN p_id_mascota BIGINT,              
    IN p_nombre VARCHAR(64),             
    IN p_sexo CHAR(1),                   
    IN p_id_raza INT,                    
    IN p_id_especie INT,                 
    IN p_id_estado INT,                  
    IN p_fecha_nacimiento DATE,          
    IN p_pelaje VARCHAR(16),             
    IN p_id_tamano INT,                  
    IN p_id_etapa INT,                   
    IN p_esterilizado TINYINT,           
    IN p_alergias VARCHAR(128),          
    IN p_peso DECIMAL(6,2),              
    IN p_chip TINYINT,                   
    IN p_pedigree TINYINT,               
    IN p_factor_dea TINYINT,             
    IN p_agresividad TINYINT,            
    IN p_foto VARCHAR(255),              
    OUT p_mensaje VARCHAR(255)           
)
proc_main: BEGIN
    DECLARE v_id_cliente BIGINT;         -- ID del cliente dueño de la mascota
    DECLARE v_codigo_mascota VARCHAR(16);-- Código único de la mascota

    -- Manejo de errores: ante cualquier excepción se revierte la transacción
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló actualización de mascota. Transacción revertida.';
    END;

    START TRANSACTION;

    -- 1️ Validar existencia de la mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 2️⃣ Obtener cliente y código asociados (para validaciones posteriores y mensaje final)
    SELECT id_cliente, codigo
    INTO v_id_cliente, v_codigo_mascota
    FROM mascotas
    WHERE id = p_id_mascota;

    -- 3️⃣ Validar duplicado: mismo nombre + cliente + especie (excluyendo la misma mascota)
    IF EXISTS (
        SELECT 1 FROM mascotas
        WHERE nombre = p_nombre
          AND id_cliente = v_id_cliente
          AND id_especie = p_id_especie
          AND id <> p_id_mascota
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe una mascota con el mismo nombre para este cliente y especie.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 4️⃣ Validar existencia de especie
    IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id_especie) THEN
        SET p_mensaje = 'ERROR: Especie no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 5️ Validar existencia del estado
    IF NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = p_id_estado) THEN
        SET p_mensaje = 'ERROR: Estado no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 6️⃣ Actualizar datos generales y estado lógico
    UPDATE mascotas
    SET nombre = p_nombre,
        sexo = p_sexo,
        id_raza = p_id_raza,
        id_especie = p_id_especie,
        id_estado = p_id_estado,
        fecha_nacimiento = p_fecha_nacimiento,
        pelaje = p_pelaje,
        id_tamano = p_id_tamano,
        id_etapa = p_id_etapa,
        esterilizado = p_esterilizado,
        alergias = p_alergias,
        peso = p_peso,
        chip = p_chip,
        pedigree = p_pedigree,
        factor_dea = p_factor_dea,
        agresividad = p_agresividad,
        foto = p_foto,
        fecha_modificacion = NOW()
    WHERE id = p_id_mascota;

    -- 7️ Generar mensaje final adaptado al cambio de estado (actualización o eliminación lógica)
    IF (SELECT nombre FROM estado_mascota WHERE id = p_id_estado) LIKE 'INACTIVA%' THEN
        SET p_mensaje = CONCAT('Mascota ', v_codigo_mascota, ' desactivada (eliminación lógica).');
    ELSE
        SET p_mensaje = CONCAT('Mascota actualizada correctamente. Código: ', v_codigo_mascota);
    END IF;

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_MEDICAMENTO_MASCOTA
-- Registra la administración de un medicamento a una mascota,
-- validando duplicados y relaciones referenciales.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_medicamento_mascota;
DELIMITER $$

CREATE PROCEDURE registrar_medicamento_mascota (
    IN p_id_mascota BIGINT,              
    IN p_id_medicamento INT,             
    IN p_id_via INT,                     
    IN p_dosis VARCHAR(32),              
    IN p_fecha_aplicacion DATE,          
    IN p_id_colaborador BIGINT,          
    IN p_id_veterinario BIGINT,          
    IN p_observaciones VARCHAR(64),      
    OUT p_mensaje VARCHAR(255)           
)
proc_main: BEGIN
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_nombre_medicamento VARCHAR(64);

    -- Manejo de errores
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló el registro del medicamento. Transacción revertida.';
    END;

    START TRANSACTION;

    -- 1️⃣ Validar existencia de mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 2️⃣ Validar existencia de medicamento
    IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id_medicamento) THEN
        SET p_mensaje = 'ERROR: Medicamento no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 3️⃣ Validar existencia de vía de aplicación
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 4️⃣ Validar duplicado (misma mascota, medicamento y fecha)
    IF EXISTS (
        SELECT 1 FROM medicamentos_mascota
        WHERE id_mascota = p_id_mascota
          AND id_medicamento = p_id_medicamento
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro para este medicamento en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 5️⃣ Insertar nuevo registro
    INSERT INTO medicamentos_mascota (
        codigo, id_mascota, id_medicamento, id_via, dosis,
        fecha_aplicacion, id_colaborador, id_veterinario, observaciones
    )
    VALUES (
        CONCAT('MEDM', LPAD((SELECT IFNULL(MAX(id), 0) + 1 FROM medicamentos_mascota), 6, '0')),
        p_id_mascota,
        p_id_medicamento,
        p_id_via,
        p_dosis,
        p_fecha_aplicacion,
        p_id_colaborador,
        p_id_veterinario,
        p_observaciones
    );

    -- 6️⃣ Obtener el código del registro insertado
    SELECT codigo INTO v_codigo_registro 
    FROM medicamentos_mascota 
    WHERE id = LAST_INSERT_ID();

    -- 7️⃣ Obtener nombre del medicamento para el mensaje
    SELECT nombre INTO v_nombre_medicamento FROM medicamentos WHERE id = p_id_medicamento;

    COMMIT;

    -- 8️⃣ Mensaje final
    SET p_mensaje = CONCAT('Medicamento "', v_nombre_medicamento,
                            '" registrado correctamente. Código del registro: ', v_codigo_registro, '.');
END$$
DELIMITER ;


-- ========================================
-- SP: ACTUALIZAR_MEDICAMENTO_MASCOTA
-- Actualiza la información del medicamento aplicado a una mascota
-- o realiza eliminación lógica mediante campo activo.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_medicamento_mascota;
DELIMITER $$

CREATE PROCEDURE actualizar_medicamento_mascota (
    IN p_id_registro INT,                
    IN p_id_mascota BIGINT,              
    IN p_id_medicamento INT,             
    IN p_id_via INT,                     
    IN p_dosis VARCHAR(32),              
    IN p_fecha_aplicacion DATE,          
    IN p_id_colaborador BIGINT,          -- Colaborador que aplicó el medicamento
    IN p_id_veterinario BIGINT,          -- Veterinario responsable
    IN p_observaciones VARCHAR(64),      
    IN p_activo TINYINT,                 
    OUT p_mensaje VARCHAR(255)          
)
proc_main: BEGIN
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_codigo_mascota VARCHAR(16);
    DECLARE v_nombre_medicamento VARCHAR(64);

    -- Manejo de errores
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló actualización del medicamento. Transacción revertida.';
    END;

    START TRANSACTION;

    -- 1️⃣ Validar existencia del registro
    IF NOT EXISTS (SELECT 1 FROM medicamentos_mascota WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 2️⃣ Obtener datos de referencia
    SELECT codigo INTO v_codigo_registro FROM medicamentos_mascota WHERE id = p_id_registro;
    SELECT codigo INTO v_codigo_mascota FROM mascotas WHERE id = p_id_mascota;
    SELECT nombre INTO v_nombre_medicamento FROM medicamentos WHERE id = p_id_medicamento;

    -- 3️⃣ Actualizar datos generales o estado lógico
    UPDATE medicamentos_mascota
    SET id_mascota = p_id_mascota,
        id_medicamento = p_id_medicamento,
        id_via = p_id_via,
        dosis = p_dosis,
        fecha_aplicacion = p_fecha_aplicacion,
        id_colaborador = p_id_colaborador,
        id_veterinario = p_id_veterinario,
        observaciones = p_observaciones,
        fecha_modificacion = NOW(),
        activo = p_activo
    WHERE id = p_id_registro;

    COMMIT;

    -- 4️ Mensaje final
    IF p_activo = 0 THEN
        SET p_mensaje = CONCAT('Registro ', v_codigo_registro,
                               ' desactivado (eliminación lógica).');
    ELSE
        SET p_mensaje = CONCAT('Registro ', v_codigo_registro,
                               ' actualizado correctamente (', v_nombre_medicamento,
                               ' - Mascota ', v_codigo_mascota, ').');
    END IF;
END$$
DELIMITER ;


-- ========================================
-- SP: REGISTRAR_VACUNA_MASCOTA
-- Registra una nueva vacuna aplicada a una mascota.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_vacuna_mascota;
DELIMITER $$
CREATE PROCEDURE registrar_vacuna_mascota(
    IN p_id_mascota INT,
    IN p_id_vacuna INT,
    IN p_id_via INT,
    IN p_fecha_aplicacion DATE,
    IN p_durabilidad_anios INT,
    IN p_proxima_dosis DATE,
    IN p_id_colaborador INT,
    OUT p_id_insertado INT,
    OUT p_mensaje VARCHAR(100)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_insertado = NULL;
        SET p_mensaje = 'Error al registrar vacuna.';
    END;

    START TRANSACTION;

    INSERT INTO vacunas_mascota (
        id_mascota, id_vacuna, id_via, fecha_aplicacion,
        durabilidad_anios, proxima_dosis, id_colaborador, activo
    )
    VALUES (
        p_id_mascota, p_id_vacuna, p_id_via, p_fecha_aplicacion,
        p_durabilidad_anios, p_proxima_dosis, p_id_colaborador, 1
    );

    SET p_id_insertado = LAST_INSERT_ID();
    SET p_mensaje = 'Vacuna registrada correctamente.';

    COMMIT;
END $$
DELIMITER ;



-- ========================================
-- SP: ACTUALIZAR_VACUNA_MASCOTA
-- Actualiza los datos de una vacuna aplicada o realiza
-- la eliminación lógica del registro si corresponde.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_vacuna_mascota;
DELIMITER $$

CREATE PROCEDURE actualizar_vacuna_mascota(
    IN p_id_vacuna_mascota INT,
    IN p_id_vacuna INT,
    IN p_id_via INT,
    IN p_dosis VARCHAR(32),
    IN p_fecha_aplicacion DATE,
    IN p_durabilidad_anios INT,
    IN p_proxima_dosis DATE,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_observaciones VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_codigo VARCHAR(16);
    DECLARE v_nombre_vacuna VARCHAR(64);
    DECLARE v_nombre_mascota VARCHAR(64);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló actualización de vacuna aplicada. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia del registro
    IF NOT EXISTS (SELECT 1 FROM vacunas_mascota WHERE id = p_id_vacuna_mascota) THEN
        SET p_mensaje = 'ERROR: Registro de vacuna no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar vacuna y vía
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener datos informativos
    SELECT codigo INTO v_codigo FROM vacunas_mascota WHERE id = p_id_vacuna_mascota;
    SELECT v.nombre, m.nombre
    INTO v_nombre_vacuna, v_nombre_mascota
    FROM vacunas_mascota vm
    JOIN vacunas v ON vm.id_vacuna = v.id
    JOIN mascotas m ON vm.id_mascota = m.id
    WHERE vm.id = p_id_vacuna_mascota;

    -- Actualizar registro
    UPDATE vacunas_mascota
    SET id_vacuna = p_id_vacuna,
        id_via = p_id_via,
        dosis = p_dosis,
        fecha_aplicacion = p_fecha_aplicacion,
        durabilidad_anios = p_durabilidad_anios,
        proxima_dosis = p_proxima_dosis,
        id_colaborador = p_id_colaborador,
        id_veterinario = p_id_veterinario,
        observaciones = p_observaciones,
        fecha_modificacion = NOW()
    WHERE id = p_id_vacuna_mascota;

    -- Eliminación lógica si aplica (si agregas campo activo)
    IF p_activo = 0 THEN
        UPDATE vacunas_mascota SET activo = 0, fecha_modificacion = NOW() WHERE id = p_id_vacuna_mascota;
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" para la mascota "', v_nombre_mascota, '" desactivada.');
    ELSE
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" actualizada correctamente. Código: ', v_codigo);
    END IF;

    COMMIT;
END$$
DELIMITER ;