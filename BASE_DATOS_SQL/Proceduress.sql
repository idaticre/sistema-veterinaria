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
-- PROCEDIMIENTO: gestionar_asistencia
-- Controla el registro de asistencia diaria (entrada, almuerzo, salida)
-- ========================================
DROP PROCEDURE IF EXISTS gestionar_asistencia;
DELIMITER $$

CREATE PROCEDURE gestionar_asistencia (
    IN p_colaborador_id BIGINT,
    IN p_tipo_marca VARCHAR(10),
    OUT p_mensaje VARCHAR(255)
)
proc_asistencia: BEGIN
    DECLARE v_id_asistencia BIGINT;
    DECLARE v_fecha_actual DATE;
    DECLARE v_hora_actual TIME;
    DECLARE v_id_horario INT;
    DECLARE v_hora_inicio TIME;
    DECLARE v_id_estado_presente INT;
    DECLARE v_id_estado_completado INT;

    -- Inicializa valores actuales
    SET v_fecha_actual = CURDATE();
    SET v_hora_actual = CURTIME();

    -- Obtiene IDs de estados
    SELECT id INTO v_id_estado_presente FROM estado_asistencia WHERE nombre = 'PRESENTE' LIMIT 1;
    SELECT id INTO v_id_estado_completado FROM estado_asistencia WHERE nombre = 'COMPLETADO' LIMIT 1;

    -- Valida tipo_marca
    IF p_tipo_marca NOT IN ('ENTRADA', 'LUNCH_IN', 'LUNCH_OUT', 'SALIDA') THEN
        SET p_mensaje = 'Tipo de marca inválido.';
        LEAVE proc_asistencia;
    END IF;

    -- Obtiene el horario asignado según el día actual
    SELECT ah.id_horario_base, hb.hora_inicio
    INTO v_id_horario, v_hora_inicio
    FROM asignacion_horarios ah
    JOIN horarios_base hb ON hb.id = ah.id_horario_base
    WHERE ah.id_colaborador = p_colaborador_id
      AND ah.id_dia_semana = CASE DAYOFWEEK(CURDATE())
                                WHEN 1 THEN 7
                                ELSE DAYOFWEEK(CURDATE()) - 1
                             END
      AND ah.activo = 1
    LIMIT 1;

    -- Verifica si tiene asignación hoy
    IF v_id_horario IS NULL THEN
        SET p_mensaje = 'El colaborador no tiene horario asignado para hoy.';
        LEAVE proc_asistencia;
    END IF;

    -- Busca si ya tiene un registro hoy
    SELECT id INTO v_id_asistencia
    FROM registro_asistencias
    WHERE id_colaborador = p_colaborador_id
      AND fecha = v_fecha_actual
    LIMIT 1;

    -- =====================================
    -- CASO 1: No hay registro previo → se crea solo si es ENTRADA
    -- =====================================
    IF v_id_asistencia IS NULL THEN
        IF p_tipo_marca = 'ENTRADA' THEN
            INSERT INTO registro_asistencias (
                id_colaborador, id_horario_base, fecha, hora_entrada, id_estado_asistencia
            )
            VALUES (
                p_colaborador_id, v_id_horario, v_fecha_actual, v_hora_actual, v_id_estado_presente
            );

            -- Calcula tardanza si hora_inicio es no nula
            IF v_hora_inicio IS NOT NULL THEN
                UPDATE registro_asistencias
                SET tardanza_minutos = GREATEST(TIMESTAMPDIFF(MINUTE, v_hora_inicio, v_hora_actual), 0)
                WHERE id = LAST_INSERT_ID();
            END IF;

            SET p_mensaje = CONCAT('Entrada registrada a las ', v_hora_actual);

        ELSE
            SET p_mensaje = 'Debe marcar ENTRADA antes de cualquier otro evento.';
        END IF;

    -- =====================================
    -- CASO 2: Ya tiene registro → se actualiza según tipo de marca
    -- =====================================
    ELSE
        CASE p_tipo_marca

            WHEN 'ENTRADA' THEN
                SET p_mensaje = 'Ya existe una entrada registrada hoy.';

            WHEN 'LUNCH_IN' THEN
                UPDATE registro_asistencias
                SET hora_lunch_inicio = IF(hora_lunch_inicio IS NULL, v_hora_actual, hora_lunch_inicio)
                WHERE id = v_id_asistencia
                  AND hora_entrada IS NOT NULL
                  AND hora_lunch_inicio IS NULL;

                IF ROW_COUNT() > 0 THEN
                    SET p_mensaje = CONCAT('Inicio de almuerzo registrado a las ', v_hora_actual);
                ELSE
                    SET p_mensaje = 'Ya registró inicio de almuerzo o no marcó entrada.';
                END IF;

            WHEN 'LUNCH_OUT' THEN
                UPDATE registro_asistencias
                SET hora_lunch_fin = IF(hora_lunch_fin IS NULL, v_hora_actual, hora_lunch_fin),
                    minutos_lunch = CASE
                        WHEN hora_lunch_inicio IS NOT NULL THEN TIMESTAMPDIFF(MINUTE, hora_lunch_inicio, v_hora_actual)
                        ELSE NULL
                    END
                WHERE id = v_id_asistencia
                  AND hora_lunch_inicio IS NOT NULL
                  AND hora_lunch_fin IS NULL;

                IF ROW_COUNT() > 0 THEN
                    SET p_mensaje = CONCAT('Fin de almuerzo registrado a las ', v_hora_actual);
                ELSE
                    SET p_mensaje = 'Debe iniciar almuerzo antes de finalizarlo.';
                END IF;

            WHEN 'SALIDA' THEN
                UPDATE registro_asistencias
                SET hora_salida = IF(hora_salida IS NULL, v_hora_actual, hora_salida),
                    minutos_trabajados = CASE
                        WHEN hora_entrada IS NOT NULL THEN TIMESTAMPDIFF(MINUTE, hora_entrada, v_hora_actual) - IFNULL(minutos_lunch, 0)
                        ELSE NULL
                    END,
                    id_estado_asistencia = v_id_estado_completado
                WHERE id = v_id_asistencia
                  AND hora_entrada IS NOT NULL
                  AND hora_salida IS NULL;

                IF ROW_COUNT() > 0 THEN
                    SET p_mensaje = CONCAT('Salida registrada a las ', v_hora_actual);
                ELSE
                    SET p_mensaje = 'Ya registró salida o falta marcar entrada.';
                END IF;

        END CASE;
    END IF;

