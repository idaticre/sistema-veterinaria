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
    IN p_accion VARCHAR(10),       -- 'ASIGNAR' o 'ELIMINAR'
    IN p_id_usuario INT,
    IN p_id_rol INT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    DECLARE v_nombre_usuario VARCHAR(64);
    DECLARE v_nombre_rol VARCHAR(64);

    -- Normalizar acción
    SET p_accion = UPPER(TRIM(p_accion));

    -- Validar existencia de usuario
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id_usuario) THEN
        SET p_mensaje = 'ERROR: Usuario no existe.';
        LEAVE main_block;
    END IF;

    -- Validar existencia de rol
    IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id_rol) THEN
        SET p_mensaje = 'ERROR: Rol no existe.';
        LEAVE main_block;
    END IF;

    -- Obtener nombres
    SELECT username INTO v_nombre_usuario
    FROM usuarios
    WHERE id = p_id_usuario;

    SELECT nombre INTO v_nombre_rol
    FROM roles
    WHERE id = p_id_rol;

    -- Acción: ASIGNAR
    IF p_accion = 'ASIGNAR' THEN
        IF EXISTS (
            SELECT 1 FROM usuarios_roles 
            WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol
        ) THEN
            SET p_mensaje = CONCAT('ERROR: El usuario ', v_nombre_usuario, ' ya tiene asignado el rol ', v_nombre_rol, '.');
            LEAVE main_block;
        END IF;

        INSERT INTO usuarios_roles (id_usuario, id_rol)
        VALUES (p_id_usuario, p_id_rol);

        SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' asignado correctamente al usuario ', v_nombre_usuario, '.');

    -- Acción: ELIMINAR
    ELSEIF p_accion = 'ELIMINAR' THEN
        IF NOT EXISTS (
            SELECT 1 FROM usuarios_roles 
            WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol
        ) THEN
            SET p_mensaje = CONCAT('ERROR: El usuario ', v_nombre_usuario, ' no tiene asignado el rol ', v_nombre_rol, '.');
            LEAVE main_block;
        END IF;

        DELETE FROM usuarios_roles 
        WHERE id_usuario = p_id_usuario AND id_rol = p_id_rol;

        SET p_mensaje = CONCAT('Rol ', v_nombre_rol, ' eliminado correctamente del usuario ', v_nombre_usuario, '.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use "ASIGNAR" o "ELIMINAR".';
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
    OUT p_codigo_entidad VARCHAR(20),
    OUT p_codigo_cliente VARCHAR(20),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_id_entidad BIGINT DEFAULT NULL;
    DECLARE v_codigo_entidad_local VARCHAR(20);
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_cliente BIGINT DEFAULT NULL;

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
        NULL,                    -- representante NULL para cliente
        v_id_entidad,
        v_codigo_entidad_local,
        v_mensaje_entidad
    );

    -- Retornar resultados de la entidad
    SET p_codigo_entidad = v_codigo_entidad_local;
    SET p_mensaje = v_mensaje_entidad;

    -- Si no se creó la entidad, salir
    IF v_id_entidad IS NULL THEN
        LEAVE proc_main;
    END IF;

    -- Si ya existe rol Cliente para esa entidad, devolver código existente
    IF EXISTS (SELECT 1 FROM clientes WHERE id_entidad = v_id_entidad) THEN
        SELECT codigo INTO p_codigo_cliente FROM clientes WHERE id_entidad = v_id_entidad LIMIT 1;
        SET p_mensaje = CONCAT('La entidad ya está registrada como Cliente. Código Cliente: ', p_codigo_cliente);
        LEAVE proc_main;
    END IF;

    -- Insertar en clientes (codigo se genera luego)
    INSERT INTO clientes (id_entidad, codigo) VALUES (v_id_entidad, NULL);
    SET v_id_cliente = LAST_INSERT_ID();

    UPDATE clientes
    SET codigo = CONCAT('CLI', LPAD(v_id_cliente, 6, '0'))
    WHERE id = v_id_cliente;

    SET p_codigo_cliente = CONCAT('CLI', LPAD(v_id_cliente, 6, '0'));
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
        NULL,
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
-- PROCEDURE: asignar_horario_dia
-- Asigna un horario base específico a un colaborador en un día determinado.
-- Si ya existe una asignación para ese día, la actualiza.
-- ========================================
DROP PROCEDURE IF EXISTS asignar_horario_dia;
DELIMITER $$

