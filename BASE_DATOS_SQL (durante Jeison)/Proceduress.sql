-- SCRIPT: PROCEDIMIENTOS ALMACENADOS CRUD - SISTEMA VETERINARIA_WOOF
USE vet_manada_woof;
-- ========================================
-- SP: gestionar_rol_usuario // Asigna o elimina roles de usuarios 
-- usuario puede tener varios roles y un rol puede estar en varios usuarios.
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_rol_usuario;
DELIMITER $$
CREATE PROCEDURE gestionar_rol_usuario(
    IN p_accion VARCHAR(10),
    IN p_id_usuario INT,
    IN p_id_rol INT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nombre_usuario VARCHAR(64);
    DECLARE v_nombre_rol VARCHAR(64);
    DECLARE v_id_colaborador BIGINT;
    DECLARE v_fecha_inicio DATE;
    DECLARE v_tiene_roles_previos INT DEFAULT 0;
    DECLARE v_horarios_insertados INT DEFAULT 0;
    SET p_accion = UPPER(TRIM(p_accion));

    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id_usuario) THEN
        SET p_mensaje = 'ERROR: Usuario no existe.';
        LEAVE main_block;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id_rol) THEN
        SET p_mensaje = 'ERROR: Rol no existe.';
        LEAVE main_block;
    END IF;

SELECT 
    username
INTO v_nombre_usuario FROM
    usuarios
WHERE
    id = p_id_usuario;
SELECT 
    nombre
INTO v_nombre_rol FROM
    roles
WHERE
    id = p_id_rol;

    IF p_accion = 'ASIGNAR' THEN
        IF EXISTS (SELECT 1 FROM usuarios_roles WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol) THEN
            SET p_mensaje = CONCAT('ERROR: El usuario ', v_nombre_usuario, ' ya tiene asignado el rol ', v_nombre_rol, '.');
            LEAVE main_block;
        END IF;

        -- Verificar si ya tiene otros roles asignados
SELECT 
    COUNT(*)
INTO v_tiene_roles_previos FROM
    usuarios_roles
WHERE
    id_usuario = p_id_usuario;

        -- Insertar rol
        INSERT INTO usuarios_roles (id_usuario, id_rol)
        VALUES (p_id_usuario, p_id_rol);

        -- AUTO-ASIGNAR HORARIOS SOLO SI ES EL PRIMER ROL
        IF v_tiene_roles_previos = 0 THEN
            SELECT id INTO v_id_colaborador
            FROM colaboradores
            WHERE id_usuario = p_id_usuario
            LIMIT 1;
            
            IF v_id_colaborador IS NOT NULL THEN
                SET v_fecha_inicio = CURDATE();
                
                -- INSERTAR TODOS LOS HORARIOS
                INSERT INTO asignacion_horarios (id_colaborador, id_horario_base, id_dia_semana,
												fecha_inicio_vigencia, fecha_fin_vigencia, motivo_cambio, activo)
                SELECT 
                    v_id_colaborador,
                    hbr.id_horario_base,
                    hbr.id_dia_semana,
                    v_fecha_inicio,
                    NULL,
                    CONCAT('Auto-asignado por primer rol: ', v_nombre_rol),
                    1
                FROM horarios_base_roles hbr
                WHERE hbr.id_rol = p_id_rol
                  AND NOT EXISTS (
                      SELECT 1 
                      FROM asignacion_horarios ah
                      WHERE ah.id_colaborador = v_id_colaborador 
                        AND ah.id_dia_semana = hbr.id_dia_semana 
                        AND ah.activo = 1
                        AND ah.fecha_fin_vigencia IS NULL
                  );
                -- Contar cuantos horarios se insertaron
                SET v_horarios_insertados = ROW_COUNT();
                
                IF v_horarios_insertados > 0 THEN
                    SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' asignado. ', v_horarios_insertados, ' horarios auto-asignados.');
                ELSE
                    SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' asignado. El colaborador ya tenia horarios asignados.');
                END IF;
            ELSE
                SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' asignado correctamente.');
            END IF;
        ELSE
            SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' asignado. Horarios no modificados (ya tenia roles previos).');
        END IF;

    ELSEIF p_accion = 'ELIMINAR' THEN
        IF NOT EXISTS (SELECT 1 FROM usuarios_roles WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol) THEN
            SET p_mensaje = CONCAT('ERROR: El usuario ', v_nombre_usuario, ' no tiene asignado el rol ', v_nombre_rol, '.');
            LEAVE main_block;
        END IF;

DELETE FROM usuarios_roles 
WHERE
    id_usuario = p_id_usuario
    AND id_rol = p_id_rol;
        SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Accion no valida. Use "ASIGNAR" o "ELIMINAR".';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_ENTIDAD_BASE
-- Inserta una nueva entidad con datos generales en la tabla 'entidades',
-- genera un código único con prefijo 'ENT' y devuelve el ID y código generado.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_entidad_base;
DELIMITER $$