END $$

DELIMITER ;


-- ========================================
-- PROCEDIMIENTO: ver_asistencia_por_rango
-- Devuelve las asistencias registradas entre dos fechas.
-- Incluye colaborador, horario asignado y estado.
-- ========================================
DROP PROCEDURE IF EXISTS ver_asistencia_por_rango;
DELIMITER $$

CREATE PROCEDURE ver_asistencia_por_rango (
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_id_estado INT 
)
BEGIN
    -- Valida que el rango sea correcto
    IF p_fecha_inicio > p_fecha_fin THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La fecha inicial no puede ser mayor que la final.';
    END IF;

    SELECT 
        c.id AS id_colaborador,
        e.nombre AS colaborador,
        hb.nombre AS horario,
        ra.fecha,
        ra.hora_entrada,
        ra.hora_lunch_inicio,
        ra.hora_lunch_fin,
        ra.hora_salida,
        ra.minutos_trabajados,
        ra.minutos_lunch,
        ra.tardanza_minutos,
        ea.nombre AS estado_asistencia,
        ra.observaciones
    FROM registro_asistencias ra
    JOIN colaboradores c ON c.id = ra.id_colaborador
    JOIN entidades e ON e.id = c.id_entidad
    LEFT JOIN horarios_base hb ON hb.id = ra.id_horario_base
    LEFT JOIN estado_asistencia ea ON ea.id = ra.id_estado_asistencia
    WHERE ra.fecha BETWEEN p_fecha_inicio AND p_fecha_fin
		AND (p_id_estado IS NULL OR ra.id_estado_asistencia = p_id_estado)
    ORDER BY ra.fecha DESC, colaborador ASC;
END $$

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

-- USE vet_manada_woof;

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