CREATE PROCEDURE asignar_horario_dia (
    IN p_id_colaborador BIGINT,
    IN p_id_horario_base INT,
    IN p_id_dia_semana INT
)
BEGIN
    -- Manejador de errores SQL genérico
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error al asignar horario diario.';
    END;

    START TRANSACTION;

    -- Validación: colaborador existente
    IF NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Colaborador no existe.';
    END IF;

    -- Validación: horario activo
    IF NOT EXISTS (SELECT 1 FROM horarios_base WHERE id = p_id_horario_base AND activo = 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Horario base no válido o inactivo.';
    END IF;

    -- Validación: día válido
    IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id_dia_semana) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Día de semana no válido.';
    END IF;

    -- Inserta o actualiza la asignación existente según la clave única (colaborador + día)
    INSERT INTO asignacion_horarios (
        id_colaborador, id_horario_base, id_dia_semana, fecha_asignacion, activo
    )
    VALUES (
        p_id_colaborador, p_id_horario_base, p_id_dia_semana, NOW(), 1
    )
    ON DUPLICATE KEY UPDATE
        id_horario_base = VALUES(id_horario_base),
        fecha_asignacion = NOW(),
        activo = 1;

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- PROCEDURE: asignar_horario_semana
-- Asigna un mismo horario base a todos los días laborales (lunes a sábado)
-- de un colaborador. Si ya existen asignaciones, se actualizan.
-- ========================================
DROP PROCEDURE IF EXISTS asignar_horario_semana;
DELIMITER $$