CREATE PROCEDURE registrar_entidad_base (
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    OUT p_id_entidad BIGINT,
    OUT p_codigo_entidad VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_entidad = NULL;
        SET p_codigo_entidad = NULL;
        SET p_mensaje = 'ERROR: Falló el registro. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validaciones obligatorias
    IF p_nombre IS NULL OR p_documento IS NULL THEN
        SET p_mensaje = 'ERROR: Nombre y documento obligatorios.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar documento duplicado
    IF EXISTS (SELECT 1 FROM entidades WHERE documento = p_documento) THEN
        SET p_mensaje = 'ERROR: Documento ya registrado.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar correo duplicado (si no es NULL)
    IF p_correo IS NOT NULL AND EXISTS (SELECT 1 FROM entidades WHERE correo = p_correo) THEN
        SET p_mensaje = 'ERROR: Correo ya registrado.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar sexo permitido
    IF p_sexo NOT IN ('M','F','O', NULL) THEN
        SET p_mensaje = 'ERROR: Sexo inválido (M, F, O).';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar teléfono básico
    IF p_telefono IS NOT NULL AND p_telefono NOT REGEXP '^[0-9+ ]{6,15}$' THEN
        SET p_mensaje = 'ERROR: Teléfono inválido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Insertar entidad
    INSERT INTO entidades (
        id_tipo_persona_juridica, nombre, sexo, documento,
        id_tipo_documento, telefono, correo, direccion,
        ciudad, distrito, representante
    ) VALUES (
        p_id_tipo_persona_juridica, p_nombre, p_sexo, p_documento,
        p_id_tipo_documento, p_telefono, p_correo, p_direccion,
        p_ciudad, p_distrito, p_representante
    );

    SET p_id_entidad = LAST_INSERT_ID();

    -- Generar código único
    SET p_codigo_entidad = CONCAT('ENT', LPAD(p_id_entidad,6,'0'));

    -- Actualizar campo código
    UPDATE entidades SET codigo = p_codigo_entidad WHERE id = p_id_entidad;

    SET p_mensaje = CONCAT('Entidad registrada con código: ', p_codigo_entidad);

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_ENTIDAD_BASE
-- Actualiza los datos generales de una entidad.
-- Valida existencia y evita documentos duplicados.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_entidad_base;
DELIMITER $$

CREATE PROCEDURE actualizar_entidad_base (
    IN p_id_entidad BIGINT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló actualización. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia
    IF NOT EXISTS (SELECT 1 FROM entidades WHERE id = p_id_entidad) THEN
        SET p_mensaje = 'ERROR: Entidad no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar documento duplicado
    IF EXISTS (SELECT 1 FROM entidades WHERE documento = p_documento AND id <> p_id_entidad) THEN
        SET p_mensaje = 'ERROR: Documento ya registrado en otra entidad.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar correo duplicado
    IF p_correo IS NOT NULL AND EXISTS (SELECT 1 FROM entidades WHERE correo = p_correo AND id <> p_id_entidad) THEN
        SET p_mensaje = 'ERROR: Correo ya registrado en otra entidad.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar sexo permitido
    IF p_sexo NOT IN ('M','F','O', NULL) THEN
        SET p_mensaje = 'ERROR: Sexo inválido (M, F, O).';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar teléfono
    IF p_telefono IS NOT NULL AND p_telefono NOT REGEXP '^[0-9+ ]{6,15}$' THEN
        SET p_mensaje = 'ERROR: Teléfono inválido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Actualizar entidad
    UPDATE entidades
    SET
        id_tipo_persona_juridica = p_id_tipo_persona_juridica,
        nombre = p_nombre,
        sexo = p_sexo,
        documento = p_documento,
        id_tipo_documento = p_id_tipo_documento,
        correo = p_correo,
        telefono = p_telefono,
        direccion = p_direccion,
        ciudad = p_ciudad,
        distrito = p_distrito,
        representante = p_representante,
        activo = p_activo
    WHERE id = p_id_entidad;

    SET p_mensaje = CONCAT('Entidad actualizada: ', (SELECT codigo FROM entidades WHERE id = p_id_entidad));

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_COLABORADOR
-- Permite registrar una nueva entidad tipo colaborador,
-- utilizando el procedimiento base registrar_entidad_base para insertar
-- la información común en la tabla entidades con código generado (prefijo "ENT"),
-- y luego crea el registro en la tabla colaboradores asignándole su propio código (prefijo "COL").
-- Retorna los códigos generados y un mensaje informativo o de error.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_colaborador;
DELIMITER $$
CREATE PROCEDURE registrar_colaborador (
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_fecha_ingreso DATE,
    IN p_id_usuario INT,
    IN p_foto VARCHAR(128),
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_colaborador VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_colaborador BIGINT DEFAULT NULL;

   -- Llamada al SP base con variables de sesión
	CALL registrar_entidad_base(
		p_id_tipo_persona_juridica,
		p_nombre,
		p_sexo,
		p_documento,
		p_id_tipo_documento,
		p_correo,
		p_telefono,
		p_direccion,
		p_ciudad,
		p_distrito,
		NULL,
		@id_entidad,
		@codigo_entidad,
		@msg_entidad
	);

	-- Recuperar los valores
	SELECT @id_entidad, @codigo_entidad, @msg_entidad
	INTO v_id_entidad, v_codigo_entidad_local, v_mensaje_entidad;

	SET p_codigo_entidad = v_codigo_entidad_local;
	SET p_mensaje = v_mensaje_entidad;

	IF v_id_entidad IS NULL THEN
		LEAVE proc_main;
	END IF;

    -- Si ya existe colaborador, devolver su código
    IF EXISTS (SELECT 1 FROM colaboradores WHERE id_entidad = v_id_entidad) THEN
        SELECT codigo INTO p_codigo_colaborador FROM colaboradores WHERE id_entidad = v_id_entidad LIMIT 1;
        SET p_mensaje = CONCAT('La entidad ya está registrada como Colaborador. Código Colaborador: ', p_codigo_colaborador);
        LEAVE proc_main;
    END IF;

    -- Insertar en colaboradores
    INSERT INTO colaboradores (id_entidad, fecha_ingreso, id_usuario, foto, activo, codigo)
    VALUES (v_id_entidad, p_fecha_ingreso, p_id_usuario, p_foto, 1, NULL);

    SET v_id_colaborador = LAST_INSERT_ID();

    UPDATE colaboradores
    SET codigo = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'))
    WHERE id = v_id_colaborador;

    SET p_codigo_colaborador = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'));

    
    SET p_mensaje = CONCAT('Colaborador registrado correctamente. Código Colaborador: ', p_codigo_colaborador);
END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_COLABORADOR
-- Actualiza datos generales de la entidad y datos específicos del colaborador,
-- incluyendo actualización lógica de estado.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_colaborador;
DELIMITER $$
CREATE PROCEDURE actualizar_colaborador (
    IN p_id_entidad BIGINT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_fecha_ingreso DATE,
    IN p_id_usuario INT,
    IN p_foto VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_colaborador BIGINT;
    DECLARE v_codigo_colaborador VARCHAR(20);

    -- Actualizar entidad
    CALL actualizar_entidad_base(
        p_id_entidad,
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        NULL,
        p_activo,
        v_mensaje_entidad
    );

    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc_main;
    END IF;

    -- Obtener colaborador asociado
    SELECT id, codigo
    INTO v_id_colaborador, v_codigo_colaborador
    FROM colaboradores
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_colaborador IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un colaborador asociado a esa entidad.';
        LEAVE proc_main;
    END IF;

    -- Actualizar datos específicos y estado lógico
    UPDATE colaboradores SET
        fecha_ingreso = p_fecha_ingreso,
        id_usuario = p_id_usuario,
        foto = p_foto,
        activo = p_activo
    WHERE id = v_id_colaborador;

    SET p_mensaje = CONCAT('Colaborador actualizado correctamente. Código Colaborador: ', v_codigo_colaborador);
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_PROVEEDOR
-- Permite registrar una nueva entidad tipo proveedor,
-- utilizando el procedimiento base registrar_entidad_base para insertar
-- la información común en la tabla entidades con código generado (prefijo "ENT"),
-- y luego crea el registro en la tabla proveedores asignándole su propio código (prefijo "PRO").
-- Retorna los códigos generados y un mensaje informativo o de error.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_proveedor;
DELIMITER $$
CREATE PROCEDURE registrar_proveedor (
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_proveedor VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_proveedor BIGINT DEFAULT NULL;

    -- Llamar al SP base (sin id_tipo_entidad)
    CALL registrar_entidad_base(
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        p_representante,
        v_id_entidad,
        v_codigo_entidad_local,
        v_mensaje_entidad
    );

    SET p_codigo_entidad = v_codigo_entidad_local;
    SET p_mensaje = v_mensaje_entidad;

    IF v_id_entidad IS NULL THEN
        LEAVE proc_main;
    END IF;

    -- Evitar duplicar rol proveedor
    IF EXISTS (SELECT 1 FROM proveedores WHERE id_entidad = v_id_entidad) THEN
        SELECT codigo INTO p_codigo_proveedor FROM proveedores WHERE id_entidad = v_id_entidad LIMIT 1;
        SET p_mensaje = CONCAT('La entidad ya está registrada como Proveedor. Código Proveedor: ', p_codigo_proveedor);
        LEAVE proc_main;
    END IF;

    -- Insertar en proveedores y generar código
    INSERT INTO proveedores (id_entidad, codigo) VALUES (v_id_entidad, NULL);
    SET v_id_proveedor = LAST_INSERT_ID();

    UPDATE proveedores
    SET codigo = CONCAT('PRV', LPAD(v_id_proveedor, 6, '0'))
    WHERE id = v_id_proveedor;

    SET p_codigo_proveedor = CONCAT('PRV', LPAD(v_id_proveedor, 6, '0'));
    SET p_mensaje = CONCAT('Proveedor registrado correctamente. Código Proveedor: ', p_codigo_proveedor);
END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_PROVEEDOR
-- Actualiza datos generales de la entidad, representante y estado lógico del proveedor.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_proveedor;
DELIMITER $$
CREATE PROCEDURE actualizar_proveedor (
    IN p_id_entidad BIGINT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_proveedor BIGINT;
    DECLARE v_codigo_proveedor VARCHAR(20);

    -- Actualizar entidad
    CALL actualizar_entidad_base(
        p_id_entidad,
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        p_representante,
        p_activo,
        v_mensaje_entidad
    );

    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc_main;
    END IF;

    -- Buscar proveedor asociado
    SELECT id, codigo
    INTO v_id_proveedor, v_codigo_proveedor
    FROM proveedores
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_proveedor IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un proveedor asociado a esa entidad.';
        LEAVE proc_main;
    END IF;

    -- Actualizar estado lógico
    UPDATE proveedores
    SET activo = p_activo
    WHERE id = v_id_proveedor;

    SET p_mensaje = CONCAT('Proveedor actualizado correctamente. Código Proveedor: ', v_codigo_proveedor);
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_CLIENTE
-- Permite registrar una nueva entidad de tipo cliente,
-- utilizando el procedimiento base registrar_entidad_base para insertar
-- la información común en la tabla entidades con código generado (prefijo "ENT"),
-- y luego crea el registro en la tabla clientes asignándole su propio código (prefijo "CLI").
-- Retorna los códigos generados y un mensaje informativo o de error.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_cliente;
DELIMITER $$
CREATE PROCEDURE registrar_cliente (
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(128),
    OUT p_id_entidad BIGINT,
    OUT p_id_cliente BIGINT,
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_cliente VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_cliente BIGINT DEFAULT NULL;

    -- Llamar al SP base (crear o recuperar entidad)
    CALL registrar_entidad_base(
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        p_representante,
        v_id_entidad,
        v_codigo_entidad_local,
        v_mensaje_entidad
    );

    -- Retornar resultados de la entidad
    SET p_codigo_entidad = v_codigo_entidad_local;
    SET p_mensaje = v_mensaje_entidad;

    -- Si no se obtuvo la entidad, salir
    IF v_id_entidad IS NULL THEN
        LEAVE proc_main;
    END IF;

    -- Verificar si ya existe cliente
    IF EXISTS (SELECT 1 FROM clientes WHERE id_entidad = v_id_entidad) THEN
        SELECT id, codigo INTO v_id_cliente, p_codigo_cliente 
        FROM clientes WHERE id_entidad = v_id_entidad LIMIT 1;
        SET p_mensaje = CONCAT('La entidad ya está registrada como Cliente. Código Cliente: ', p_codigo_cliente);
        SET p_id_cliente = v_id_cliente;
        SET p_id_entidad = v_id_entidad;
        LEAVE proc_main;
    END IF;

    -- Crear cliente si no existe
    INSERT INTO clientes (id_entidad, codigo) VALUES (v_id_entidad, NULL);
    SET v_id_cliente = LAST_INSERT_ID();

    -- Generar código cliente
    SET p_codigo_cliente = CONCAT('CLI', LPAD(v_id_cliente, 6, '0'));
    UPDATE clientes
    SET codigo = p_codigo_cliente
    WHERE id = v_id_cliente;

    -- Salidas finales
    SET p_id_entidad = v_id_entidad;
    SET p_id_cliente = v_id_cliente;
    SET p_mensaje = CONCAT('Cliente registrado correctamente. Código Cliente: ', p_codigo_cliente);

END$$
DELIMITER ;


-- ========================================
-- SP: ACTUALIZAR_CLIENTE
-- Actualiza datos generales de la entidad y estado lógico del cliente.
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_cliente;
DELIMITER $$
CREATE PROCEDURE actualizar_cliente (
    IN p_id_entidad INT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_cliente INT;
    DECLARE v_codigo_cliente VARCHAR(20);

    -- Actualizar datos generales de la entidad (representante NULL para cliente)
    CALL actualizar_entidad_base(
        p_id_entidad,
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        p_representante,
        p_activo,
        v_mensaje_entidad
    );

    -- Si hubo error en la actualización base, devolverlo
    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc_main;
    END IF;

    -- Obtener cliente asociado
    SELECT id, codigo
    INTO v_id_cliente, v_codigo_cliente
    FROM clientes
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_cliente IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un cliente asociado a esa entidad.';
        LEAVE proc_main;
    END IF;

    -- Actualizar estado lógico del cliente
    UPDATE clientes
    SET activo = p_activo
    WHERE id = v_id_cliente;

    SET p_mensaje = CONCAT('Cliente actualizado correctamente. Código Cliente: ', v_codigo_cliente);
END$$
DELIMITER ;

-- ========================================
-- SP: REGISTRAR_VETERINARIO
-- Registra un veterinario asociado a una entidad.
-- Si la entidad no tiene colaborador, crea entidad + colaborador.
-- Luego registra veterinario con especialidad y CMP.
-- Genera códigos con prefijos en cada tabla.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_veterinario;
DELIMITER $$

CREATE PROCEDURE registrar_veterinario (
    IN p_id_entidad INT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    IN p_id_especialidad INT,
    IN p_cmp VARCHAR(32),
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_colaborador VARCHAR(20),
    OUT p_codigo_veterinario VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
registro: BEGIN
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_id_colaborador BIGINT DEFAULT NULL;
    DECLARE v_id_veterinario BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_codigo_colaborador_local VARCHAR(20);
    DECLARE v_codigo_veterinario_local VARCHAR(20);
    DECLARE v_mensaje_local VARCHAR(255);

    -- ========================================
    -- CASO 1: Crear entidad + colaborador nuevos
    -- ========================================
    IF p_id_entidad IS NULL OR p_id_entidad = 0 THEN

        CALL registrar_entidad_base(
            p_id_tipo_persona_juridica,
            p_nombre,
            p_sexo,
            p_documento,
            p_id_tipo_documento,
            p_correo,
            p_telefono,
            p_direccion,
            p_ciudad,
            p_distrito,
            p_representante,
            @v_id_ent,
            @v_cod_ent,
            @v_msg_ent
        );

        SELECT @v_id_ent, @v_cod_ent, @v_msg_ent
        INTO v_id_entidad, v_codigo_entidad_local, v_mensaje_local;

        IF v_id_entidad IS NULL THEN
            SET p_mensaje = v_mensaje_local;
            LEAVE registro;
        END IF;

        INSERT INTO colaboradores (id_entidad, codigo, activo, id_usuario, foto)
        VALUES (v_id_entidad, NULL, 1, NULL, NULL);

        SET v_id_colaborador = LAST_INSERT_ID();
        SET v_codigo_colaborador_local = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'));

        UPDATE colaboradores
        SET codigo = v_codigo_colaborador_local
        WHERE id = v_id_colaborador;

        SET p_codigo_entidad = v_codigo_entidad_local;
        SET p_codigo_colaborador = v_codigo_colaborador_local;

    -- ========================================
    -- CASO 2: Entidad ya existe
    -- ========================================
    ELSE
        SET v_id_entidad = p_id_entidad;

        IF NOT EXISTS (SELECT 1 FROM entidades WHERE id = v_id_entidad) THEN
            SET p_mensaje = 'ERROR: La entidad especificada no existe.';
            LEAVE registro;
        END IF;

        SELECT id, codigo
        INTO v_id_colaborador, v_codigo_colaborador_local
        FROM colaboradores
        WHERE id_entidad = v_id_entidad
        LIMIT 1;

        IF v_id_colaborador IS NULL THEN
            INSERT INTO colaboradores (id_entidad, codigo, activo, id_usuario, foto)
            VALUES (v_id_entidad, NULL, 1, NULL, NULL);

            SET v_id_colaborador = LAST_INSERT_ID();
            SET v_codigo_colaborador_local = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'));

            UPDATE colaboradores
            SET codigo = v_codigo_colaborador_local
            WHERE id = v_id_colaborador;
        END IF;

        SELECT codigo INTO v_codigo_entidad_local
        FROM entidades WHERE id = v_id_entidad;

        SET p_codigo_entidad = v_codigo_entidad_local;
        SET p_codigo_colaborador = v_codigo_colaborador_local;
    END IF;

    -- ========================================
    -- VALIDAR Y REGISTRAR VETERINARIO
    -- ========================================
    IF EXISTS (SELECT 1 FROM veterinarios WHERE id_colaborador = v_id_colaborador) THEN
        SET p_mensaje = CONCAT('ERROR: El colaborador ya está registrado como veterinario. Código: ',
                                v_codigo_colaborador_local);
        LEAVE registro;
    END IF;

    INSERT INTO veterinarios (id_colaborador, id_especialidad, cmp, activo, codigo)
    VALUES (v_id_colaborador, p_id_especialidad, p_cmp, 1, NULL);

    SET v_id_veterinario = LAST_INSERT_ID();
    SET v_codigo_veterinario_local = CONCAT('VET', LPAD(v_id_veterinario, 6, '0'));

    UPDATE veterinarios
    SET codigo = v_codigo_veterinario_local
    WHERE id = v_id_veterinario;

    SET p_codigo_veterinario = v_codigo_veterinario_local;
    SET p_mensaje = CONCAT('Veterinario registrado correctamente. Código: ', v_codigo_veterinario_local);

END$$
DELIMITER ;

-- ========================================
-- SP: ACTUALIZAR_VETERINARIO
-- Actualiza los datos generales en entidades,
-- los datos específicos del colaborador,
-- y los datos particulares del veterinario.
-- Incluye manejo de estado activo para eliminación lógica.
-- El mensaje final muestra los códigos (prefijos).
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_veterinario;
DELIMITER $$

CREATE PROCEDURE actualizar_veterinario (
    IN p_id_entidad INT,
    IN p_id_tipo_persona_juridica INT,
    IN p_nombre VARCHAR(128),
    IN p_sexo CHAR(1),
    IN p_documento VARCHAR(20),
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(128),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64),
    IN p_id_usuario INT,
    IN p_foto VARCHAR(128),
    IN p_id_especialidad INT,
    IN p_cmp VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_colaborador VARCHAR(20),
    OUT p_codigo_veterinario VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
actualizar: BEGIN
    DECLARE v_id_colaborador BIGINT;
    DECLARE v_id_veterinario BIGINT;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_codigo_colaborador_local VARCHAR(20);
    DECLARE v_codigo_veterinario_local VARCHAR(20);
    DECLARE v_mensaje_entidad VARCHAR(255);

    -- ========================================
    -- VALIDAR EXISTENCIA DE ENTIDAD
    -- ========================================
    IF NOT EXISTS (SELECT 1 FROM entidades WHERE id = p_id_entidad) THEN
        SET p_mensaje = 'ERROR: La entidad especificada no existe.';
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- VALIDAR DOCUMENTO DUPLICADO EN OTRA ENTIDAD
    -- ========================================
    IF EXISTS (
        SELECT 1 FROM entidades
        WHERE documento = p_documento
          AND id <> p_id_entidad
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe otra entidad con ese número de documento.';
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- VALIDAR CORREO DUPLICADO EN OTRA ENTIDAD
    -- ========================================
    IF p_correo IS NOT NULL AND EXISTS (
        SELECT 1 FROM entidades
        WHERE correo = p_correo
          AND id <> p_id_entidad
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe otra entidad con ese correo.';
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- OBTENER COLABORADOR ASOCIADO A LA ENTIDAD
    -- ========================================
    SELECT id, codigo
    INTO v_id_colaborador, v_codigo_colaborador_local
    FROM colaboradores
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_colaborador IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un colaborador asociado a esta entidad.';
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- VALIDAR QUE EL COLABORADOR ES VETERINARIO
    -- ========================================
    SELECT id, codigo
    INTO v_id_veterinario, v_codigo_veterinario_local
    FROM veterinarios
    WHERE id_colaborador = v_id_colaborador
    LIMIT 1;

    IF v_id_veterinario IS NULL THEN
        SET p_mensaje = 'ERROR: Este colaborador no está registrado como veterinario.';
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- ACTUALIZAR ENTIDAD BASE
    -- Reutilizamos el SP base para mantener consistencia
    -- ========================================
    CALL actualizar_entidad_base(
        p_id_entidad,
        p_id_tipo_persona_juridica,
        p_nombre,
        p_sexo,
        p_documento,
        p_id_tipo_documento,
        p_correo,
        p_telefono,
        p_direccion,
        p_ciudad,
        p_distrito,
        p_representante,
        p_activo,
        v_mensaje_entidad
    );

    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE actualizar;
    END IF;

    -- ========================================
    -- ACTUALIZAR COLABORADOR
    -- ========================================
    UPDATE colaboradores
    SET id_usuario = p_id_usuario,
        foto       = p_foto,
        activo     = p_activo
    WHERE id = v_id_colaborador;

    -- ========================================
    -- ACTUALIZAR VETERINARIO
    -- ========================================
    UPDATE veterinarios
    SET id_especialidad = p_id_especialidad,
        cmp             = p_cmp,
        activo          = p_activo
    WHERE id = v_id_veterinario;

    -- ========================================
    -- OBTENER CÓDIGO DE ENTIDAD PARA SALIDA
    -- ========================================
    SELECT codigo INTO v_codigo_entidad_local
    FROM entidades
    WHERE id = p_id_entidad;

    -- ========================================
    -- ASIGNAR SALIDAS
    -- ========================================
    SET p_codigo_entidad      = v_codigo_entidad_local;
    SET p_codigo_colaborador  = v_codigo_colaborador_local;
    SET p_codigo_veterinario  = v_codigo_veterinario_local;

    SET p_mensaje = CONCAT(
        'Veterinario actualizado correctamente. Código veterinario: ',
        v_codigo_veterinario_local
    );

END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: gestionar_horarios_rango
-- Aplica acción HORARIO/DESCANSO/DESASIGNAR en un rango de fechas para un colaborador
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_asignar_rango;

DELIMITER $$

CREATE PROCEDURE gestionar_asignar_rango(
    IN p_id_colaborador   BIGINT,
    IN p_fecha_inicio     DATE,
    IN p_fecha_fin        DATE,
    IN p_tipo_accion      VARCHAR(20),
    IN p_id_horario_base  INT,
    IN p_id_usuario       BIGINT,
    IN p_motivo           VARCHAR(255)
)
proc_label: BEGIN
    -- Declaración de variables
    DECLARE v_fecha_actual   DATE DEFAULT p_fecha_inicio;
    DECLARE v_dia_semana_id  INT;
    DECLARE v_id_asignacion  BIGINT DEFAULT NULL;
    DECLARE v_id_detalle     BIGINT DEFAULT NULL;
    DECLARE v_hora_inicio    TIME DEFAULT NULL;
    DECLARE v_hora_fin       TIME DEFAULT NULL;
    DECLARE v_horario_creado BOOLEAN DEFAULT FALSE;

    -- Normalizar tipo de acción
    SET p_tipo_accion = UPPER(TRIM(p_tipo_accion));

    -- Validación de parámetros obligatorios
    IF p_id_colaborador IS NULL OR p_fecha_inicio IS NULL OR p_fecha_fin IS NULL THEN
        SELECT 'ERROR' AS status, 'Faltan parametros obligatorios' AS mensaje;
        LEAVE proc_label;
    END IF;

    -- Validación de rango de fechas
    IF p_fecha_fin < p_fecha_inicio THEN
        SELECT 'ERROR' AS status, 'Rango de fechas invalido' AS mensaje;
        LEAVE proc_label;
    END IF;

    -- Validación de tipo de acción
    IF p_tipo_accion NOT IN ('ASIGNAR_HORARIO', 'ASIGNAR_DESCANSO', 'RESTAURAR_BASE') THEN
        SELECT 'ERROR' AS status, 'Accion invalida. Use: ASIGNAR_HORARIO, ASIGNAR_DESCANSO o RESTAURAR_BASE' AS mensaje;
        LEAVE proc_label;
    END IF;

    -- Validación específica para ASIGNAR_HORARIO
    IF p_tipo_accion = 'ASIGNAR_HORARIO' AND (p_id_horario_base IS NULL OR p_id_horario_base <= 0) THEN
        SELECT 'ERROR' AS status, 'Debe proporcionar un ID de horario base valido' AS mensaje;
        LEAVE proc_label;
    END IF;

    -- Verificar que el horario base existe
    IF NOT EXISTS(SELECT 1 FROM horarios_base WHERE id = p_id_horario_base) THEN
        SELECT 'ERROR' AS status, CONCAT('El horario base ID ', p_id_horario_base, ' no existe') AS mensaje;
        LEAVE proc_label;
    END IF;

    -- Inicio de transacción
    START TRANSACTION;

    -- Bucle por cada fecha en el rango
    WHILE v_fecha_actual <= p_fecha_fin DO
        -- Calcular día de la semana (1=Lunes, 7=Domingo)
        SET v_dia_semana_id = CASE DAYOFWEEK(v_fecha_actual) 
                                WHEN 1 THEN 7 
                                ELSE DAYOFWEEK(v_fecha_actual) - 1 
                              END;

        -- Buscar asignación de horario base activa
        SELECT ah.id INTO v_id_asignacion
        FROM asignacion_horarios ah
        WHERE ah.id_colaborador = p_id_colaborador
          AND ah.id_dia_semana = v_dia_semana_id
          AND ah.activo = 1
          AND v_fecha_actual BETWEEN ah.fecha_inicio_vigencia 
                                AND COALESCE(ah.fecha_fin_vigencia, '9999-12-31')
        ORDER BY ah.fecha_inicio_vigencia DESC 
        LIMIT 1;

        -- ✅ NUEVO: Si NO existe asignación base, crearla automáticamente
        IF v_id_asignacion IS NULL THEN
            INSERT INTO asignacion_horarios (
                id_colaborador,
                id_horario_base,
                id_dia_semana,
                fecha_inicio_vigencia,
                fecha_fin_vigencia,
                motivo_cambio,
                activo
            ) VALUES (
                p_id_colaborador,
                p_id_horario_base,
                v_dia_semana_id,
                v_fecha_actual,
                NULL,
                CONCAT('Auto-creado por gestionar_asignar_rango: ', COALESCE(p_motivo, 'Sin motivo')),
                1
            );
            
            SET v_id_asignacion = LAST_INSERT_ID();
            SET v_horario_creado = TRUE;
        END IF;

        -- Validar que no exista registro de asistencia
        IF EXISTS(SELECT 1 FROM registro_asistencias 
                  WHERE id_colaborador = p_id_colaborador 
                    AND fecha = v_fecha_actual) THEN
            ROLLBACK;
            SELECT 'ERROR' AS status, 
                   CONCAT('Ya existe registro de asistencia para el dia ', DATE_FORMAT(v_fecha_actual, '%d/%m/%Y')) AS mensaje;
            LEAVE proc_label;
        END IF;

        -- Buscar si existe detalle de excepción
        SELECT id_detalle INTO v_id_detalle
        FROM asignacion_horarios_detalle
        WHERE id_asignacion = v_id_asignacion 
          AND fecha = v_fecha_actual;

        -- Ejecutar acción según tipo
        IF p_tipo_accion = 'RESTAURAR_BASE' THEN
            -- RESTAURAR BASE: Eliminar excepción
            IF v_id_detalle IS NOT NULL THEN
                DELETE FROM asignacion_horarios_detalle 
                WHERE id_detalle = v_id_detalle;
            END IF;

        ELSEIF p_tipo_accion = 'ASIGNAR_DESCANSO' THEN
            -- ASIGNAR DESCANSO: Crear o actualizar con horas NULL
            IF v_id_detalle IS NULL THEN
                INSERT INTO asignacion_horarios_detalle(
                    id_asignacion, fecha, hora_inicio, hora_fin, es_excepcion
                )
                VALUES(
                    v_id_asignacion, v_fecha_actual, NULL, NULL, 1
                );
            ELSE
                UPDATE asignacion_horarios_detalle 
                SET hora_inicio = NULL, 
                    hora_fin = NULL, 
                    es_excepcion = 1 
                WHERE id_detalle = v_id_detalle;
            END IF;

        ELSEIF p_tipo_accion = 'ASIGNAR_HORARIO' THEN
            -- ASIGNAR HORARIO: Crear o actualizar con horario específico
            -- Obtener horario del horario base
            SELECT hora_inicio, hora_fin 
            INTO v_hora_inicio, v_hora_fin 
            FROM horarios_base 
            WHERE id = p_id_horario_base;

            -- Validar que no sea horario de descanso
            IF v_hora_inicio IS NULL THEN
                ROLLBACK;
                SELECT 'ERROR' AS status, 
                       'El horario seleccionado es un horario de descanso' AS mensaje;
                LEAVE proc_label;
            END IF;

            -- Insertar o actualizar detalle
            IF v_id_detalle IS NULL THEN
                INSERT INTO asignacion_horarios_detalle(
                    id_asignacion, fecha, hora_inicio, hora_fin, es_excepcion
                )
                VALUES(
                    v_id_asignacion, v_fecha_actual, v_hora_inicio, v_hora_fin, 1
                );
            ELSE
                UPDATE asignacion_horarios_detalle 
                SET hora_inicio = v_hora_inicio, 
                    hora_fin = v_hora_fin, 
                    es_excepcion = 1 
                WHERE id_detalle = v_id_detalle;
            END IF;
        END IF;

        -- Avanzar al siguiente día
        SET v_fecha_actual = DATE_ADD(v_fecha_actual, INTERVAL 1 DAY);
        SET v_id_asignacion = NULL; -- Reset para el siguiente día
    END WHILE;

    -- Confirmar transacción
    COMMIT;

    -- Mensaje de éxito
    SELECT 'OK' AS status,
           CONCAT(
               CASE p_tipo_accion
                   WHEN 'RESTAURAR_BASE'   THEN 'Horario base restaurado'
                   WHEN 'ASIGNAR_DESCANSO' THEN 'Descanso asignado'
                   WHEN 'ASIGNAR_HORARIO'  THEN 'Horario asignado'
               END,
               ' del ', DATE_FORMAT(p_fecha_inicio, '%d/%m/%Y'),
               ' al ', DATE_FORMAT(p_fecha_fin, '%d/%m/%Y'),
               IF(v_horario_creado, ' (horarios base creados automaticamente)', '')
           ) AS mensaje;

END$$

DELIMITER ;
-- ========================================
-- PROCEDIMIENTO: gestionar_dia_especial
-- Controla la asignacion, desasignacion o un descanso por dia
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_dia_especial;
DELIMITER $$

CREATE PROCEDURE gestionar_dia_especial(
    IN p_id_colaborador BIGINT,
    IN p_fecha          DATE,
    IN p_tipo_accion    VARCHAR(20),
    IN p_hora_inicio    TIME,
    IN p_hora_fin       TIME,
    IN p_id_usuario     BIGINT
)
gestionDia: BEGIN
    DECLARE v_id_asignacion       BIGINT DEFAULT NULL;
    DECLARE v_detalle_existente   BIGINT DEFAULT NULL;
    DECLARE v_asistencia_existente BIGINT DEFAULT NULL;
    DECLARE v_tipo_accion         VARCHAR(20);
    DECLARE v_dia_semana          INT;
    DECLARE v_nombre_dia          VARCHAR(20);
    DECLARE v_error_msg           VARCHAR(500);

    -- SOLO UN EXIT HANDLER
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @sqlstate = RETURNED_SQLSTATE,
            @errno = MYSQL_ERRNO,
            @text = MESSAGE_TEXT;
        
        ROLLBACK;
        
        SELECT 'ERROR' AS status, 
               CONCAT('Error SQL [', @errno, ']: ', @text) AS mensaje;
    END;

    -- ========================================
    -- VALIDACIONES INICIALES
    -- ========================================
    SET v_tipo_accion = UPPER(TRIM(p_tipo_accion));

    IF v_tipo_accion NOT IN ('HORARIO','DESCANSO','DESASIGNAR') THEN
        SELECT 'ERROR' AS status, 'Accion invalida. Use: HORARIO, DESCANSO o DESASIGNAR.' AS mensaje;
        LEAVE gestionDia;
    END IF;

    IF p_id_colaborador IS NULL OR p_fecha IS NULL THEN
        SELECT 'ERROR' AS status, 'Faltan datos obligatorios (colaborador, fecha).' AS mensaje;
        LEAVE gestionDia;
    END IF;

    IF v_tipo_accion = 'HORARIO' THEN
        IF p_hora_inicio IS NULL OR p_hora_fin IS NULL THEN
            SELECT 'ERROR' AS status, 'Para accion HORARIO se requieren hora_inicio y hora_fin.' AS mensaje;
            LEAVE gestionDia;
        END IF;
        IF p_hora_fin <= p_hora_inicio THEN
            SELECT 'ERROR' AS status, 'hora_fin debe ser mayor que hora_inicio.' AS mensaje;
            LEAVE gestionDia;
        END IF;
    END IF;

    -- ========================================
    -- OBTENER DIA DE LA SEMANA
    -- ========================================
    SET v_dia_semana = CASE DAYOFWEEK(p_fecha)
                           WHEN 1 THEN 7
                           ELSE DAYOFWEEK(p_fecha) - 1
                       END;

    SELECT nombre INTO v_nombre_dia
    FROM dias_semana
    WHERE id = v_dia_semana;

    -- ========================================
    -- BUSCAR ASIGNACION BASE ACTIVA
    -- ========================================
    SELECT ah.id INTO v_id_asignacion
    FROM asignacion_horarios ah
    WHERE ah.id_colaborador = p_id_colaborador
      AND ah.id_dia_semana = v_dia_semana
      AND ah.fecha_inicio_vigencia <= p_fecha
      AND (ah.fecha_fin_vigencia IS NULL OR ah.fecha_fin_vigencia >= p_fecha)
      AND ah.activo = 1
    LIMIT 1;

    IF v_id_asignacion IS NULL THEN
        SET v_error_msg = CONCAT(
            'No existe horario base asignado para ',
            v_nombre_dia,
            ' (', DATE_FORMAT(p_fecha, '%d/%m/%Y'), ')',
            '. Debe asignar primero un horario base para este dia.'
        );
        
        SELECT 'ERROR' AS status, v_error_msg AS mensaje;
        LEAVE gestionDia;
    END IF;

    -- ========================================
    -- VERIFICAR DETALLE EXISTENTE
    -- ========================================
    SELECT id_detalle INTO v_detalle_existente
    FROM asignacion_horarios_detalle
    WHERE id_asignacion = v_id_asignacion
      AND fecha = p_fecha
    LIMIT 1;

    -- ========================================
    -- VERIFICAR ASISTENCIA YA REGISTRADA
    -- ========================================
    SELECT id INTO v_asistencia_existente
    FROM registro_asistencias
    WHERE id_colaborador = p_id_colaborador
      AND fecha = p_fecha
    LIMIT 1;

    IF v_asistencia_existente IS NOT NULL THEN
        SET v_error_msg = CONCAT('No se puede modificar el dia ', DATE_FORMAT(p_fecha, '%d/%m/%Y'), ': ya existe asistencia registrada.');
        SELECT 'ERROR' AS status, v_error_msg AS mensaje;
        LEAVE gestionDia;
    END IF;

    -- ========================================
    -- INICIAR TRANSACCION
    -- ========================================
    START TRANSACTION;

    -- ========================================
    -- ACCION: DESASIGNAR
    -- ========================================
    IF v_tipo_accion = 'DESASIGNAR' THEN
        IF v_detalle_existente IS NOT NULL THEN
            DELETE FROM asignacion_horarios_detalle WHERE id_detalle = v_detalle_existente;
            COMMIT;
            SELECT 'OK' AS status, 'Excepcion eliminada - vuelve al horario base' AS mensaje;
            LEAVE gestionDia;
        ELSE
            COMMIT;
            SELECT 'OK' AS status, 'No habia excepcion - ya usa horario base' AS mensaje;
            LEAVE gestionDia;
        END IF;
    END IF;

    -- ========================================
    -- ACCION: DESCANSO
    -- ========================================
    IF v_tipo_accion = 'DESCANSO' THEN
        IF v_detalle_existente IS NULL THEN
            INSERT INTO asignacion_horarios_detalle (id_asignacion, fecha, hora_inicio, hora_fin, es_excepcion)
            VALUES (v_id_asignacion, p_fecha, NULL, NULL, 1);
        ELSE
            UPDATE asignacion_horarios_detalle
            SET hora_inicio = NULL,
                hora_fin    = NULL,
                es_excepcion = 1,
                actualizado_en = NOW()
            WHERE id_detalle = v_detalle_existente;
        END IF;

        COMMIT;
        SELECT 'OK' AS status, 'Descanso registrado correctamente' AS mensaje;
        LEAVE gestionDia;
    END IF;

    -- ========================================
    -- ACCION: HORARIO PERSONALIZADO
    -- ========================================
    IF v_tipo_accion = 'HORARIO' THEN
        IF v_detalle_existente IS NULL THEN
            INSERT INTO asignacion_horarios_detalle (id_asignacion, fecha, hora_inicio, hora_fin, es_excepcion)
            VALUES (v_id_asignacion, p_fecha, p_hora_inicio, p_hora_fin, 1);
        ELSE
            UPDATE asignacion_horarios_detalle
            SET hora_inicio = p_hora_inicio,
                hora_fin    = p_hora_fin,
                es_excepcion = 1,
                actualizado_en = NOW()
            WHERE id_detalle = v_detalle_existente;
        END IF;

        COMMIT;
        SELECT 'OK' AS status, 
               CONCAT('Horario personalizado ', TIME_FORMAT(p_hora_inicio,'%H:%i'), '-', TIME_FORMAT(p_hora_fin,'%H:%i'), ' asignado') AS mensaje;
        LEAVE gestionDia;
    END IF;

END gestionDia$$
DELIMITER ;

-- ========================================
-- CONSULTAR HISTORIAL DE CAMBIOS
-- Ver todos los cambios de horario de un colaborador
-- ========================================
DROP PROCEDURE IF EXISTS consultar_historial_horarios;
DELIMITER $$

CREATE PROCEDURE consultar_historial_horarios (
    IN p_id_colaborador BIGINT,
    IN p_id_dia_semana INT          -- NULL = todos los días
)
BEGIN
    SELECT 
        ah.id,
        c.id AS id_colaborador,
        e.nombre AS colaborador,
        ds.nombre AS dia,
        hb.nombre AS horario,
        CONCAT(
            TIME_FORMAT(hb.hora_inicio, '%H:%i'), 
            ' - ', 
            TIME_FORMAT(hb.hora_fin, '%H:%i')
        ) AS rango_horario,
        DATE_FORMAT(ah.fecha_inicio_vigencia, '%d/%m/%Y') AS desde,
        IFNULL(DATE_FORMAT(ah.fecha_fin_vigencia, '%d/%m/%Y'), 'ACTUAL') AS hasta,
        DATEDIFF(
            IFNULL(ah.fecha_fin_vigencia, CURDATE()), 
            ah.fecha_inicio_vigencia
        ) AS dias_vigencia,
        ah.motivo_cambio,
        IF(ah.activo = 1, 'ACTIVO', 'INACTIVO') AS estado,
        DATE_FORMAT(ah.fecha_asignacion, '%d/%m/%Y %H:%i') AS fecha_registro
    FROM asignacion_horarios ah
    JOIN colaboradores c ON c.id = ah.id_colaborador
    JOIN entidades e ON e.id = c.id_entidad
    JOIN dias_semana ds ON ds.id = ah.id_dia_semana
    JOIN horarios_base hb ON hb.id = ah.id_horario_base
    WHERE ah.id_colaborador = p_id_colaborador
      AND (p_id_dia_semana IS NULL OR ah.id_dia_semana = p_id_dia_semana)
    ORDER BY ah.fecha_inicio_vigencia DESC, ds.orden;
END$$
DELIMITER ;

-- ========================================
-- VER HORARIOS VIGENTES ACTUALES
-- Muestra qué horario tiene cada colaborador AHORA
-- ========================================
DROP PROCEDURE IF EXISTS ver_horarios_vigentes;
DELIMITER $$

CREATE PROCEDURE ver_horarios_vigentes (
    IN p_id_colaborador BIGINT
)
BEGIN
    SELECT 
        c.id AS id_colaborador,
        e.nombre AS colaborador,
        ds.nombre AS dia,
        hb.nombre AS horario,
        CASE 
            WHEN hb.hora_inicio IS NULL THEN 'DESCANSO'
            ELSE CONCAT(
                TIME_FORMAT(hb.hora_inicio, '%H:%i'), 
                ' - ', 
                TIME_FORMAT(hb.hora_fin, '%H:%i')
            )
        END AS rango,
        DATE_FORMAT(ah.fecha_inicio_vigencia, '%d/%m/%Y') AS vigente_desde,
        DATEDIFF(CURDATE(), ah.fecha_inicio_vigencia) AS dias_con_este_horario,
        CASE 
            WHEN ah.fecha_fin_vigencia IS NULL THEN 'INDEFINIDO'
            ELSE DATE_FORMAT(ah.fecha_fin_vigencia, '%d/%m/%Y')
        END AS vigente_hasta
    FROM asignacion_horarios ah
    JOIN colaboradores c ON c.id = ah.id_colaborador
    JOIN entidades e ON e.id = c.id_entidad
    JOIN dias_semana ds ON ds.id = ah.id_dia_semana
    JOIN horarios_base hb ON hb.id = ah.id_horario_base
    WHERE ah.activo = 1
      -- Muestra horarios vigentes HOY (con o sin fecha fin)
      AND ah.fecha_inicio_vigencia <= CURDATE()
      AND (ah.fecha_fin_vigencia IS NULL OR ah.fecha_fin_vigencia >= CURDATE())
      AND (p_id_colaborador IS NULL OR c.id = p_id_colaborador)
    ORDER BY e.nombre, ds.orden;
END$$
DELIMITER ;
-- ========================================
-- RESUMEN DE HORARIOS POR COLABORADOR
-- Vista consolidada de la semana completa
-- ========================================
DROP PROCEDURE IF EXISTS resumen_horarios_colaborador;
DELIMITER $$

CREATE PROCEDURE resumen_horarios_colaborador (
    IN p_id_colaborador BIGINT
)
BEGIN
    SELECT 
        c.id,
        e.nombre AS colaborador,
        MAX(CASE WHEN ds.id = 1 THEN hb.nombre END) AS lunes,
        MAX(CASE WHEN ds.id = 2 THEN hb.nombre END) AS martes,
        MAX(CASE WHEN ds.id = 3 THEN hb.nombre END) AS miercoles,
        MAX(CASE WHEN ds.id = 4 THEN hb.nombre END) AS jueves,
        MAX(CASE WHEN ds.id = 5 THEN hb.nombre END) AS viernes,
        MAX(CASE WHEN ds.id = 6 THEN hb.nombre END) AS sabado,
        MAX(CASE WHEN ds.id = 7 THEN hb.nombre END) AS domingo
    FROM colaboradores c
    JOIN entidades e ON e.id = c.id_entidad
    LEFT JOIN asignacion_horarios ah ON ah.id_colaborador = c.id 
        AND ah.activo = 1 
        AND ah.fecha_fin_vigencia IS NULL
    LEFT JOIN dias_semana ds ON ds.id = ah.id_dia_semana
    LEFT JOIN horarios_base hb ON hb.id = ah.id_horario_base
    WHERE c.id = p_id_colaborador
    GROUP BY c.id, e.nombre;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: gestionar_asistencia_automatica
-- Registra marcaciones con asignación automática del horario vigente
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_asistencia;
DELIMITER $$
CREATE PROCEDURE gestionar_asistencia(
    IN p_colaborador_id BIGINT,
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_tipo_movimiento VARCHAR(20),
    OUT p_mensaje VARCHAR(500),
    OUT p_success TINYINT(1),
    OUT p_tardanza_minutos INT,
    OUT p_estado_final VARCHAR(50)
)
proc_main: BEGIN
    DECLARE v_dia_semana_id INT;
    DECLARE v_horario_base_id INT;
    DECLARE v_hora_inicio TIME;
    DECLARE v_tolerancia_min INT DEFAULT 0;
    DECLARE v_minutos_lunch_std INT DEFAULT 60;
    DECLARE v_id_registro BIGINT DEFAULT NULL;
    DECLARE v_est_presente INT;
    DECLARE v_est_tardanza INT;
    DECLARE v_est_completado INT;
    DECLARE v_est_descanso INT;
    DECLARE v_entrada TIME DEFAULT NULL;
    DECLARE v_lunch_in TIME DEFAULT NULL;
    DECLARE v_lunch_out TIME DEFAULT NULL;
    DECLARE v_salida TIME DEFAULT NULL;
    DECLARE v_tolerancia_time TIME;
    DECLARE v_es_tarde BOOLEAN DEFAULT FALSE;
    DECLARE v_lunch_real INT DEFAULT 60;
    DECLARE v_nombre_colaborador VARCHAR(128);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = 0;
        SET p_mensaje = 'Error interno en gestionar_asistencia';
        SET p_tardanza_minutos = 0;
        SET p_estado_final = '';
        
        SELECT 
            p_mensaje AS mensaje,
            p_success AS success,
            0 AS tardanza_minutos,
            '' AS estado_final,
            p_colaborador_id AS id_colaborador,
            NULL AS colaborador,
            p_hora AS hora_marcacion,
            p_tipo_movimiento AS tipo_marca;
    END;

    START TRANSACTION;

    SET v_dia_semana_id = WEEKDAY(p_fecha) + 1;

    SELECT id INTO v_est_presente FROM estado_asistencia WHERE nombre = 'PRESENTE' LIMIT 1;
    SELECT id INTO v_est_tardanza FROM estado_asistencia WHERE nombre = 'TARDANZA' LIMIT 1;
    SELECT id INTO v_est_completado FROM estado_asistencia WHERE nombre = 'COMPLETADO' LIMIT 1;
    SELECT id INTO v_est_descanso FROM estado_asistencia WHERE nombre = 'DESCANSO_SEMANAL' LIMIT 1;

    SELECT e.nombre INTO v_nombre_colaborador
    FROM colaboradores c
    JOIN entidades e ON e.id = c.id_entidad
    WHERE c.id = p_colaborador_id;

    SELECT ah.id_horario_base, hb.hora_inicio, COALESCE(hb.minutos_tolerancia_entrada,0), COALESCE(hb.minutos_lunch,60)
      INTO v_horario_base_id, v_hora_inicio, v_tolerancia_min, v_minutos_lunch_std
      FROM asignacion_horarios ah
      JOIN horarios_base hb ON hb.id = ah.id_horario_base
     WHERE ah.id_colaborador = p_colaborador_id
       AND ah.id_dia_semana = v_dia_semana_id
       AND ah.activo = 1
       AND p_fecha BETWEEN ah.fecha_inicio_vigencia AND COALESCE(ah.fecha_fin_vigencia,'9999-12-31')
     ORDER BY ah.fecha_inicio_vigencia DESC LIMIT 1;

    IF v_horario_base_id IS NULL THEN
        IF NOT EXISTS (SELECT 1 FROM registro_asistencias WHERE id_colaborador = p_colaborador_id AND fecha = p_fecha) THEN
            INSERT INTO registro_asistencias(id_colaborador, fecha, id_estado_asistencia, observaciones)
            VALUES (p_colaborador_id, p_fecha, v_est_descanso, 'Descanso semanal programado');
        END IF;
        
        SET p_success = 0;
        SET p_mensaje = 'Dia de descanso programado';
        SET p_tardanza_minutos = 0;
        SET p_estado_final = 'DESCANSO_SEMANAL';
        
        COMMIT;
        
        SELECT 
            p_mensaje AS mensaje,
            p_success AS success,
            0 AS tardanza_minutos,
            p_estado_final AS estado_final,
            p_colaborador_id AS id_colaborador,
            v_nombre_colaborador AS colaborador,
            p_hora AS hora_marcacion,
            p_tipo_movimiento AS tipo_marca;
        
        LEAVE proc_main;
    END IF;

    SELECT id, hora_entrada, hora_lunch_inicio, hora_lunch_fin, hora_salida
      INTO v_id_registro, v_entrada, v_lunch_in, v_lunch_out, v_salida
      FROM registro_asistencias
     WHERE id_colaborador = p_colaborador_id AND fecha = p_fecha;

    IF v_id_registro IS NULL THEN
        INSERT INTO registro_asistencias(id_colaborador, fecha, id_horario_base, id_estado_asistencia)
        VALUES (p_colaborador_id, p_fecha, v_horario_base_id, v_est_presente);
        SET v_id_registro = LAST_INSERT_ID();
    END IF;

    SET v_tolerancia_time = SEC_TO_TIME(v_tolerancia_min * 60);
    SET p_tardanza_minutos = 0;
    SET v_lunch_real = v_minutos_lunch_std;

    CASE UPPER(p_tipo_movimiento)
        WHEN 'ENTRADA' THEN
            IF v_entrada IS NOT NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Ya hay entrada registrada hoy';
            ELSE
                IF p_hora > ADDTIME(v_hora_inicio, v_tolerancia_time) THEN
                    SET v_es_tarde = TRUE;
                    SET p_tardanza_minutos = TIMESTAMPDIFF(MINUTE, ADDTIME(v_hora_inicio, v_tolerancia_time), p_hora);
                END IF;
                
                UPDATE registro_asistencias SET
                    hora_entrada = p_hora,
                    tardanza_minutos = p_tardanza_minutos,
                    id_estado_asistencia = IF(v_es_tarde, v_est_tardanza, v_est_presente)
                WHERE id = v_id_registro;
                
                SET p_success = 1;
                SET p_mensaje = IF(v_es_tarde, CONCAT('Entrada con ', p_tardanza_minutos, ' min de tardanza'), 'Entrada puntual');
                SET p_estado_final = IF(v_es_tarde, 'TARDANZA', 'PRESENTE');
            END IF;

        WHEN 'LUNCH_IN' THEN
            IF v_lunch_in IS NOT NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Ya inicio almuerzo hoy';
            ELSEIF v_entrada IS NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Debe marcar entrada primero';
            ELSE
                UPDATE registro_asistencias SET hora_lunch_inicio = p_hora WHERE id = v_id_registro;
                SET p_success = 1;
                SET p_mensaje = 'Inicio de almuerzo registrado';
                SET p_estado_final = 'EN_ALMUERZO';
            END IF;

        WHEN 'LUNCH_OUT' THEN
            IF v_lunch_out IS NOT NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Ya finalizo almuerzo hoy';
            ELSEIF v_lunch_in IS NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Debe iniciar almuerzo primero';
            ELSE
                SET v_lunch_real = TIMESTAMPDIFF(MINUTE, v_lunch_in, p_hora);
                UPDATE registro_asistencias SET hora_lunch_fin = p_hora, minutos_lunch = v_lunch_real WHERE id = v_id_registro;
                SET p_success = 1;
                SET p_mensaje = 'Fin de almuerzo registrado';
                SET p_estado_final = 'PRESENTE';
            END IF;

        WHEN 'SALIDA' THEN
            IF v_salida IS NOT NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'Ya registro salida hoy';
            ELSEIF v_entrada IS NULL THEN
                SET p_success = 0;
                SET p_mensaje = 'No puede salir sin entrada';
            ELSE
                IF v_lunch_in IS NOT NULL AND v_lunch_out IS NOT NULL THEN
                    SET v_lunch_real = TIMESTAMPDIFF(MINUTE, v_lunch_in, v_lunch_out);
                ELSEIF v_lunch_in IS NOT NULL AND v_lunch_out IS NULL THEN
                    SET v_lunch_real = v_minutos_lunch_std;
                ELSE
                    SET v_lunch_real = 0;
                END IF;
                
                UPDATE registro_asistencias SET
                    hora_salida = p_hora,
                    minutos_trabajados = TIMESTAMPDIFF(MINUTE, v_entrada, p_hora) - v_lunch_real,
                    minutos_lunch = v_lunch_real,
                    id_estado_asistencia = v_est_completado
                WHERE id = v_id_registro;
                
                SET p_success = 1;
                SET p_mensaje = 'Salida registrada - Jornada completada';
                SET p_estado_final = 'COMPLETADO';
            END IF;

        ELSE
            SET p_success = 0;
            SET p_mensaje = 'Tipo de movimiento invalido';
    END CASE;

    IF p_success = 1 THEN
        COMMIT;
    ELSE
        ROLLBACK;
    END IF;

    SELECT 
        p_mensaje AS mensaje,
        p_success AS success,
        COALESCE(p_tardanza_minutos, 0) AS tardanza_minutos,
        COALESCE(p_estado_final, 'PRESENTE') AS estado_final,
        p_colaborador_id AS id_colaborador,
        v_nombre_colaborador AS colaborador,
        p_hora AS hora_marcacion,
        p_tipo_movimiento AS tipo_marca;

END proc_main$$
DELIMITER ;
-- ========================================
-- SP: Reporte de asistencias por rango de fechas
-- ========================================
DROP PROCEDURE IF EXISTS ver_asistencia_por_rango;
DELIMITER $$
CREATE PROCEDURE ver_asistencia_por_rango(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_id_colaborador BIGINT, 
    IN p_id_estado INT           
)
BEGIN
    WITH RECURSIVE calendario AS (
        SELECT p_fecha_inicio AS fecha
        UNION ALL
        SELECT fecha + INTERVAL 1 DAY
        FROM calendario
        WHERE fecha + INTERVAL 1 DAY <= p_fecha_fin
    ),
    horarios_vigentes AS (
        SELECT 
            col.id AS id_colaborador,
            CONCAT(col.nombres, ' ', col.apellidos) AS colaborador,
            cal.fecha,
            CASE DAYOFWEEK(cal.fecha) WHEN 1 THEN 7 ELSE DAYOFWEEK(cal.fecha)-1 END AS id_dia_semana,
            ds.nombre AS dia_semana,
            ah.id_horario_base,
            hb.nombre AS horario_programado,
            hb.hora_inicio,
            hb.hora_fin,
            COALESCE(hd.hora_inicio, hb.hora_inicio) AS hora_inicio_real,
            COALESCE(hd.hora_fin, hb.hora_fin) AS hora_fin_real,
            IF(hb.id IS NULL OR (hd.hora_inicio IS NULL AND hd.hora_fin IS NULL), 1, 0) AS es_descanso
        FROM colaboradores col
        CROSS JOIN calendario cal
        LEFT JOIN dias_semana ds ON ds.id = WEEKDAY(cal.fecha)+1
        LEFT JOIN asignacion_horarios ah ON ah.id_colaborador = col.id
            AND ah.id_dia_semana = WEEKDAY(cal.fecha)+1
            AND ah.activo = 1
            AND cal.fecha BETWEEN ah.fecha_inicio_vigencia AND COALESCE(ah.fecha_fin_vigencia, '9999-12-31')
            AND ah.fecha_inicio_vigencia = (
                SELECT MAX(ah2.fecha_inicio_vigencia) 
                FROM asignacion_horarios ah2 
                WHERE ah2.id_colaborador = col.id 
                  AND ah2.id_dia_semana = WEEKDAY(cal.fecha)+1
                  AND ah2.activo = 1
                  AND cal.fecha >= ah2.fecha_inicio_vigencia
            )
        LEFT JOIN horarios_base hb ON hb.id = ah.id_horario_base
        LEFT JOIN asignacion_horarios_detalle hd ON hd.id_asignacion = ah.id AND hd.fecha = cal.fecha
        WHERE (p_id_colaborador IS NULL OR col.id = p_id_colaborador)
    )
    SELECT 
        hv.id_colaborador,
        hv.colaborador,
        hv.fecha,
        hv.dia_semana,
        IF(hv.es_descanso=1, 'Descanso', hv.horario_programado) AS horario,
        ra.hora_entrada,
        ra.hora_lunch_inicio,
        ra.hora_lunch_fin,
        ra.hora_salida,
        ra.minutos_trabajados,
        ra.minutos_lunch,
        ra.tardanza_minutos,
        COALESCE(ea.nombre, 
            IF(hv.es_descanso=1, 'DESCANSO_SEMANAL',
                IF(ra.id IS NULL, 'PENDIENTE', 'PRESENTE')
            )
        ) AS estado,
        hv.hora_inicio_real AS programada_inicio,
        hv.hora_fin_real AS programada_fin,
        ra.observaciones
    FROM horarios_vigentes hv
    LEFT JOIN registro_asistencias ra ON ra.id_colaborador = hv.id_colaborador AND ra.fecha = hv.fecha
    LEFT JOIN estado_asistencia ea ON ea.id = ra.id_estado_asistencia
    WHERE (p_id_estado IS NULL OR ra.id_estado_asistencia = p_id_estado)
    ORDER BY hv.fecha DESC, hv.colaborador;
END$$
DELIMITER ;
-- ===========================================================================================================================================
-- ===========================================================================================================================================
-- ===========================================================================================================================================

-- BLOQUE 02 PROCEDIMIENTOS ALMACENADOS CRUD

-- USE vet_manada_woof;
-- ========================================
-- SP: REGISTRAR_MASCOTA
-- Registra una nueva mascota validando cliente, duplicados y consistencia de datos.
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
    DECLARE v_nuevo_id BIGINT;
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_nombre_medicamento VARCHAR(64);
    DECLARE v_sqlstate CHAR(5);
    DECLARE v_sqlmsg TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            v_sqlstate = RETURNED_SQLSTATE,
            v_sqlmsg = MESSAGE_TEXT;
        ROLLBACK;
        SET p_mensaje = CONCAT('ERROR SQL: [', v_sqlstate, '] ', v_sqlmsg);
    END;

    START TRANSACTION;

    -- Validar existencia de mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de medicamento
    IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id_medicamento) THEN
        SET p_mensaje = 'ERROR: Medicamento no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de vía de aplicación
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar duplicado
    IF EXISTS (
        SELECT 1 FROM medicamentos_mascota
        WHERE id_mascota = p_id_mascota
          AND id_medicamento = p_id_medicamento
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro para este medicamento en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- ========================================
    -- INSERT primero con codigo temporal NULL
    -- Luego LAST_INSERT_ID() para generar código
    -- ========================================
    INSERT INTO medicamentos_mascota (
        codigo, id_mascota, id_medicamento, id_via, dosis,
        fecha_aplicacion, id_colaborador, id_veterinario, observaciones
    ) VALUES (
        'TEMP',
        p_id_mascota, p_id_medicamento, p_id_via, p_dosis,
        p_fecha_aplicacion, p_id_colaborador, p_id_veterinario, p_observaciones
    );

    SET v_nuevo_id = LAST_INSERT_ID();
    SET v_codigo_registro = CONCAT('MEDM', LPAD(v_nuevo_id, 6, '0'));

    UPDATE medicamentos_mascota
    SET codigo = v_codigo_registro
    WHERE id = v_nuevo_id;

    -- Obtener nombre del medicamento para el mensaje
    SELECT nombre INTO v_nombre_medicamento
    FROM medicamentos
    WHERE id = p_id_medicamento;

    COMMIT;

    SET p_mensaje = CONCAT(
        'Medicamento "', v_nombre_medicamento,
        '" registrado correctamente. Código del registro: ', v_codigo_registro, '.'
    );

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
    IN p_id_colaborador BIGINT,          
    IN p_id_veterinario BIGINT,          
    IN p_observaciones VARCHAR(64),      
    IN p_activo TINYINT,                 
    OUT p_mensaje VARCHAR(255)          
)
proc_main: BEGIN
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_codigo_mascota VARCHAR(16);
    DECLARE v_nombre_medicamento VARCHAR(64);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1 
            p_mensaje = MESSAGE_TEXT;
    END;

    START TRANSACTION;

    IF NOT EXISTS (SELECT 1 FROM medicamentos_mascota WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

SELECT 
    codigo
INTO v_codigo_registro FROM
    medicamentos_mascota
WHERE
    id = p_id_registro;
SELECT 
    codigo
INTO v_codigo_mascota FROM
    mascotas
WHERE
    id = p_id_mascota;
SELECT 
    nombre
INTO v_nombre_medicamento FROM
    medicamentos
WHERE
    id = p_id_medicamento;

UPDATE medicamentos_mascota 
SET 
    id_mascota = p_id_mascota,
    id_medicamento = p_id_medicamento,
    id_via = p_id_via,
    dosis = p_dosis,
    fecha_aplicacion = p_fecha_aplicacion,
    id_colaborador = p_id_colaborador,
    id_veterinario = p_id_veterinario,
    observaciones = p_observaciones,
    fecha_modificacion = NOW(),
    activo = p_activo
WHERE
    id = p_id_registro;

    COMMIT;

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
    IN p_id_mascota BIGINT,
    IN p_id_vacuna INT,
    IN p_id_via INT,
    IN p_dosis VARCHAR(32),
    IN p_fecha_aplicacion DATE,
    IN p_durabilidad_anios INT,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_observaciones VARCHAR(64),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_nuevo_id BIGINT;
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_nombre_vacuna VARCHAR(64);
    DECLARE v_sqlstate CHAR(5);
    DECLARE v_sqlmsg TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            v_sqlstate = RETURNED_SQLSTATE,
            v_sqlmsg = MESSAGE_TEXT;
        ROLLBACK;
        SET p_mensaje = CONCAT('ERROR SQL: [', v_sqlstate, '] ', v_sqlmsg);
    END;

    START TRANSACTION;

    -- Validar existencia de mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de vacuna
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de vía
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar duplicado
    IF EXISTS (
        SELECT 1 FROM vacunas_mascota
        WHERE id_mascota = p_id_mascota
          AND id_vacuna = p_id_vacuna
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro de esta vacuna en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- ========================================
    -- INSERT primero con codigo temporal
    -- Luego LAST_INSERT_ID() para generar código
    -- ========================================
    INSERT INTO vacunas_mascota (
        codigo, id_mascota, id_vacuna, id_via, dosis,
        fecha_aplicacion, durabilidad_anios, proxima_dosis,
        id_colaborador, id_veterinario, observaciones, activo
    ) VALUES (
        'TEMP',
        p_id_mascota, p_id_vacuna, p_id_via, p_dosis,
        p_fecha_aplicacion,
        p_durabilidad_anios,
        CASE
            WHEN p_durabilidad_anios IS NOT NULL AND p_durabilidad_anios > 0
            THEN DATE_ADD(p_fecha_aplicacion, INTERVAL p_durabilidad_anios YEAR)
            ELSE NULL
        END,
        p_id_colaborador, p_id_veterinario, p_observaciones, 1
    );

    SET v_nuevo_id = LAST_INSERT_ID();
    SET v_codigo_registro = CONCAT('VACM', LPAD(v_nuevo_id, 6, '0'));

    UPDATE vacunas_mascota
    SET codigo = v_codigo_registro
    WHERE id = v_nuevo_id;

    -- Obtener nombre de la vacuna para el mensaje
    SELECT nombre INTO v_nombre_vacuna
    FROM vacunas
    WHERE id = p_id_vacuna;

    COMMIT;

    SET p_mensaje = CONCAT(
        'Vacuna "', v_nombre_vacuna,
        '" registrada correctamente. Código del registro: ', v_codigo_registro, '.'
    );

END$$
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
        GET DIAGNOSTICS CONDITION 1
            p_mensaje = MESSAGE_TEXT;
    END;

    START TRANSACTION;

    -- Validar existencia del registro
    IF NOT EXISTS (SELECT 1 FROM vacunas_mascota WHERE id = p_id_vacuna_mascota) THEN
        SET p_mensaje = 'ERROR: Registro de vacuna no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de vacuna
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar existencia de vía
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Obtener datos para el mensaje final
    SELECT vm.codigo, v.nombre, m.nombre
    INTO v_codigo, v_nombre_vacuna, v_nombre_mascota
    FROM vacunas_mascota vm
    JOIN vacunas v ON vm.id_vacuna = v.id
    JOIN mascotas m ON vm.id_mascota = m.id
    WHERE vm.id = p_id_vacuna_mascota;

    -- UPDATE consolidado en una sola operación
    UPDATE vacunas_mascota
    SET id_vacuna         = p_id_vacuna,
        id_via            = p_id_via,
        dosis             = p_dosis,
        fecha_aplicacion  = p_fecha_aplicacion,
        durabilidad_anios = p_durabilidad_anios,
        proxima_dosis     = p_proxima_dosis,
        id_colaborador    = p_id_colaborador,
        id_veterinario    = p_id_veterinario,
        observaciones     = p_observaciones,
        activo            = p_activo,
        fecha_modificacion = NOW()
    WHERE id = p_id_vacuna_mascota;

    COMMIT;

    -- Mensaje adaptado según acción
    IF p_activo = 0 THEN
        SET p_mensaje = CONCAT(
            'Vacuna "', v_nombre_vacuna,
            '" para la mascota "', v_nombre_mascota,
            '" desactivada. Código: ', v_codigo
        );
    ELSE
        SET p_mensaje = CONCAT(
            'Vacuna "', v_nombre_vacuna,
            '" actualizada correctamente. Código: ', v_codigo
        );
    END IF;

END$$
DELIMITER ;

-- ===========================================================================================================================================
-- ===========================================================================================================================================
-- ==========================================
--  GESTIONAR AGENDA (CREAR/ACTUALIZAR CITAS)
-- ==========================================
DROP PROCEDURE IF EXISTS gestionar_agenda;
DELIMITER $$
CREATE PROCEDURE gestionar_agenda(
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
    
    -- VALIDACIONES GENERALES
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
        
        SELECT COUNT(*) INTO v_existe_cita
        FROM agenda
        WHERE id_mascota = p_id_mascota
          AND fecha = p_fecha
          AND hora = p_hora
          AND id_estado NOT IN (4, 6);
        
        IF v_existe_cita > 0 THEN
            SET p_mensaje = 'ERROR: Ya existe una cita para esta mascota en la misma fecha y hora.';
            LEAVE main_block;
        END IF;
        
        SET v_nuevo_codigo = CONCAT('AG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        WHILE EXISTS (SELECT 1 FROM agenda WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('AG-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO agenda (
			codigo, id_cliente, id_mascota, id_medio_solicitud, fecha, hora,
			duracion_estimada_min, abono_inicial, total_cita, id_estado, observaciones
		) VALUES (
			'TEMP', p_id_cliente, p_id_mascota, p_id_medio_solicitud,
			p_fecha, p_hora, p_duracion_estimada_min, p_abono_inicial, 0, p_id_estado, p_observaciones
		);

		SET p_id_resultado = LAST_INSERT_ID();
		SET p_codigo = CONCAT('AG-', LPAD(p_id_resultado, 6, '0'));

		UPDATE agenda SET codigo = p_codigo WHERE id = p_id_resultado;
    
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
        
        SELECT id_estado INTO v_estado_actual FROM agenda WHERE id = p_id_agenda;
        
        IF v_estado_actual IN (4, 5) THEN
            SET p_mensaje = 'ERROR: No se puede actualizar una cita cancelada o atendida.';
            LEAVE main_block;
        END IF;
        
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
-- GESTIONAR INGRESO SERVICIO (CREAR/ACTUALIZAR/ELIMINAR)
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_ingreso_servicio;
DELIMITER $$
CREATE PROCEDURE gestionar_ingreso_servicio(
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
    
    -- VALIDACIONES GENERALES
    IF p_accion NOT IN ('CREAR', 'ACTUALIZAR', 'ELIMINAR') THEN
        SET p_mensaje = 'ERROR: Acción no válida. Use "CREAR", "ACTUALIZAR" o "ELIMINAR".';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Cita no existe.';
        LEAVE main_block;
    END IF;
    
    SELECT id_estado INTO v_estado_cita FROM agenda WHERE id = p_id_agenda;
    
    IF v_estado_cita IN (4, 5) THEN
        SET p_mensaje = 'ERROR: No se pueden agregar servicios a una cita cancelada o atendida.';
        LEAVE main_block;
    END IF;
    
    -- CREAR NUEVO SERVICIO
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
        
        SELECT COUNT(*) INTO v_existe_servicio
        FROM ingresos_servicios
        WHERE id_agenda = p_id_agenda AND id_servicio = p_id_servicio;
        
        IF v_existe_servicio > 0 THEN
            SET p_mensaje = 'ERROR: Este servicio ya fue agregado a la cita. Use ACTUALIZAR para modificarlo.';
            LEAVE main_block;
        END IF;
        
        SET v_nuevo_codigo = CONCAT('IS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        WHILE EXISTS (SELECT 1 FROM ingresos_servicios WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('IS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO ingresos_servicios (
			codigo, id_agenda, id_servicio, id_colaborador, id_veterinario,
			cantidad, duracion_min, valor_servicio, observaciones
		) VALUES (
			'TEMP', p_id_agenda, p_id_servicio, p_id_colaborador, p_id_veterinario,
			p_cantidad, p_duracion_min, p_valor_servicio, p_observaciones
		);

		SET p_id_resultado = LAST_INSERT_ID();
		SET p_codigo = CONCAT('IS-', LPAD(p_id_resultado, 6, '0'));

		UPDATE ingresos_servicios SET codigo = p_codigo WHERE id = p_id_resultado;
        
        UPDATE agenda SET
            total_cita = (SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                         FROM ingresos_servicios WHERE id_agenda = p_id_agenda)
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        SET p_mensaje = CONCAT('Servicio ', v_nuevo_codigo, ' agregado exitosamente.');
    
    -- ACTUALIZAR SERVICIO EXISTENTE
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
        
        UPDATE agenda SET
            total_cita = (SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                         FROM ingresos_servicios WHERE id_agenda = p_id_agenda)
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        SET p_mensaje = CONCAT('Servicio ', p_codigo, ' actualizado exitosamente.');
    
    -- ELIMINAR SERVICIO
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
        
        UPDATE agenda SET
            total_cita = (SELECT COALESCE(SUM(cantidad * valor_servicio), 0)
                         FROM ingresos_servicios WHERE id_agenda = p_id_agenda)
        WHERE id = p_id_agenda;
        
        SELECT total_cita INTO p_nuevo_total_cita FROM agenda WHERE id = p_id_agenda;
        SET p_mensaje = CONCAT('Servicio ', p_codigo, ' eliminado exitosamente.');
    
    END IF;
    
END$$
DELIMITER ;

-- ========================================
-- GESTIONAR PAGO AGENDA (CREAR/ELIMINAR PAGOS)
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_pago_agenda;
DELIMITER $$
CREATE PROCEDURE gestionar_pago_agenda(
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
    
    -- VALIDACIONES GENERALES
    IF p_accion NOT IN ('CREAR', 'ELIMINAR') THEN
        SET p_mensaje = 'ERROR: Acción no válida. Use "CREAR" o "ELIMINAR".';
        LEAVE main_block;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Cita no existe.';
        LEAVE main_block;
    END IF;
    
    -- CREAR NUEVO PAGO
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
        
        SELECT total_cita, abono_inicial INTO v_total_cita, v_abono_actual
        FROM agenda WHERE id = p_id_agenda;
        
        IF v_total_cita = 0 THEN
            SET p_mensaje = 'ERROR: La cita no tiene servicios asociados. Agregue servicios antes de registrar pagos.';
            LEAVE main_block;
        END IF;
        
        SET v_nuevo_abono = v_abono_actual + p_monto;
        
        IF v_nuevo_abono > v_total_cita THEN
            SET p_mensaje = CONCAT('ERROR: El monto excede el total. Total: S/ ', v_total_cita, ', Ya abonado: S/ ', v_abono_actual);
            LEAVE main_block;
        END IF;
        
        SET v_nuevo_codigo = CONCAT('AB-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        
        WHILE EXISTS (SELECT 1 FROM agenda_pagos WHERE codigo = v_nuevo_codigo) DO
            SET v_nuevo_codigo = CONCAT('AB-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
        END WHILE;
        
        INSERT INTO agenda_pagos (
			codigo, id_agenda, id_medio_pago, id_usuario, monto, fecha_pago, observaciones
		) VALUES (
			'TEMP', p_id_agenda, p_id_medio_pago, p_id_usuario, p_monto, NOW(), p_observaciones
		);

		SET p_id_resultado = LAST_INSERT_ID();
		SET p_codigo = CONCAT('AB-', LPAD(p_id_resultado, 6, '0'));

		UPDATE agenda_pagos SET codigo = p_codigo WHERE id = p_id_resultado;
        
        SET p_total_abonado = v_nuevo_abono;
        SET p_saldo_pendiente = v_total_cita - v_nuevo_abono;
        SET p_mensaje = CONCAT('Pago ', v_nuevo_codigo, ' registrado exitosamente.');
    
    -- ELIMINAR PAGO
    ELSEIF p_accion = 'ELIMINAR' THEN
        
        IF p_id_pago IS NULL THEN
            SET p_mensaje = 'ERROR: Debe proporcionar el ID del pago a eliminar.';
            LEAVE main_block;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM agenda_pagos WHERE id = p_id_pago) THEN
            SET p_mensaje = 'ERROR: Pago no existe.';
            LEAVE main_block;
        END IF;
        
        SELECT codigo INTO p_codigo FROM agenda_pagos WHERE id = p_id_pago;
        
        DELETE FROM agenda_pagos WHERE id = p_id_pago;
        
        SET p_id_resultado = p_id_pago;
        
        SELECT COALESCE(SUM(monto), 0) INTO v_nuevo_abono
        FROM agenda_pagos WHERE id_agenda = p_id_agenda;
        
        UPDATE agenda SET abono_inicial = v_nuevo_abono WHERE id = p_id_agenda;
        
        SELECT total_cita INTO v_total_cita FROM agenda WHERE id = p_id_agenda;
        
        SET p_total_abonado = v_nuevo_abono;
        SET p_saldo_pendiente = v_total_cita - v_nuevo_abono;
        SET p_mensaje = CONCAT('Pago ', p_codigo, ' eliminado exitosamente.');
    
    END IF;
    
END$$
DELIMITER ;

-- ========================================
-- CREAR HISTORIA CLÍNICA (una vez por mascota)
-- ========================================
DROP PROCEDURE IF EXISTS crear_historia_clinica;
DELIMITER $$

CREATE PROCEDURE crear_historia_clinica(
    IN p_id_mascota BIGINT,
    IN p_observaciones_generales TEXT,
    OUT p_id_historia BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nuevo_codigo VARCHAR(16);
    
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
        LEAVE main_block;
    END IF;
    
    IF EXISTS (SELECT 1 FROM historia_clinica WHERE id_mascota = p_id_mascota) THEN
        SELECT id, codigo INTO p_id_historia, p_codigo
        FROM historia_clinica WHERE id_mascota = p_id_mascota;
        
        SET p_mensaje = CONCAT('ADVERTENCIA: La mascota ya tiene historia clínica: ', p_codigo);
        LEAVE main_block;
    END IF;
    
    SET v_nuevo_codigo = CONCAT('HIS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    
    WHILE EXISTS (SELECT 1 FROM historia_clinica WHERE codigo = v_nuevo_codigo) DO
        SET v_nuevo_codigo = CONCAT('HIS-', LPAD(FLOOR(1 + RAND() * 999999), 6, '0'));
    END WHILE;
    
    INSERT INTO historia_clinica (
		codigo, id_mascota, fecha_apertura, observaciones_generales, activo
	) VALUES (
		'TEMP', p_id_mascota, CURDATE(), p_observaciones_generales, 1
	);

	SET p_id_historia = LAST_INSERT_ID();
	SET p_codigo = CONCAT('HIS-', LPAD(p_id_historia, 6, '0'));

	UPDATE historia_clinica SET codigo = p_codigo WHERE id = p_id_historia;
    
END$$
DELIMITER ;

-- ========================================
-- registrar_cita_atendida
-- Esta es la FUNCIÓN CENTRAL para capturar TODAS las citas
-- ========================================
DROP PROCEDURE IF EXISTS registrar_cita_atendida;
DELIMITER $$
CREATE PROCEDURE registrar_cita_atendida(
    IN p_id_agenda BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_tipo_visita VARCHAR(32),
    IN p_motivo_consulta VARCHAR(256),
    IN p_anamnesis TEXT,
    IN p_examen_fisico TEXT,
    IN p_signos_vitales VARCHAR(256),
    IN p_peso_kg DECIMAL(6,2),
    IN p_temperatura_c DECIMAL(4,2),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_proximo_control DATE,
    IN p_estado_pelaje VARCHAR(128),
    IN p_condicion_piel VARCHAR(128),
    IN p_observaciones_grooming TEXT,
    IN p_comportamiento_hospedaje TEXT,
    IN p_alimentacion_hospedaje VARCHAR(256),
    IN p_actividad_hospedaje TEXT,
    IN p_observaciones TEXT,
    OUT p_id_registro BIGINT,
    OUT p_codigo_registro VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_id_mascota BIGINT;
    DECLARE v_id_cliente BIGINT;
    DECLARE v_id_historia BIGINT;
    DECLARE v_fecha_atencion DATE;
    DECLARE v_hora_inicio TIME;
    DECLARE v_estado_abierta INT;
    DECLARE v_total_cita DECIMAL(10,2);
    DECLARE v_abono_total DECIMAL(10,2);
    DECLARE v_estado_cita INT;
    DECLARE v_codigo_his VARCHAR(16);

    -- Validar que la cita existe
    IF NOT EXISTS (SELECT 1 FROM agenda WHERE id = p_id_agenda) THEN
        SET p_mensaje = 'ERROR: Cita no existe.';
        LEAVE main_block;
    END IF;

    -- Obtener datos de la cita
    SELECT id_mascota, id_cliente, fecha, hora, total_cita, abono_inicial, id_estado
    INTO v_id_mascota, v_id_cliente, v_fecha_atencion, v_hora_inicio, v_total_cita, v_abono_total, v_estado_cita
    FROM agenda WHERE id = p_id_agenda;

    -- Validar que la cita no está cancelada o sin asistencia
    IF v_estado_cita IN (4, 6) THEN
        SET p_mensaje = 'ERROR: No se puede registrar atención en una cita cancelada o sin asistencia.';
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

    -- Obtener estado ABIERTA
    SELECT id INTO v_estado_abierta
    FROM estado_historia_clinica
    WHERE nombre = 'ABIERTA'
    LIMIT 1;

    IF v_estado_abierta IS NULL THEN
        SET p_mensaje = 'ERROR: Estado ABIERTA no existe en el catálogo.';
        LEAVE main_block;
    END IF;

    -- Obtener o crear historia clínica para la mascota
    SELECT id INTO v_id_historia
    FROM historia_clinica
    WHERE id_mascota = v_id_mascota;

    IF v_id_historia IS NULL THEN
        INSERT INTO historia_clinica (
            codigo,
            id_mascota,
            fecha_apertura,
            observaciones_generales,
            activo
        ) VALUES (
            'TEMP',
            v_id_mascota,
            CURDATE(),
            'Historia automáticamente creada al registrar primera atención.',
            1
        );

        SET v_id_historia = LAST_INSERT_ID();
        SET v_codigo_his = CONCAT('HIS-', LPAD(v_id_historia, 6, '0'));

        UPDATE historia_clinica
        SET codigo = v_codigo_his
        WHERE id = v_id_historia;
    END IF;

    -- Insertar registro de atención con código temporal
    INSERT INTO historia_clinica_registros (
        codigo,
        id_historia_clinica,
        id_agenda,
        id_veterinario,
        id_colaborador,
        fecha_atencion,
        hora_inicio,
        tipo_visita,
        total_cita,
        abono_total,
        saldo_pendiente,
        motivo_consulta,
        anamnesis,
        examen_fisico,
        signos_vitales,
        peso_kg,
        temperatura_c,
        diagnostico,
        tratamiento,
        proximo_control,
        estado_pelaje,
        condicion_piel,
        observaciones_grooming,
        comportamiento_hospedaje,
        alimentacion_hospedaje,
        actividad_hospedaje,
        observaciones,
        id_estado
    ) VALUES (
        'TEMP',
        v_id_historia,
        p_id_agenda,
        p_id_veterinario,
        p_id_colaborador,
        v_fecha_atencion,
        v_hora_inicio,
        COALESCE(p_tipo_visita, 'GENERAL'),
        v_total_cita,
        v_abono_total,
        (v_total_cita - v_abono_total),
        p_motivo_consulta,
        p_anamnesis,
        p_examen_fisico,
        p_signos_vitales,
        COALESCE(p_peso_kg, 0),
        p_temperatura_c,
        p_diagnostico,
        p_tratamiento,
        p_proximo_control,
        p_estado_pelaje,
        p_condicion_piel,
        p_observaciones_grooming,
        p_comportamiento_hospedaje,
        p_alimentacion_hospedaje,
        p_actividad_hospedaje,
        p_observaciones,
        v_estado_abierta
    );

    SET p_id_registro = LAST_INSERT_ID();
    SET p_codigo_registro = CONCAT('REG-', LPAD(p_id_registro, 6, '0'));

    UPDATE historia_clinica_registros
    SET codigo = p_codigo_registro
    WHERE id = p_id_registro;

    -- Actualizar estado de la cita a ATENDIDA (id = 5)
    UPDATE agenda SET id_estado = 5 WHERE id = p_id_agenda;

    SET p_mensaje = CONCAT(
        'Cita atendida y registrada: ',
        p_codigo_registro,
        ' | Total: S/', FORMAT(v_total_cita, 2),
        ' | Abonado: S/', FORMAT(v_abono_total, 2),
        ' | Saldo: S/', FORMAT((v_total_cita - v_abono_total), 2)
    );

END$$
DELIMITER ;
-- ========================================
-- REGISTRAR ATENCIÓN  (cada consulta)
-- ========================================
DROP PROCEDURE IF EXISTS registrar_atencion;
DELIMITER $$
CREATE PROCEDURE registrar_atencion(
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
    DECLARE v_estado_abierta INT;

    -- Validar existencia de historia clínica
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia_clinica) THEN
        SET p_mensaje = 'ERROR: Historia clínica no existe.';
        LEAVE main_block;
    END IF;

    -- Validar que la historia esté activa
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id = p_id_historia_clinica AND activo = 1) THEN
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

    -- Validar fecha obligatoria
    IF p_fecha_atencion IS NULL THEN
        SET p_mensaje = 'ERROR: Fecha de atención es obligatoria.';
        LEAVE main_block;
    END IF;

    -- Validar coherencia de horas
    IF p_hora_fin IS NOT NULL AND p_hora_fin <= p_hora_inicio THEN
        SET p_mensaje = 'ERROR: Hora fin debe ser posterior a hora inicio.';
        LEAVE main_block;
    END IF;

    -- Obtener estado ABIERTA
    SELECT id INTO v_estado_abierta
    FROM estado_historia_clinica
    WHERE nombre = 'ABIERTA'
    LIMIT 1;

    IF v_estado_abierta IS NULL THEN
        SET p_mensaje = 'ERROR: Estado ABIERTA no existe en el catálogo.';
        LEAVE main_block;
    END IF;

    -- Insertar registro con código temporal
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
        'TEMP',
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
    SET p_codigo = CONCAT('REG-', LPAD(p_id_registro, 6, '0'));

    UPDATE historia_clinica_registros
    SET codigo = p_codigo
    WHERE id = p_id_registro;

    SET p_mensaje = CONCAT('Registro de atención ', p_codigo, ' creado exitosamente.');

END$$
DELIMITER ;
-- ========================================
-- ACTUALIZAR ATENCIÓN
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_atencion;
DELIMITER $$
CREATE PROCEDURE actualizar_atencion(
    IN p_id_registro BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_id_colaborador BIGINT,
    IN p_hora_fin TIME,
    
    -- DATOS CLÍNICOS
    IN p_motivo_consulta VARCHAR(256),
    IN p_anamnesis TEXT,
    IN p_examen_fisico TEXT,
    IN p_signos_vitales VARCHAR(256),
    IN p_peso_kg DECIMAL(6,2),
    IN p_temperatura_c DECIMAL(4,2),
    IN p_diagnostico TEXT,
    IN p_tratamiento TEXT,
    IN p_proximo_control DATE,
    
    -- DATOS ESTÉTICA
    IN p_estado_pelaje VARCHAR(128),
    IN p_condicion_piel VARCHAR(128),
    IN p_observaciones_grooming TEXT,
    
    -- DATOS HOSPEDAJE
    IN p_comportamiento_hospedaje TEXT,
    IN p_alimentacion_hospedaje VARCHAR(256),
    IN p_actividad_hospedaje TEXT,
    
    -- NOTAS GENERALES
    IN p_observaciones TEXT,
    IN p_id_estado INT,
    
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_estado_actual INT;
    DECLARE v_id_estado_cerrada INT;
    DECLARE v_id_estado_anulada INT;
    
    -- Validar que el registro existe
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
        LEAVE main_block;
    END IF;
    
    -- Obtener estado actual y código
    SELECT id_estado, codigo INTO v_estado_actual, p_codigo
    FROM historia_clinica_registros
    WHERE id = p_id_registro;
    
    -- Obtener IDs de estados no editables
    SELECT id INTO v_id_estado_cerrada
    FROM estado_historia_clinica
    WHERE nombre = 'CERRADA';
    
    SELECT id INTO v_id_estado_anulada
    FROM estado_historia_clinica
    WHERE nombre = 'ANULADA';
    
    -- No permitir editar si está cerrado o anulado
    IF v_estado_actual IN (v_id_estado_cerrada, v_id_estado_anulada) THEN
        SET p_mensaje = CONCAT(
            'ERROR: Registro ',
            p_codigo,
            ' no puede modificarse (estado cerrado/anulado).'
        );
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
    
    -- Actualizar registro (COALESCE mantiene valores anteriores si no se pasan nuevos)
    UPDATE historia_clinica_registros SET
        id_veterinario = COALESCE(p_id_veterinario, id_veterinario),
        id_colaborador = COALESCE(p_id_colaborador, id_colaborador),
        hora_fin = COALESCE(p_hora_fin, hora_fin),
        
        -- Datos clínicos
        motivo_consulta = COALESCE(p_motivo_consulta, motivo_consulta),
        anamnesis = COALESCE(p_anamnesis, anamnesis),
        examen_fisico = COALESCE(p_examen_fisico, examen_fisico),
        signos_vitales = COALESCE(p_signos_vitales, signos_vitales),
        peso_kg = COALESCE(p_peso_kg, peso_kg),
        temperatura_c = COALESCE(p_temperatura_c, temperatura_c),
        diagnostico = COALESCE(p_diagnostico, diagnostico),
        tratamiento = COALESCE(p_tratamiento, tratamiento),
        proximo_control = COALESCE(p_proximo_control, proximo_control),
        
        -- Datos estética
        estado_pelaje = COALESCE(p_estado_pelaje, estado_pelaje),
        condicion_piel = COALESCE(p_condicion_piel, condicion_piel),
        observaciones_grooming = COALESCE(p_observaciones_grooming, observaciones_grooming),
        
        -- Datos hospedaje
        comportamiento_hospedaje = COALESCE(p_comportamiento_hospedaje, comportamiento_hospedaje),
        alimentacion_hospedaje = COALESCE(p_alimentacion_hospedaje, alimentacion_hospedaje),
        actividad_hospedaje = COALESCE(p_actividad_hospedaje, actividad_hospedaje),
        
        -- Notas generales
        observaciones = COALESCE(p_observaciones, observaciones),
        id_estado = COALESCE(p_id_estado, id_estado)
    WHERE id = p_id_registro;
    
    SET p_mensaje = CONCAT('Registro ', p_codigo, ' actualizado exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- ELIMINAR ATENCION
-- Responsabilidad: Eliminar un registro
-- ========================================
DROP PROCEDURE IF EXISTS eliminar_atencion;
DELIMITER $$
CREATE PROCEDURE eliminar_atencion(
    IN p_id_registro BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_estado_actual INT;
    DECLARE v_id_estado_temporal INT;
    DECLARE v_id_estado_abierta INT;
    
    -- Validar que el registro existe
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
        LEAVE main_block;
    END IF;
    
    -- Obtener estado actual y código
    SELECT id_estado, codigo INTO v_estado_actual, p_codigo
    FROM historia_clinica_registros
    WHERE id = p_id_registro;
    
    -- Obtener IDs de estados permitidos
    SELECT id INTO v_id_estado_temporal
    FROM estado_historia_clinica
    WHERE nombre = 'TEMPORAL';
    
    SELECT id INTO v_id_estado_abierta
    FROM estado_historia_clinica
    WHERE nombre = 'ABIERTA';
    
    -- Solo permitir eliminar si está en TEMPORAL o ABIERTA
    IF v_estado_actual NOT IN (v_id_estado_temporal, v_id_estado_abierta) THEN
        SET p_mensaje = CONCAT(
            'ERROR: Registro ',
            p_codigo,
            ' no puede eliminarse. Solo se pueden eliminar registros en estado TEMPORAL o ABIERTA.'
        );
        LEAVE main_block;
    END IF;
    
    -- Eliminar el registro
    DELETE FROM historia_clinica_registros WHERE id = p_id_registro;
    
    SET p_mensaje = CONCAT('Registro ', p_codigo, ' eliminado exitosamente.');
    
END$$
DELIMITER ;

-- ========================================
-- SUBIR ARCHIVO CLÍNICO
-- ========================================
DROP PROCEDURE IF EXISTS subir_archivo_clinico;
DELIMITER $$

CREATE PROCEDURE subir_archivo_clinico(
    IN p_id_registro_atencion BIGINT,
    IN p_id_tipo_archivo INT,
    IN p_nombre_archivo VARCHAR(128),
    IN p_extension_archivo VARCHAR(32),
    IN p_descripcion VARCHAR(256),
    OUT p_id_archivo BIGINT,
    OUT p_codigo VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_sqlstate CHAR(5);
    DECLARE v_sqlmsg TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            v_sqlstate = RETURNED_SQLSTATE,
            v_sqlmsg = MESSAGE_TEXT;
        ROLLBACK;
        SET p_mensaje = CONCAT('ERROR SQL: [', v_sqlstate, '] ', v_sqlmsg);
    END;

    START TRANSACTION;

    -- Validar existencia del registro de atención
    IF NOT EXISTS (SELECT 1 FROM historia_clinica_registros WHERE id = p_id_registro_atencion) THEN
        SET p_mensaje = 'ERROR: Registro de atención no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar tipo de archivo si se proporciona
    IF p_id_tipo_archivo IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM tipos_archivo_clinico WHERE id = p_id_tipo_archivo
    ) THEN
        SET p_mensaje = 'ERROR: Tipo de archivo no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Validar campos obligatorios
    IF p_nombre_archivo IS NULL OR TRIM(p_nombre_archivo) = '' THEN
        SET p_mensaje = 'ERROR: Nombre de archivo es obligatorio.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF p_extension_archivo IS NULL OR TRIM(p_extension_archivo) = '' THEN
        SET p_mensaje = 'ERROR: Extensión de archivo es obligatoria.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- INSERT con código temporal
    INSERT INTO historia_clinica_archivos (
        codigo,
        id_registro_atencion,
        id_tipo_archivo,
        nombre_archivo,
        extension_archivo,
        descripcion
    ) VALUES (
        'TEMP',
        p_id_registro_atencion,
        p_id_tipo_archivo,
        p_nombre_archivo,
        p_extension_archivo,
        p_descripcion
    );

    SET p_id_archivo = LAST_INSERT_ID();
    SET p_codigo = CONCAT('ARC', LPAD(p_id_archivo, 6, '0'));

    UPDATE historia_clinica_archivos
    SET codigo = p_codigo
    WHERE id = p_id_archivo;

    COMMIT;

    SET p_mensaje = CONCAT('Archivo "', p_nombre_archivo, '" subido correctamente. Código: ', p_codigo);

END$$
DELIMITER ;

-- ========================================
-- ELIMINAR ARCHIVO CLÍNICO
-- ========================================
DROP PROCEDURE IF EXISTS eliminar_archivo_clinico;
DELIMITER $$

CREATE PROCEDURE eliminar_archivo_clinico(
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
-- ===========================================================
-- SP: sp_consultar_historial_mascota
-- RS1 — todo lo de la mascota y su historia, una sola fila
-- RS2 — todos los registros de atención ordenados del más reciente al más antiguo
-- RS3 — todos los archivos de todos los registros, ordenados por fecha de subida 
-- ===========================================================
DROP PROCEDURE IF EXISTS sp_consultar_historial_mascota;
DELIMITER $$

CREATE PROCEDURE sp_consultar_historial_mascota(
    IN p_id_mascota BIGINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN

    -- Validar existencia de la mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existe.';
        LEAVE proc_main;
    END IF;

    -- Validar que tenga historia clínica
    IF NOT EXISTS (SELECT 1 FROM historia_clinica WHERE id_mascota = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: La mascota no tiene historia clínica registrada.';
        LEAVE proc_main;
    END IF;

    SET p_mensaje = 'OK';

    -- ========================================
    -- RESULT SET 1: Datos de la mascota + historia clínica
    -- ========================================
    SELECT
        m.id                        AS id_mascota,
        m.codigo                    AS codigo_mascota,
        m.nombre                    AS nombre_mascota,
        m.sexo                      AS sexo,
        m.fecha_nacimiento          AS fecha_nacimiento,
        m.pelaje                    AS pelaje,
        m.peso                      AS peso,
        m.esterilizado              AS esterilizado,
        m.alergias                  AS alergias,
        m.chip                      AS chip,
        m.pedigree                  AS pedigree,
        m.factor_dea                AS factor_dea,
        m.agresividad               AS agresividad,
        m.foto                      AS foto,
        esp.nombre                  AS especie,
        r.nombre                    AS raza,
        t.descripcion               AS tamano,
        ev.descripcion              AS etapa_vida,
        em.nombre                   AS estado_mascota,
        -- Datos del cliente (dueño)
        c.id                        AS id_cliente,
        c.codigo                    AS codigo_cliente,
        ent.nombre                  AS nombre_cliente,
        ent.telefono                AS telefono_cliente,
        ent.correo                  AS correo_cliente,
        -- Datos de la historia clínica
        hc.id                       AS id_historia_clinica,
        hc.codigo                   AS codigo_historia,
        hc.fecha_apertura           AS fecha_apertura,
        hc.observaciones_generales  AS observaciones_generales,
        hc.activo                   AS historia_activa,
        hc.fecha_registro           AS fecha_registro_historia
    FROM mascotas m
    JOIN especies esp       ON esp.id = m.id_especie
    LEFT JOIN razas r       ON r.id = m.id_raza
    JOIN tamanos t          ON t.id = m.id_tamano
    JOIN etapas_vida ev     ON ev.id = m.id_etapa
    JOIN estado_mascota em  ON em.id = m.id_estado
    JOIN clientes c         ON c.id = m.id_cliente
    JOIN entidades ent      ON ent.id = c.id_entidad
    JOIN historia_clinica hc ON hc.id_mascota = m.id
    WHERE m.id = p_id_mascota;

    -- ========================================
    -- RESULT SET 2: Registros de atención
    -- ========================================
    SELECT
        hcr.id                          AS id_registro,
        hcr.codigo                      AS codigo_registro,
        hcr.fecha_atencion              AS fecha_atencion,
        hcr.hora_inicio                 AS hora_inicio,
        hcr.hora_fin                    AS hora_fin,
        hcr.tipo_visita                 AS tipo_visita,
        hcr.total_cita                  AS total_cita,
        hcr.abono_total                 AS abono_total,
        hcr.saldo_pendiente             AS saldo_pendiente,
        -- Datos clínicos
        hcr.motivo_consulta             AS motivo_consulta,
        hcr.anamnesis                   AS anamnesis,
        hcr.examen_fisico               AS examen_fisico,
        hcr.signos_vitales              AS signos_vitales,
        hcr.peso_kg                     AS peso_kg,
        hcr.temperatura_c               AS temperatura_c,
        hcr.diagnostico                 AS diagnostico,
        hcr.tratamiento                 AS tratamiento,
        hcr.proximo_control             AS proximo_control,
        -- Datos estética
        hcr.estado_pelaje               AS estado_pelaje,
        hcr.condicion_piel              AS condicion_piel,
        hcr.observaciones_grooming      AS observaciones_grooming,
        -- Datos hospedaje
        hcr.comportamiento_hospedaje    AS comportamiento_hospedaje,
        hcr.alimentacion_hospedaje      AS alimentacion_hospedaje,
        hcr.actividad_hospedaje         AS actividad_hospedaje,
        -- Notas y estado
        hcr.observaciones               AS observaciones,
        ehc.nombre                      AS estado_registro,
        hcr.fecha_registro              AS fecha_registro,
        -- Responsable
        CASE
            WHEN hcr.id_veterinario IS NOT NULL
                THEN ent_vet.nombre
            WHEN hcr.id_colaborador IS NOT NULL
                THEN ent_col.nombre
            ELSE NULL
        END                             AS responsable,
        CASE
            WHEN hcr.id_veterinario IS NOT NULL THEN 'VETERINARIO'
            WHEN hcr.id_colaborador IS NOT NULL THEN 'COLABORADOR'
            ELSE NULL
        END                             AS tipo_responsable
    FROM historia_clinica_registros hcr
    JOIN historia_clinica hc            ON hc.id = hcr.id_historia_clinica
    JOIN estado_historia_clinica ehc    ON ehc.id = hcr.id_estado
    LEFT JOIN veterinarios v            ON v.id = hcr.id_veterinario
    LEFT JOIN colaboradores col_vet     ON col_vet.id = v.id_colaborador
    LEFT JOIN entidades ent_vet         ON ent_vet.id = col_vet.id_entidad
    LEFT JOIN colaboradores col         ON col.id = hcr.id_colaborador
    LEFT JOIN entidades ent_col         ON ent_col.id = col.id_entidad
    WHERE hc.id_mascota = p_id_mascota
    ORDER BY hcr.fecha_atencion DESC, hcr.hora_inicio DESC;

    -- ========================================
    -- RESULT SET 3: Archivos por registro
    -- ========================================
    SELECT
        hca.id                      AS id_archivo,
        hca.codigo                  AS codigo_archivo,
        hca.id_registro_atencion    AS id_registro_atencion,
        hca.nombre_archivo          AS nombre_archivo,
        hca.extension_archivo       AS extension_archivo,
        hca.descripcion             AS descripcion,
        hca.fecha_subida            AS fecha_subida,
        tac.nombre                  AS tipo_archivo
    FROM historia_clinica_archivos hca
    JOIN historia_clinica_registros hcr ON hcr.id = hca.id_registro_atencion
    JOIN historia_clinica hc            ON hc.id = hcr.id_historia_clinica
    LEFT JOIN tipos_archivo_clinico tac ON tac.id = hca.id_tipo_archivo
    WHERE hc.id_mascota = p_id_mascota
    ORDER BY hca.fecha_subida DESC;

END$$
DELIMITER ;