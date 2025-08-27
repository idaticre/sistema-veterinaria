
USE vet_manada_woof;

-- BLOQUE 01 PROCEDIMIENTOS ALMACENADOS CRUD
-- Correspondiente a la parte de administración y funcionalidad
-- En la parte final podran visualizar todos los procedures con show

-- ========================================
-- CRUD: TABLA 'empresa'
-- Procedimiento para lectura y actualización de datos
-- ========================================
DROP PROCEDURE IF EXISTS sp_empresa;
DELIMITER $$
CREATE PROCEDURE sp_empresa(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_razon_social VARCHAR(128),
    IN p_ruc CHAR(11),
    IN p_direccion VARCHAR(256),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_correo VARCHAR(64),
    IN p_representante VARCHAR(64),
    IN p_logo_empresa VARCHAR(255)
)
BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM empresa;
        ELSE
            SELECT * FROM empresa WHERE id = p_id;
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        UPDATE empresa
        SET razon_social = p_razon_social,
            ruc = p_ruc,
            direccion = p_direccion,
            ciudad = p_ciudad,
            distrito = p_distrito,
            telefono = p_telefono,
            correo = p_correo,
            representante = p_representante,
            logo_empresa = p_logo_empresa
        WHERE id = p_id;

        -- Mostrar el código del registro actualizado
        SELECT CONCAT('Registro empresa con ID ', p_id, ' actualizado correctamente.') AS mensaje;

    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Acción no válida. Use READ o UPDATE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_documento'
