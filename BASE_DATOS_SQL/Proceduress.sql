-- ================================================================
-- SCRIPT: PROCEDIMIENTOS ALMACENADOS CRUD - SISTEMA VETERINARIA_WOOF
-- ================================================================
USE vet_manada_woof;

-- BLOQUE 01 PROCEDIMIENTOS ALMACENADOS CRUD

-- ========================================
-- SP: gestionar_rol_usuario
-- Asigna o elimina roles de usuarios.
-- Un usuario puede tener varios roles y un rol puede estar en varios usuarios.
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

    SELECT username INTO v_nombre_usuario FROM usuarios WHERE id = p_id_usuario;
    SELECT nombre INTO v_nombre_rol FROM roles WHERE id = p_id_rol;

    IF p_accion = 'ASIGNAR' THEN
        IF EXISTS (SELECT 1 FROM usuarios_roles WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol) THEN
            SET p_mensaje = CONCAT('ERROR: El usuario ', v_nombre_usuario, ' ya tiene asignado el rol ', v_nombre_rol, '.');
            LEAVE main_block;
        END IF;

        -- Verificar si ya tiene otros roles asignados
        SELECT COUNT(*) INTO v_tiene_roles_previos
        FROM usuarios_roles
        WHERE id_usuario = p_id_usuario;

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
                
                -- INSERTAR TODOS LOS HORARIOS EN UNA SOLA OPERACION (SIN CURSOR)
                INSERT INTO asignacion_horarios (
                    id_colaborador,
                    id_horario_base,
                    id_dia_semana,
                    fecha_inicio_vigencia,
                    fecha_fin_vigencia,
                    motivo_cambio,
                    activo
                )
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

        DELETE FROM usuarios_roles WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol;
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
    IN p_id_entidad INT, -- Si es 0 o NULL, se crea entidad + colaborador
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
    DECLARE v_id_tipo_entidad BIGINT DEFAULT NULL;-- ESTO YA NO EXISTE LA TABLA SE ELIMINO Y TODA RELACION
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_id_colaborador BIGINT DEFAULT NULL;
    DECLARE v_id_veterinario BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_codigo_colaborador_local VARCHAR(20);
    DECLARE v_codigo_veterinario_local VARCHAR(20);

    -- Obtener id tipo entidad 'COLABORADOR'
    SELECT id INTO v_id_tipo_entidad FROM tipo_entidad WHERE nombre = 'COLABORADOR' LIMIT 1;
    IF v_id_tipo_entidad IS NULL THEN
        SET p_mensaje = 'ERROR: Tipo entidad COLABORADOR no encontrado.';
        LEAVE registro;
    END IF;

    -- Caso 1: Si p_id_entidad es NULL o 0, crear entidad + colaborador
    IF p_id_entidad IS NULL OR p_id_entidad = 0 THEN
        -- Crear entidad base (tipo colaborador)
        CALL registrar_entidad_base(
            v_id_tipo_entidad,
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
            @p_id_entidad,
            @p_codigo_entidad,
            @p_mensaje
        );

        SELECT @p_id_entidad INTO v_id_entidad;
        SELECT @p_codigo_entidad INTO v_codigo_entidad_local;
        SELECT @p_mensaje INTO p_mensaje;

        IF v_id_entidad IS NULL THEN
            -- Error, mensaje ya asignado
            LEAVE registro;
        END IF;

        -- Insertar en colaboradores sin código
        INSERT INTO colaboradores (id_entidad, codigo, activo, id_usuario, foto)
        VALUES (v_id_entidad, NULL, 1, 1, NULL); -- Ajustar id_usuario y foto según contexto

        SET v_id_colaborador = LAST_INSERT_ID();

        -- Actualizar código colaborador con prefijo 'COL'
        SET v_codigo_colaborador_local = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'));
        UPDATE colaboradores SET codigo = v_codigo_colaborador_local WHERE id = v_id_colaborador;

        -- Asignar salida
        SET p_codigo_entidad = v_codigo_entidad_local;
        SET p_codigo_colaborador = v_codigo_colaborador_local;

    ELSE
        -- Caso 2: entidad existente, buscar colaborador relacionado
        SET v_id_entidad = p_id_entidad;

        SELECT id INTO v_id_colaborador
        FROM colaboradores
        WHERE id_entidad = v_id_entidad;

        IF v_id_colaborador IS NULL THEN
            -- No hay colaborador, crear uno para la entidad existente
            INSERT INTO colaboradores (id_entidad, codigo, activo, id_usuario, foto)
            VALUES (v_id_entidad, NULL, 1, 1, NULL); -- Ajustar id_usuario y foto según contexto

            SET v_id_colaborador = LAST_INSERT_ID();

            -- Actualizar código colaborador con prefijo 'COL'
            SET v_codigo_colaborador_local = CONCAT('COL', LPAD(v_id_colaborador, 6, '0'));
            UPDATE colaboradores SET codigo = v_codigo_colaborador_local WHERE id = v_id_colaborador;

            -- Obtener código entidad
            SELECT codigo INTO v_codigo_entidad_local FROM entidades WHERE id = v_id_entidad;

            SET p_codigo_entidad = v_codigo_entidad_local;
            SET p_codigo_colaborador = v_codigo_colaborador_local;
        ELSE
            -- Colaborador ya existe, obtener códigos
            SELECT codigo INTO v_codigo_colaborador_local FROM colaboradores WHERE id = v_id_colaborador;
            SELECT codigo INTO v_codigo_entidad_local FROM entidades WHERE id = v_id_entidad;

            SET p_codigo_entidad = v_codigo_entidad_local;
            SET p_codigo_colaborador = v_codigo_colaborador_local;
        END IF;
    END IF;

    -- Validar que no exista veterinario para ese colaborador
    IF EXISTS (
        SELECT 1 FROM veterinarios WHERE id_colaborador = v_id_colaborador
    ) THEN
        SET p_mensaje = CONCAT('ERROR: El colaborador ya está registrado como veterinario. Código colaborador: ', v_codigo_colaborador_local);
        LEAVE registro;
    END IF;

    -- Insertar veterinario sin código
    INSERT INTO veterinarios (id_colaborador, id_especialidad, cmp, activo)
    VALUES (v_id_colaborador, p_id_especialidad, p_cmp, 1);

    SET v_id_veterinario = LAST_INSERT_ID();

    -- Actualizar código veterinario con prefijo 'VET'
    SET v_codigo_veterinario_local = CONCAT('VET', LPAD(v_id_veterinario, 6, '0'));
    UPDATE veterinarios SET codigo = v_codigo_veterinario_local WHERE id = v_id_veterinario;

    SET p_codigo_veterinario = v_codigo_veterinario_local;

    SET p_mensaje = CONCAT('Veterinario registrado correctamente. Código veterinario: ', v_codigo_veterinario_local);
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
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_codigo_colaborador_local VARCHAR(20);
    DECLARE v_codigo_veterinario_local VARCHAR(20);

    -- Validar existencia de la entidad
    IF NOT EXISTS (SELECT 1 FROM entidades WHERE id = p_id_entidad) THEN
        SET p_mensaje = 'ERROR: La entidad especificada no existe.';
        LEAVE actualizar;
    END IF;

    -- Validar duplicidad de documento en otra entidad
    IF EXISTS (SELECT 1 FROM entidades WHERE documento = p_documento AND id <> p_id_entidad) THEN
        SET p_mensaje = 'ERROR: Ya existe otra entidad con ese número de documento.';
        LEAVE actualizar;
    END IF;

    -- Obtener ID y código del colaborador relacionado a la entidad
    SELECT id, codigo INTO v_id_colaborador, v_codigo_colaborador_local
    FROM colaboradores
    WHERE id_entidad = p_id_entidad;

    IF v_id_colaborador IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un colaborador asociado a esta entidad.';
        LEAVE actualizar;
    END IF;

    -- Verificar que exista registro en veterinarios para este colaborador
    IF NOT EXISTS (SELECT 1 FROM veterinarios WHERE id_colaborador = v_id_colaborador) THEN
        SET p_mensaje = 'ERROR: Este colaborador no está registrado como veterinario.';
        LEAVE actualizar;
    END IF;

    -- Obtener códigos de entidad y veterinario
    SELECT codigo INTO v_codigo_entidad_local FROM entidades WHERE id = p_id_entidad;
    SELECT codigo INTO v_codigo_veterinario_local FROM veterinarios WHERE id_colaborador = v_id_colaborador;

    -- Actualizar datos generales de la entidad
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

    -- Actualizar datos del colaborador
    UPDATE colaboradores
    SET
        id_usuario = p_id_usuario,
        foto = p_foto,
        activo = p_activo
    WHERE id = v_id_colaborador;

    -- Actualizar datos específicos del veterinario
    UPDATE veterinarios
    SET
        id_especialidad = p_id_especialidad,
        cmp = p_cmp,
        activo = p_activo
    WHERE id_colaborador = v_id_colaborador;

    -- Asignar códigos de salida
    SET p_codigo_entidad = v_codigo_entidad_local;
    SET p_codigo_colaborador = v_codigo_colaborador_local;
    SET p_codigo_veterinario = v_codigo_veterinario_local;

    SET p_mensaje = CONCAT(
        'Veterinario actualizado correctamente. Código veterinario: ', v_codigo_veterinario_local
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
    DECLARE v_nuevo_id BIGINT DEFAULT 0;
    DECLARE v_sqlstate CHAR(5);
    DECLARE v_sqlmsg TEXT;

    -- Manejo de errores con detalle real
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            v_sqlstate = RETURNED_SQLSTATE,
            v_sqlmsg = MESSAGE_TEXT;
        ROLLBACK;
        SET p_mensaje = CONCAT('ERROR SQL: [', v_sqlstate, '] ', v_sqlmsg);
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

    -- 4️⃣ Validar duplicado
    IF EXISTS (
        SELECT 1 FROM medicamentos_mascota
        WHERE id_mascota = p_id_mascota
          AND id_medicamento = p_id_medicamento
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro para este medicamento en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 5️⃣ Calcular próximo ID de forma segura
    SELECT IFNULL(MAX(id), 0) + 1 INTO v_nuevo_id FROM medicamentos_mascota;

    -- 6️⃣ Insertar nuevo registro
    INSERT INTO medicamentos_mascota (
        codigo, id_mascota, id_medicamento, id_via, dosis,
        fecha_aplicacion, id_colaborador, id_veterinario, observaciones
    )
    VALUES (
        CONCAT('MEDM', LPAD(v_nuevo_id, 6, '0')),
        p_id_mascota,
        p_id_medicamento,
        p_id_via,
        p_dosis,
        p_fecha_aplicacion,
        p_id_colaborador,
        p_id_veterinario,
        p_observaciones
    );

    -- 7️⃣ Obtener el código insertado
    SELECT codigo INTO v_codigo_registro 
    FROM medicamentos_mascota 
    WHERE id = LAST_INSERT_ID();

    -- 8️⃣ Obtener nombre del medicamento
    SELECT nombre INTO v_nombre_medicamento FROM medicamentos WHERE id = p_id_medicamento;

    COMMIT;

    -- 9️⃣ Mensaje final
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
    DECLARE v_codigo_registro VARCHAR(16);
    DECLARE v_nombre_vacuna VARCHAR(64);
    DECLARE v_nuevo_id BIGINT DEFAULT 0;
    DECLARE v_sqlstate CHAR(5);
    DECLARE v_sqlmsg TEXT;

    -- Manejo de errores con detalle
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            v_sqlstate = RETURNED_SQLSTATE,
            v_sqlmsg = MESSAGE_TEXT;
        ROLLBACK;
        SET p_mensaje = CONCAT('ERROR SQL: [', v_sqlstate, '] ', v_sqlmsg);
    END;

    START TRANSACTION;

    -- 1️⃣ Validar existencia de mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 2️⃣ Validar existencia de vacuna
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 3️⃣ Validar existencia de vía
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 4️⃣ Validar duplicado (misma vacuna y fecha)
    IF EXISTS (
        SELECT 1 FROM vacunas_mascota
        WHERE id_mascota = p_id_mascota
          AND id_vacuna = p_id_vacuna
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro de esta vacuna en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 5️⃣ Calcular nuevo ID para generar código
    SELECT IFNULL(MAX(id), 0) + 1 INTO v_nuevo_id FROM vacunas_mascota;

    -- 6️⃣ Insertar nuevo registro
    INSERT INTO vacunas_mascota (
        codigo, id_mascota, id_vacuna, id_via, dosis,
        fecha_aplicacion, durabilidad_anios, proxima_dosis,
        id_colaborador, id_veterinario, observaciones, activo
    )
    VALUES (
        CONCAT('VACM', LPAD(v_nuevo_id, 6, '0')),
        p_id_mascota,
        p_id_vacuna,
        p_id_via,
        p_dosis,
        p_fecha_aplicacion,
        p_durabilidad_anios,
        DATE_ADD(p_fecha_aplicacion, INTERVAL p_durabilidad_anios YEAR),
        p_id_colaborador,
        p_id_veterinario,
        p_observaciones,
        1
    );

    -- 7️⃣ Obtener código insertado
    SELECT codigo INTO v_codigo_registro 
    FROM vacunas_mascota 
    WHERE id = LAST_INSERT_ID();

    -- 8️⃣ Obtener nombre de la vacuna
    SELECT nombre INTO v_nombre_vacuna FROM vacunas WHERE id = p_id_vacuna;

    COMMIT;

    -- 9️⃣ Mensaje final
    SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna,
                            '" registrada correctamente. Código del registro: ', v_codigo_registro, '.');
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

    IF NOT EXISTS (SELECT 1 FROM vacunas_mascota WHERE id = p_id_vacuna_mascota) THEN
        SET p_mensaje = 'ERROR: Registro de vacuna no existe.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    SELECT codigo INTO v_codigo FROM vacunas_mascota WHERE id = p_id_vacuna_mascota;
    SELECT v.nombre, m.nombre
    INTO v_nombre_vacuna, v_nombre_mascota
    FROM vacunas_mascota vm
    JOIN vacunas v ON vm.id_vacuna = v.id
    JOIN mascotas m ON vm.id_mascota = m.id
    WHERE vm.id = p_id_vacuna_mascota;

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

    IF p_activo = 0 THEN
        UPDATE vacunas_mascota SET activo = 0, fecha_modificacion = NOW() WHERE id = p_id_vacuna_mascota;
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" para la mascota "', v_nombre_mascota, '" desactivada.');
    ELSE
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" actualizada correctamente. Código: ', v_codigo);
    END IF;

    COMMIT;
END$$
DELIMITER ;

-- ===========================================================================================================================================
-- ===========================================================================================================================================
-- ===========================================================================================================================================
-- BLOQUE 04 PROCEDIMIENTOS ALMACENADOS CRUD

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