CREATE PROCEDURE asignar_horario_semana (
    IN p_id_colaborador BIGINT,
    IN p_id_horario_base INT
)
BEGIN
    DECLARE v_dia INT DEFAULT 1; -- Contador de días (lunes a sábado)

    -- Manejador de errores SQL genérico
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error al asignar horario semanal.';
    END;

    START TRANSACTION;

    -- Validación: colaborador existente
    IF NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Colaborador no existe.';
    END IF;

    -- Validación: horario activo
    IF NOT EXISTS (SELECT 1 FROM horarios_base WHERE id = p_id_horario_base AND activo = 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Horario base no válido o inactivo.';
    END IF;

    -- (Opcional pero recomendable) Limpia asignaciones previas del colaborador
    -- para evitar residuos de horarios antiguos o diferentes configuraciones
    DELETE FROM asignacion_horarios
    WHERE id_colaborador = p_id_colaborador;

    -- Bucle de lunes (1) a sábado (6)
    WHILE v_dia <= 6 DO
        INSERT INTO asignacion_horarios (
            id_colaborador, id_horario_base, id_dia_semana, fecha_asignacion, activo
        )
        VALUES (
            p_id_colaborador, p_id_horario_base, v_dia, NOW(), 1
        )
        ON DUPLICATE KEY UPDATE
            id_horario_base = VALUES(id_horario_base),
            fecha_asignacion = NOW(),
            activo = 1;
        SET v_dia = v_dia + 1;
    END WHILE;

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- PROCEDURE: desasignar_horario_dia
-- Desactiva (sin eliminar) la asignación de horario
-- para un colaborador en un día específico.
-- ========================================
DROP PROCEDURE IF EXISTS desasignar_horario_dia;
DELIMITER $$

CREATE PROCEDURE desasignar_horario_dia (
    IN p_id_colaborador BIGINT,
    IN p_id_dia_semana INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error al desasignar horario diario.';
    END;

    START TRANSACTION;

    -- Validación: colaborador existente
    IF NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Colaborador no existe.';
    END IF;

    -- Validación: día válido
    IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id_dia_semana) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Día de semana no válido.';
    END IF;

    -- Desactivar la asignación existente
    UPDATE asignacion_horarios
    SET activo = 0, fecha_asignacion = NOW()
    WHERE id_colaborador = p_id_colaborador
      AND id_dia_semana = p_id_dia_semana;

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- PROCEDURE: desasignar_horario_semana
-- Desactiva (sin eliminar) todas las asignaciones de horario
-- para un colaborador de lunes a sábado.
-- ========================================
DROP PROCEDURE IF EXISTS desasignar_horario_semana;
DELIMITER $$

CREATE PROCEDURE desasignar_horario_semana (
    IN p_id_colaborador BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error al desasignar horario semanal.';
    END;

    START TRANSACTION;

    -- Validación: colaborador existente
    IF NOT EXISTS (SELECT 1 FROM colaboradores WHERE id = p_id_colaborador) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Colaborador no existe.';
    END IF;

    -- Desactivar todas las asignaciones (lunes a sábado)
    UPDATE asignacion_horarios
    SET activo = 0, fecha_asignacion = NOW()
    WHERE id_colaborador = p_id_colaborador
      AND id_dia_semana BETWEEN 1 AND 6;

    COMMIT;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: registrar_asistencia
-- Inserta o actualiza la asistencia de un colaborador según si ya existe registro del día.
-- ========================================
DROP PROCEDURE IF EXISTS registrar_asistencia;
DELIMITER $$

CREATE PROCEDURE registrar_asistencia (
    IN p_id_colaborador BIGINT,
    IN p_fecha DATE,
    IN p_hora_entrada TIME,
    IN p_hora_salida TIME,
    IN p_observaciones TEXT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_exist_colab INT;
    DECLARE v_exist_registro INT;

    -- Validar existencia del colaborador
    SELECT COUNT(*) INTO v_exist_colab FROM colaboradores WHERE id = p_id_colaborador;
    IF v_exist_colab = 0 THEN
        SET p_mensaje = 'ERROR: El colaborador no existe.';
        LEAVE proc_main;
    END IF;

    -- Verificar si ya hay registro en esa fecha
    SELECT COUNT(*) INTO v_exist_registro
    FROM registro_asistencia
    WHERE id_colaborador = p_id_colaborador AND fecha = p_fecha;

    -- Si ya existe, se actualiza (por ejemplo, para registrar hora de salida)
    IF v_exist_registro > 0 THEN
        UPDATE registro_asistencia
        SET hora_salida = COALESCE(p_hora_salida, hora_salida),
            observaciones = CONCAT(COALESCE(observaciones, ''), ' ', COALESCE(p_observaciones, ''))
        WHERE id_colaborador = p_id_colaborador AND fecha = p_fecha;
        SET p_mensaje = 'Asistencia actualizada correctamente.';
    ELSE
        -- Si no existe, se inserta el registro (por ejemplo, al marcar entrada)
        INSERT INTO registro_asistencia (id_colaborador, fecha, hora_entrada, hora_salida, observaciones)
        VALUES (p_id_colaborador, p_fecha, p_hora_entrada, p_hora_salida, p_observaciones);
        SET p_mensaje = 'Asistencia registrada correctamente.';
    END IF;
END$$
DELIMITER ;

-- ===========================================================================================================================================
-- ===========================================================================================================================================
-- ===========================================================================================================================================

-- BLOQUE 02 PROCEDIMIENTOS ALMACENADOS CRUD


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

    -- 1️⃣ Validar existencia de la mascota
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

    -- 5️⃣ Validar existencia del estado
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

    -- 7️⃣ Generar mensaje final adaptado al cambio de estado (actualización o eliminación lógica)
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
    IN p_id_colaborador BIGINT,          -- Colaborador que aplicó el medicamento
    IN p_id_veterinario BIGINT,          -- Veterinario responsable
    IN p_observaciones VARCHAR(64),      
    OUT p_mensaje VARCHAR(255)           
)
proc_main: BEGIN
    DECLARE v_codigo_mascota VARCHAR(16);
    DECLARE v_nombre_medicamento VARCHAR(64);

    -- Manejo de errores
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló el registro del medicamento. Transacción revertida.';
    END;

    START TRANSACTION;

    -- 1 Validar existencia de mascota
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no existente.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 2️⃣ Validar existencia de medicamento
    IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id_medicamento) THEN
        SET p_mensaje = 'ERROR: Medicamento no válido.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 3️ Validar existencia de vía de aplicación
    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 4️ Validar duplicado (misma mascota, medicamento y fecha)
    IF EXISTS (
        SELECT 1 FROM medicamentos_mascota
        WHERE id_mascota = p_id_mascota
          AND id_medicamento = p_id_medicamento
          AND fecha_aplicacion = p_fecha_aplicacion
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe un registro para este medicamento en esa fecha.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- 5️ Obtener datos de referencia para el mensaje final
    SELECT codigo INTO v_codigo_mascota FROM mascotas WHERE id = p_id_mascota;
    SELECT nombre INTO v_nombre_medicamento FROM medicamentos WHERE id = p_id_medicamento;

    -- 6️ Insertar nuevo registro
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

    COMMIT;

    SET p_mensaje = 
    CONCAT('Medicamento "', v_nombre_medicamento,'" registrado para la mascota ', v_codigo_mascota, '.');
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

    -- 4️⃣ Mensaje final
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
    IN p_id_vacuna INT,
    IN p_id_mascota BIGINT,
    IN p_id_via INT,
    IN p_dosis VARCHAR(32),
    IN p_fecha_aplicacion DATE,
    IN p_durabilidad_anios INT,
    IN p_proxima_dosis DATE,
    IN p_id_colaborador BIGINT,
    IN p_id_veterinario BIGINT,
    IN p_observaciones VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_codigo VARCHAR(16);
    DECLARE v_nombre_vacuna VARCHAR(64);
    DECLARE v_nombre_mascota VARCHAR(64);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'ERROR: Falló registro de vacuna aplicada. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validar existencia de vacuna, mascota y vía
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id_vacuna AND activo = 1) THEN
        SET p_mensaje = 'ERROR: Vacuna no válida o inactiva.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id_mascota) THEN
        SET p_mensaje = 'ERROR: Mascota no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id_via) THEN
        SET p_mensaje = 'ERROR: Vía de aplicación no válida.';
        ROLLBACK; LEAVE proc_main;
    END IF;

    -- Generar código único (VAC + ID incremental)
    SELECT CONCAT('VAC', LPAD(COALESCE(MAX(id),0) + 1, 6, '0'))
    INTO v_codigo
    FROM vacunas_mascota;

    -- Obtener nombres para mensaje final
    SELECT nombre INTO v_nombre_vacuna FROM vacunas WHERE id = p_id_vacuna;
    SELECT nombre INTO v_nombre_mascota FROM mascotas WHERE id = p_id_mascota;

    -- Insertar registro
    INSERT INTO vacunas_mascota (
        codigo, id_vacuna, id_mascota, id_via, dosis,
        fecha_aplicacion, durabilidad_anios, proxima_dosis,
        id_colaborador, id_veterinario, observaciones, fecha_registro
    ) VALUES (
        v_codigo, p_id_vacuna, p_id_mascota, p_id_via, p_dosis,
        p_fecha_aplicacion, p_durabilidad_anios, p_proxima_dosis,
        p_id_colaborador, p_id_veterinario, p_observaciones, NOW()
    );

    SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" registrada para la mascota "', v_nombre_mascota, '". Código: ', v_codigo);
    COMMIT;
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
        UPDATE vacunas_mascota SET fecha_modificacion = NOW() WHERE id = p_id_vacuna_mascota;
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" para la mascota "', v_nombre_mascota, '" desactivada (eliminación lógica).');
    ELSE
        SET p_mensaje = CONCAT('Vacuna "', v_nombre_vacuna, '" actualizada correctamente. Código: ', v_codigo);
    END IF;

    COMMIT;
END$$
DELIMITER ;