-- Manejo de tipos de documentos con mensajes que incluyen código
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_documento;
DELIMITER $$
CREATE PROCEDURE sp_tipo_documento(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_descripcion VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));
    
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_documento WHERE descripcion = p_descripcion) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de documento con esa descripción.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_documento (descripcion, activo)
        VALUES (p_descripcion, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE tipo_documento
        SET codigo = CONCAT('TDOC', LPAD(@last_id, 2, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Tipo de documento creado correctamente con código ', (SELECT codigo FROM tipo_documento WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_documento;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe tipo de documento con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM tipo_documento WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de documento no existe.';
            LEAVE main_block;
        END IF;
        UPDATE tipo_documento
        SET descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de documento con código ', (SELECT codigo FROM tipo_documento WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de documento no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM tipo_documento WHERE id = p_id);
        DELETE FROM tipo_documento WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de documento con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_persona_juridica'
-- Manejo con mensajes que incluyen código para validar
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_persona_juridica;
DELIMITER $$
CREATE PROCEDURE sp_tipo_persona_juridica(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));
    
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_persona_juridica (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE tipo_persona_juridica
        SET codigo = CONCAT('TEMP', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Tipo persona jurídica creado correctamente con código ', (SELECT codigo FROM tipo_persona_juridica WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_persona_juridica;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe tipo persona jurídica con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM tipo_persona_juridica WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de persona jurídica no existe.';
            LEAVE main_block;
        END IF;
        UPDATE tipo_persona_juridica
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo persona jurídica con código ', (SELECT codigo FROM tipo_persona_juridica WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de persona jurídica no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM tipo_persona_juridica WHERE id = p_id);
        DELETE FROM tipo_persona_juridica WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo persona jurídica con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'usuarios'
-- Manejo con mensajes y eliminación lógica (activo=0)
-- ========================================
DROP PROCEDURE IF EXISTS sp_usuarios;
DELIMITER $$
CREATE PROCEDURE sp_usuarios(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_usuario VARCHAR(32),
    IN p_clave VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM usuarios WHERE username = p_usuario) THEN
            SET p_mensaje = 'ERROR: Ya existe un usuario con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO usuarios (username, password_hash, activo)
        VALUES (p_usuario, p_clave, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE usuarios
        SET codigo = CONCAT('USU', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Usuario creado correctamente con código ', (SELECT codigo FROM usuarios WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM usuarios WHERE activo = 1;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id AND activo = 1) THEN
                SET p_mensaje = 'ERROR: El usuario no existe o está inactivo.';
                LEAVE main_block;
            END IF;
            SELECT * FROM usuarios WHERE id = p_id AND activo = 1;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id AND activo = 1) THEN
            SET p_mensaje = 'ERROR: El usuario no existe o está inactivo.';
            LEAVE main_block;
        END IF;
        UPDATE usuarios
        SET username = p_usuario,
            password_hash = p_clave
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Usuario con código ', (SELECT codigo FROM usuarios WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id AND activo = 1) THEN
            SET p_mensaje = 'ERROR: El usuario no existe o ya fue desactivado.';
            LEAVE main_block;
        END IF;
        UPDATE usuarios SET activo = 0 WHERE id = p_id;
        SET p_mensaje = CONCAT('Usuario con código ', (SELECT codigo FROM usuarios WHERE id = p_id), ' desactivado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'roles'
-- Manejo con mensajes y eliminación física
-- ========================================
DROP PROCEDURE IF EXISTS sp_roles;
DELIMITER $$
CREATE PROCEDURE sp_roles(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));
    
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM roles WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un rol con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO roles (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE roles
        SET codigo = CONCAT('ROL', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Rol creado correctamente con código ', (SELECT codigo FROM roles WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM roles;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id) THEN
                SET p_mensaje = 'ERROR: El rol no existe.';
                LEAVE main_block;
            END IF;
            SELECT * FROM roles WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El rol no existe.';
            LEAVE main_block;
        END IF;
        UPDATE roles
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Rol con código ', (SELECT codigo FROM roles WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El rol no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM roles WHERE id = p_id);
        DELETE FROM roles WHERE id = p_id;
        SET p_mensaje = CONCAT('Rol con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'usuarios_roles'
-- Manejo de asignación y eliminación de roles a usuarios
-- ========================================
DROP PROCEDURE IF EXISTS sp_usuarios_roles;
DELIMITER $$
CREATE PROCEDURE sp_usuarios_roles(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_usuario_id INT,
    IN p_rol_id INT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM usuarios_roles WHERE id_usuario = p_usuario_id AND id_rol = p_rol_id) THEN
            SET p_mensaje = 'ERROR: El rol ya está asignado al usuario.';
            LEAVE main_block;
        END IF;
        INSERT INTO usuarios_roles (id_usuario, id_rol)
        VALUES (p_usuario_id, p_rol_id);
        SET p_mensaje = 'Rol asignado al usuario correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_usuario_id IS NULL THEN
            SELECT * FROM usuarios_roles;
        ELSE
            SELECT * FROM usuarios_roles WHERE id_usuario = p_usuario_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        DELETE FROM usuarios_roles WHERE id_usuario = p_usuario_id AND id_rol = p_rol_id;
        SET p_mensaje = 'Relación usuario-rol eliminada correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_entidad'
-- Manejo con mensajes y eliminación física
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_entidad;
DELIMITER $$
CREATE PROCEDURE sp_tipo_entidad(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_entidad WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de entidad con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_entidad (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE tipo_entidad
        SET codigo = CONCAT('TPEN', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Tipo de entidad creado correctamente con código ', (SELECT codigo FROM tipo_entidad WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_entidad;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tipo_entidad WHERE id = p_id) THEN
                SET p_mensaje = 'ERROR: El tipo de entidad no existe.';
                LEAVE main_block;
            END IF;
            SELECT * FROM tipo_entidad WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_entidad WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de entidad no existe.';
            LEAVE main_block;
        END IF;
        UPDATE tipo_entidad
        SET nombre = p_nombre,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de entidad con código ', (SELECT codigo FROM tipo_entidad WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_entidad WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de entidad no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM tipo_entidad WHERE id = p_id);
        DELETE FROM tipo_entidad WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de entidad con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
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
    IN p_id_tipo_entidad INT,
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
    OUT p_id_entidad INT,
    OUT p_codigo_entidad VARCHAR(16),
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_entidad = NULL;
        SET p_codigo_entidad = NULL;
        SET p_mensaje = 'ERROR: Falló el registro de entidad. Transacción revertida.';
    END;

    START TRANSACTION;

    -- Validación básica
    IF p_nombre IS NULL OR p_documento IS NULL THEN
        SET p_mensaje = 'ERROR: Nombre y documento son obligatorios.';
        ROLLBACK;
        LEAVE proc_main;
    END IF;

    -- Validación documento duplicado
    IF EXISTS (SELECT 1 FROM entidades WHERE documento = p_documento) THEN
        SET p_mensaje = 'ERROR: Documento ya registrado.';
        ROLLBACK;
        LEAVE proc_main;
    END IF;

    -- Insertar en entidades
    INSERT INTO entidades (
        id_tipo_entidad, id_tipo_persona_juridica, nombre, sexo, documento,
        id_tipo_documento, telefono, correo, direccion, ciudad, distrito,
        representante
    ) VALUES (
        p_id_tipo_entidad, p_id_tipo_persona_juridica, p_nombre, p_sexo, p_documento,
        p_id_tipo_documento, p_telefono, p_correo, p_direccion, p_ciudad, p_distrito,
        p_representante
    );

    SET p_id_entidad = LAST_INSERT_ID();

    -- Generar código con prefijo 'ENT' y 6 dígitos rellenados con ceros
    SET p_codigo_entidad = CONCAT('ENT', LPAD(p_id_entidad, 6, '0'));

    -- Actualizar código en la entidad
    UPDATE entidades
    SET codigo = p_codigo_entidad
    WHERE id = p_id_entidad;

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
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    -- Validar existencia de entidad
    IF NOT EXISTS (SELECT 1 FROM entidades WHERE id = p_id_entidad) THEN
        SET p_mensaje = 'ERROR: La entidad especificada no existe.';
        LEAVE proc_main;
    END IF;

    -- Validar documento duplicado en otra entidad
    IF EXISTS (SELECT 1 FROM entidades WHERE documento = p_documento AND id <> p_id_entidad) THEN
        SET p_mensaje = 'ERROR: Ya existe otra entidad con ese número de documento.';
        LEAVE proc_main;
    END IF;

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

    -- Mensaje de confirmación
    SET p_mensaje = CONCAT('Entidad actualizada: ', (SELECT codigo FROM entidades WHERE id = p_id_entidad));
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
    DECLARE v_id_tipo_entidad INT DEFAULT NULL;
    DECLARE v_id_entidad INT DEFAULT NULL;
    DECLARE v_id_cliente INT DEFAULT NULL;

    -- Obtener id de tipo entidad CLIENTE
    SELECT id INTO v_id_tipo_entidad 
    FROM tipo_entidad 
    WHERE nombre = 'CLIENTE' 
    LIMIT 1;

    IF v_id_tipo_entidad IS NULL THEN
        SET p_mensaje = 'ERROR: Tipo entidad CLIENTE no encontrado.';
        LEAVE proc_main;
    END IF;

    -- Llamar a registrar_entidad_base para crear la entidad en tabla 'entidades'
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
        NULL, -- representante para cliente es NULL
        @p_id_entidad,
        @p_codigo_entidad,
        @p_mensaje
    );

    -- Recuperar resultados del procedimiento base
    SELECT @p_id_entidad INTO v_id_entidad;
    SELECT @p_codigo_entidad INTO p_codigo_entidad;
    SELECT @p_mensaje INTO p_mensaje;

    -- Validar si hubo error en registrar_entidad_base
    IF v_id_entidad IS NULL THEN
        LEAVE proc_main;
    END IF;

    -- Insertar en clientes con id_entidad obtenido
    INSERT INTO clientes (id_entidad, codigo) VALUES (v_id_entidad, NULL);

    SET v_id_cliente = LAST_INSERT_ID();

    -- Actualizar código en clientes con prefijo 'CLI' y 6 dígitos rellenos con ceros
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

    -- Actualizar datos generales de la entidad
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
        NULL, -- representante NULL para cliente
        p_activo,
        v_mensaje_entidad
    );

    -- Validar respuesta de la actualización base
    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc_main;
    END IF;

    -- Obtener cliente y código asociado a la entidad
    SELECT id, codigo 
    INTO v_id_cliente, v_codigo_cliente 
    FROM clientes 
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_cliente IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un cliente asociado a esa entidad.';
        LEAVE proc_main;
    END IF;

    -- Actualizar estado activo (eliminación lógica)
    UPDATE clientes 
    SET activo = p_activo 
    WHERE id = v_id_cliente;

    -- Mensaje final
    SET p_mensaje = CONCAT('Cliente actualizado correctamente. Código Cliente: ', v_codigo_cliente);
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
    DECLARE v_id_tipo_entidad INT DEFAULT NULL;
    DECLARE v_id_entidad INT DEFAULT NULL;
    DECLARE v_id_proveedor INT DEFAULT NULL;

    -- Obtener id de tipo entidad PROVEEDOR
    SELECT id 
    INTO v_id_tipo_entidad 
    FROM tipo_entidad 
    WHERE nombre = 'PROVEEDOR' 
    LIMIT 1;

    IF v_id_tipo_entidad IS NULL THEN
        SET p_mensaje = 'ERROR: Tipo entidad PROVEEDOR no encontrado.';
        LEAVE proc_main;
    END IF;

    -- Llamar a registrar_entidad_base para crear la entidad
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
    SELECT @p_codigo_entidad INTO p_codigo_entidad;
    SELECT @p_mensaje INTO p_mensaje;

    IF v_id_entidad IS NULL THEN
        LEAVE proc_main;
    END IF;

    -- Insertar en proveedores con id_entidad obtenido
    INSERT INTO proveedores (id_entidad, codigo) 
    VALUES (v_id_entidad, NULL);

    SET v_id_proveedor = LAST_INSERT_ID();

    -- Actualizar código en proveedores con prefijo 'PRV' y 6 dígitos
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
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc_main: BEGIN
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_proveedor INT;
    DECLARE v_codigo_proveedor VARCHAR(20);

    -- Actualizar datos generales de la entidad
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

    -- Si hubo error en entidad, salir
    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc_main;
    END IF;

    -- Verificar si existe proveedor asociado a la entidad
    SELECT id, codigo 
    INTO v_id_proveedor, v_codigo_proveedor 
    FROM proveedores 
    WHERE id_entidad = p_id_entidad
    LIMIT 1;

    IF v_id_proveedor IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un proveedor asociado a esa entidad.';
        LEAVE proc_main;
    END IF;

    -- Actualizar estado activo (eliminación lógica)
    UPDATE proveedores 
    SET activo = p_activo 
    WHERE id = v_id_proveedor;

    -- Mensaje de éxito
    SET p_mensaje = CONCAT('Proveedor actualizado correctamente. Código Proveedor: ', v_codigo_proveedor);
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
    DECLARE v_id_tipo_entidad INT DEFAULT NULL;
    DECLARE v_id_entidad INT DEFAULT NULL;
    DECLARE v_id_colaborador INT DEFAULT NULL;

    -- Obtener id de tipo entidad COLABORADOR
    SELECT id 
    INTO v_id_tipo_entidad 
    FROM tipo_entidad 
    WHERE nombre = 'COLABORADOR' 
    LIMIT 1;

    IF v_id_tipo_entidad IS NULL THEN
        SET p_mensaje = 'ERROR: Tipo entidad COLABORADOR no encontrado.';
        LEAVE proc_main;
    END IF;

    -- Llamar a registrar_entidad_base para crear la entidad
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
        NULL, -- representante para colaborador es NULL
        @p_id_entidad,
        @p_codigo_entidad,
        @p_mensaje
    );

    -- Recuperar valores de salida
    SELECT @p_id_entidad INTO v_id_entidad;
    SELECT @p_codigo_entidad INTO p_codigo_entidad;
    SELECT @p_mensaje INTO p_mensaje;

    IF v_id_entidad IS NULL THEN
        -- Aquí ya tienes un mensaje de error en p_mensaje que viene de registrar_entidad_base
        LEAVE proc_main;
    END IF;

    -- Insertar en colaboradores
    INSERT INTO colaboradores (id_entidad, fecha_ingreso, id_usuario, foto, activo, codigo) 
    VALUES (v_id_entidad, p_fecha_ingreso, p_id_usuario, p_foto, 1, NULL);

    SET v_id_colaborador = LAST_INSERT_ID();

    -- Generar código COL + 6 dígitos
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
    IN p_fecha_ingreso DATE,
    IN p_id_usuario INT,
    IN p_foto VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
proc: BEGIN
    DECLARE v_mensaje_entidad VARCHAR(255);
    DECLARE v_id_colaborador INT;
    DECLARE v_codigo_colaborador VARCHAR(20);

    -- Actualizar datos generales de la entidad
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
        NULL, -- representante NULL para colaborador
        p_activo,
        v_mensaje_entidad
    );

    IF v_mensaje_entidad LIKE 'ERROR:%' THEN
        SET p_mensaje = v_mensaje_entidad;
        LEAVE proc;
    END IF;

    -- Obtener colaborador y código
    SELECT id, codigo INTO v_id_colaborador, v_codigo_colaborador
    FROM colaboradores
    WHERE id_entidad = p_id_entidad;

    IF v_id_colaborador IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un colaborador asociado a esa entidad.';
        LEAVE proc;
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
    DECLARE v_id_tipo_entidad INT DEFAULT NULL;
    DECLARE v_id_entidad INT DEFAULT NULL;
    DECLARE v_id_colaborador INT DEFAULT NULL;
    DECLARE v_id_veterinario INT DEFAULT NULL;
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
    OUT p_mensaje VARCHAR(255)
)
actualizar: BEGIN
    DECLARE v_id_colaborador INT;
    DECLARE v_codigo_entidad VARCHAR(20);
    DECLARE v_codigo_colaborador VARCHAR(20);
    DECLARE v_codigo_veterinario VARCHAR(20);

    -- Validar existencia de la entidad
    IF NOT EXISTS (
        SELECT 1 FROM entidades WHERE id = p_id_entidad
    ) THEN
        SET p_mensaje = 'ERROR: La entidad especificada no existe.';
        LEAVE actualizar;
    END IF;

    -- Validar duplicidad de documento en otra entidad
    IF EXISTS (
        SELECT 1 FROM entidades WHERE documento = p_documento AND id <> p_id_entidad
    ) THEN
        SET p_mensaje = 'ERROR: Ya existe otra entidad con ese número de documento.';
        LEAVE actualizar;
    END IF;

    -- Obtener ID y código del colaborador relacionado a la entidad
    SELECT c.id, c.codigo INTO v_id_colaborador, v_codigo_colaborador
    FROM colaboradores c
    WHERE c.id_entidad = p_id_entidad;

    IF v_id_colaborador IS NULL THEN
        SET p_mensaje = 'ERROR: No existe un colaborador asociado a esta entidad.';
        LEAVE actualizar;
    END IF;

    -- Verificar que exista registro en veterinarios para este colaborador
    IF NOT EXISTS (
        SELECT 1 FROM veterinarios WHERE id_colaborador = v_id_colaborador
    ) THEN
        SET p_mensaje = 'ERROR: Este colaborador no está registrado como veterinario.';
        LEAVE actualizar;
    END IF;

    -- Obtener código de la entidad
    SELECT codigo INTO v_codigo_entidad FROM entidades WHERE id = p_id_entidad;

    -- Obtener código del veterinario
    SELECT codigo INTO v_codigo_veterinario FROM veterinarios WHERE id_colaborador = v_id_colaborador;

    -- Actualizar datos generales en entidades
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

    SET p_mensaje = CONCAT(
        'Veterinario actualizado correctamente. ','Código veterinario: ', v_codigo_veterinario
    );
END$$

DELIMITER ;

-- ========================================
-- CRUD: TABLA 'especialidades'
-- Manejo con mensajes y eliminación física
-- ========================================
DROP PROCEDURE IF EXISTS sp_especialidades;
DELIMITER $$
CREATE PROCEDURE sp_especialidades(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM especialidades WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una especialidad con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO especialidades (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE especialidades
        SET codigo = CONCAT('ESPV', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Especialidad creada correctamente con código ', (SELECT codigo FROM especialidades WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM especialidades;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM especialidades WHERE id = p_id) THEN
                SET p_mensaje = 'ERROR: La especialidad no existe.';
                LEAVE main_block;
            END IF;
            SELECT * FROM especialidades WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM especialidades WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especialidad no existe.';
            LEAVE main_block;
        END IF;
        UPDATE especialidades
        SET nombre = p_nombre,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Especialidad con código ', (SELECT codigo FROM especialidades WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM especialidades WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especialidad no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM especialidades WHERE id = p_id);
        DELETE FROM especialidades WHERE id = p_id;
        SET p_mensaje = CONCAT('Especialidad con código ', @codigo, ' eliminada correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'dias_semana'
-- ========================================
DROP PROCEDURE IF EXISTS sp_dias_semana;
DELIMITER $$
CREATE PROCEDURE sp_dias_semana(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));
    
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM dias_semana WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un día con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO dias_semana (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE dias_semana
        SET codigo = CONCAT('DSE', LPAD(@last_id, 6, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Día creado correctamente con código ', (SELECT codigo FROM dias_semana WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM dias_semana;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id) THEN
                SET p_mensaje = 'ERROR: El día no existe.';
                LEAVE main_block;
            END IF;
            SELECT * FROM dias_semana WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El día no existe.';
            LEAVE main_block;
        END IF;
        UPDATE dias_semana
        SET nombre = p_nombre,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Día con código ', (SELECT codigo FROM dias_semana WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El día no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM dias_semana WHERE id = p_id);
        DELETE FROM dias_semana WHERE id = p_id;
        SET p_mensaje = CONCAT('Día con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipos_dia'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipos_dia;
DELIMITER $$
CREATE PROCEDURE sp_tipos_dia(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipos_dia WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de día con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipos_dia (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET @last_id = LAST_INSERT_ID();
        UPDATE tipos_dia
        SET codigo = CONCAT('TDI', LPAD(@last_id, 6, '0'))
        WHERE id = @last_id;
        SET p_mensaje = CONCAT('Tipo de día creado correctamente con código ', (SELECT codigo FROM tipos_dia WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipos_dia;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tipos_dia WHERE id = p_id) THEN
                SET p_mensaje = 'ERROR: El tipo de día no existe.';
                LEAVE main_block;
            END IF;
            SELECT * FROM tipos_dia WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_dia WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de día no existe.';
            LEAVE main_block;
        END IF;
        UPDATE tipos_dia
        SET nombre = p_nombre,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de día con código ', (SELECT codigo FROM tipos_dia WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_dia WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de día no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM tipos_dia WHERE id = p_id);
        DELETE FROM tipos_dia WHERE id = p_id;
        SET p_mensaje = CONCAT('Tipo de día con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;