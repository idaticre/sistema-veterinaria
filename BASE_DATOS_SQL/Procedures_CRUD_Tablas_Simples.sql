-- ================================================================
-- SCRIPT: PROCEDIMIENTOS ALMACENADOS CRUD - SISTEMA VETERINARIA_WOOF
-- ================================================================
USE vet_manada_woof;

-- ========================================
-- CRUD: TABLA 'empresa'
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
    IF p_accion = 'CREATE' THEN
        INSERT INTO empresa (razon_social, ruc, direccion, ciudad, distrito, telefono, correo, representante, logo_empresa)
        VALUES (p_razon_social, p_ruc, p_direccion, p_ciudad, p_distrito, p_telefono, p_correo, p_representante, p_logo_empresa);
    ELSEIF p_accion = 'READ' THEN
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
    ELSEIF p_accion = 'DELETE' THEN
        DELETE FROM empresa WHERE id = p_id;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_documento'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_documento WHERE descripcion = p_descripcion) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de documento con esa descripción.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_documento (descripcion, activo)
        VALUES (p_descripcion, p_activo);
        SET p_mensaje = 'Tipo de documento creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_documento;
        ELSE
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
        SET p_mensaje = 'Tipo de documento actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de documento no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM tipo_documento WHERE id = p_id;
        SET p_mensaje = 'Tipo de documento eliminado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_persona_juridica'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_persona_juridica (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET p_mensaje = 'Tipo persona jurídica creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_persona_juridica;
        ELSE
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
        SET p_mensaje = 'Tipo persona jurídica actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_persona_juridica WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de persona jurídica no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM tipo_persona_juridica WHERE id = p_id;
        SET p_mensaje = 'Tipo persona jurídica eliminado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'usuarios'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM usuarios WHERE usuario = p_usuario) THEN
            SET p_mensaje = 'ERROR: Ya existe un usuario con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO usuarios (usuario, clave, activo)
        VALUES (p_usuario, p_clave, p_activo);
        SET p_mensaje = 'Usuario creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM usuarios WHERE activo = 1;
        ELSE
            SELECT * FROM usuarios WHERE id = p_id AND activo = 1;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id AND activo = 1) THEN
            SET p_mensaje = 'ERROR: El usuario no existe o está inactivo.';
            LEAVE main_block;
        END IF;
        UPDATE usuarios
        SET usuario = p_usuario,
            clave = p_clave
        WHERE id = p_id;
        SET p_mensaje = 'Usuario actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id AND activo = 1) THEN
            SET p_mensaje = 'ERROR: El usuario no existe o ya fue desactivado.';
            LEAVE main_block;
        END IF;
        UPDATE usuarios SET activo = 0 WHERE id = p_id;
        SET p_mensaje = 'Usuario desactivado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'roles'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM roles WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un rol con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO roles (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET p_mensaje = 'Rol creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM roles;
        ELSE
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
        SET p_mensaje = 'Rol actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El rol no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM roles WHERE id = p_id;
        SET p_mensaje = 'Rol eliminado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'usuarios_roles'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM usuarios_roles WHERE usuario_id = p_usuario_id AND rol_id = p_rol_id) THEN
            SET p_mensaje = 'ERROR: El rol ya está asignado al usuario.';
            LEAVE main_block;
        END IF;
        INSERT INTO usuarios_roles (usuario_id, rol_id)
        VALUES (p_usuario_id, p_rol_id);
        SET p_mensaje = 'Rol asignado al usuario correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_usuario_id IS NULL THEN
            SELECT * FROM usuarios_roles;
        ELSE
            SELECT * FROM usuarios_roles WHERE usuario_id = p_usuario_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        DELETE FROM usuarios_roles WHERE id = p_id;
        SET p_mensaje = 'Relación usuario-rol eliminada correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_entidad'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_entidad WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de entidad con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipo_entidad (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Tipo de entidad creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_entidad;
        ELSE
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
        SET p_mensaje = 'Tipo de entidad actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_entidad WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de entidad no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM tipo_entidad WHERE id = p_id;
        SET p_mensaje = 'Tipo de entidad eliminado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'especialidades'
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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM especialidades WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una especialidad con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO especialidades (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Especialidad creada correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM especialidades;
        ELSE
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
        SET p_mensaje = 'Especialidad actualizada correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM especialidades WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especialidad no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM especialidades WHERE id = p_id;
        SET p_mensaje = 'Especialidad eliminada correctamente.';

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
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM dias_semana WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un día con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO dias_semana (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Día creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM dias_semana;
        ELSE
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
        SET p_mensaje = 'Día actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM dias_semana WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El día no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM dias_semana WHERE id = p_id;
        SET p_mensaje = 'Día eliminado correctamente.';

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
    IN p_nombre VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipos_dia WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de día con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO tipos_dia (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Tipo de día creado correctamente.';

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipos_dia;
        ELSE
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
        SET p_mensaje = 'Tipo de día actualizado correctamente.';

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_dia WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de día no existe.';
            LEAVE main_block;
        END IF;
        DELETE FROM tipos_dia WHERE id = p_id;
        SET p_mensaje = 'Tipo de día eliminado correctamente.';

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_especies
-- CRUD para la tabla de especies de mascotas.
-- ========================================
DROP PROCEDURE IF EXISTS sp_especies;
DELIMITER $$
CREATE PROCEDURE sp_especies (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM especies WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una especie con ese nombre.';
        ELSE
            INSERT INTO especies (nombre) VALUES (p_nombre);
            SET p_mensaje = 'Especie registrada exitosamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especie no existe.';
        ELSE
            UPDATE especies SET nombre = p_nombre WHERE id = p_id;
            SET p_mensaje = 'Especie actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especie no existe.';
        ELSE
            DELETE FROM especies WHERE id = p_id;
            SET p_mensaje = 'Especie eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_razas
-- CRUD para razas de mascotas, vinculadas a una especie.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_razas (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_id_especie INT,
    IN p_nombre VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id_especie) THEN
            SET p_mensaje = 'ERROR: Especie no encontrada.';
        ELSE
            INSERT INTO razas (id_especie, nombre, activo)
            VALUES (p_id_especie, p_nombre, p_activo);
            SET p_mensaje = 'Raza registrada correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM razas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Raza no encontrada para actualizar.';
        ELSE
            UPDATE razas
            SET id_especie = p_id_especie,
                nombre = p_nombre,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Raza actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM razas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Raza no encontrada para eliminar.';
        ELSE
            DELETE FROM razas WHERE id = p_id;
            SET p_mensaje = 'Raza eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_tamanos
-- CRUD para la clasificación de tamaño corporal de mascotas.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_tamanos (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_talla_equivalente VARCHAR(8),
    IN p_descripcion VARCHAR(16),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO tamanos (talla_equivalente, descripcion, activo)
        VALUES (p_talla_equivalente, p_descripcion, p_activo);
        SET p_mensaje = 'Tamaño registrado correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tamanos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tamaño no encontrado para actualizar.';
        ELSE
            UPDATE tamanos
            SET talla_equivalente = p_talla_equivalente,
                descripcion = p_descripcion,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tamaño actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tamanos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tamaño no encontrado para eliminar.';
        ELSE
            DELETE FROM tamanos WHERE id = p_id;
            SET p_mensaje = 'Tamaño eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_etapas_vida
-- CRUD para las etapas de vida de mascotas.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_etapas_vida (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO etapas_vida (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET p_mensaje = 'Etapa de vida registrada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM etapas_vida WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Etapa de vida no encontrada para actualizar.';
        ELSE
            UPDATE etapas_vida
            SET nombre = p_nombre,
                descripcion = p_descripcion,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Etapa de vida actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM etapas_vida WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Etapa de vida no encontrada para eliminar.';
        ELSE
            DELETE FROM etapas_vida WHERE id = p_id;
            SET p_mensaje = 'Etapa de vida eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_vacunas
-- CRUD para el catálogo de vacunas.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_vacunas (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO vacunas (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);
        SET p_mensaje = 'Vacuna registrada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Vacuna no encontrada para actualizar.';
        ELSE
            UPDATE vacunas
            SET nombre = p_nombre,
                descripcion = p_descripcion,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Vacuna actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Vacuna no encontrada para eliminar.';
        ELSE
            DELETE FROM vacunas WHERE id = p_id;
            SET p_mensaje = 'Vacuna eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_medicamento_tipo
-- CRUD para tipos de medicamentos veterinarios.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_medicamento_tipo (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO medicamento_tipo (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Tipo de medicamento registrado correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamento_tipo WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de medicamento no encontrado para actualizar.';
        ELSE
            UPDATE medicamento_tipo
            SET nombre = p_nombre,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de medicamento actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamento_tipo WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de medicamento no encontrado para eliminar.';
        ELSE
            DELETE FROM medicamento_tipo WHERE id = p_id;
            SET p_mensaje = 'Tipo de medicamento eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_vias_aplicacion
-- CRUD para las vías de administración de medicamentos.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_vias_aplicacion (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO vias_aplicacion (nombre, activo)
        VALUES (p_nombre, p_activo);
        SET p_mensaje = 'Vía de aplicación registrada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Vía de aplicación no encontrada para actualizar.';
        ELSE
            UPDATE vias_aplicacion
            SET nombre = p_nombre,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Vía de aplicación actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Vía de aplicación no encontrada para eliminar.';
        ELSE
            DELETE FROM vias_aplicacion WHERE id = p_id;
            SET p_mensaje = 'Vía de aplicación eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_medicamentos
-- CRUD para el catálogo de medicamentos veterinarios.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_medicamentos (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_id_tipo INT,
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamento_tipo WHERE id = p_id_tipo) THEN
            SET p_mensaje = 'ERROR: Tipo de medicamento no válido.';
        ELSE
            INSERT INTO medicamentos (nombre, id_tipo, descripcion, activo)
            VALUES (p_nombre, p_id_tipo, p_descripcion, p_activo);
            SET p_mensaje = 'Medicamento registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medicamento no encontrado para actualizar.';
        ELSE
            UPDATE medicamentos
            SET nombre = p_nombre,
                id_tipo = p_id_tipo,
                descripcion = p_descripcion,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Medicamento actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medicamento no encontrado para eliminar.';
        ELSE
            DELETE FROM medicamentos WHERE id = p_id;
            SET p_mensaje = 'Medicamento eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_estado_mascota
-- CRUD para los estados clínicos de las mascotas.
-- ========================================
DELIMITER $$
CREATE PROCEDURE sp_estado_mascota (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_estado VARCHAR(32),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        INSERT INTO estado_mascota (estado, descripcion, activo)
        VALUES (p_estado, p_descripcion, p_activo);
        SET p_mensaje = 'Estado clínico registrado correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado clínico no encontrado para actualizar.';
        ELSE
            UPDATE estado_mascota
            SET estado = p_estado,
                descripcion = p_descripcion,
                activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado clínico actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado clínico no encontrado para eliminar.';
        ELSE
            DELETE FROM estado_mascota WHERE id = p_id;
            SET p_mensaje = 'Estado clínico eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_canales_comunicacion
-- Gestión CRUD de los canales de comunicación.
-- ========================================
DROP PROCEDURE IF EXISTS sp_canales_comunicacion;
DELIMITER $$
CREATE PROCEDURE sp_canales_comunicacion (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM canales_comunicacion WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un canal con ese nombre.';
        ELSE
            INSERT INTO canales_comunicacion (nombre, activo)
            VALUES (p_nombre, 1);
            SET p_mensaje = 'Canal registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM canales_comunicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Canal no encontrado.';
        ELSE
            UPDATE canales_comunicacion SET nombre = p_nombre
            WHERE id = p_id;
            SET p_mensaje = 'Canal actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM canales_comunicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Canal no encontrado.';
        ELSE
            DELETE FROM canales_comunicacion WHERE id = p_id;
            SET p_mensaje = 'Canal eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_medios_pago
-- Gestión CRUD de medios de pago aceptados en ventas.
-- ========================================
DROP PROCEDURE IF EXISTS sp_medios_pago;
DELIMITER $$
CREATE PROCEDURE sp_medios_pago (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM medios_pago WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un medio de pago con ese nombre.';
        ELSE
            INSERT INTO medios_pago (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Medio de pago registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM medios_pago WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medio de pago no encontrado.';
        ELSE
            UPDATE medios_pago
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Medio de pago actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM medios_pago WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medio de pago no encontrado.';
        ELSE
            DELETE FROM medios_pago WHERE id = p_id;
            SET p_mensaje = 'Medio de pago eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_estado_agenda
-- Gestión CRUD de los estados de una cita agendada.
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_agenda;
DELIMITER $$
CREATE PROCEDURE sp_estado_agenda (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM estado_agenda WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un estado de agenda con ese nombre.';
        ELSE
            INSERT INTO estado_agenda (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Estado de agenda registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_agenda WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado de agenda no encontrado.';
        ELSE
            UPDATE estado_agenda
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Estado de agenda actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_agenda WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado de agenda no encontrado.';
        ELSE
            DELETE FROM estado_agenda WHERE id = p_id;
            SET p_mensaje = 'Estado de agenda eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_tipo_recordatorio
-- Gestión CRUD de tipos de recordatorio en el sistema.
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_recordatorio;
DELIMITER $$
CREATE PROCEDURE sp_tipo_recordatorio (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM tipo_recordatorio WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de recordatorio con ese nombre.';
        ELSE
            INSERT INTO tipo_recordatorio (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Tipo de recordatorio registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_recordatorio WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de recordatorio no encontrado.';
        ELSE
            UPDATE tipo_recordatorio
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de recordatorio actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_recordatorio WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de recordatorio no encontrado.';
        ELSE
            DELETE FROM tipo_recordatorio WHERE id = p_id;
            SET p_mensaje = 'Tipo de recordatorio eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_medio_solicitud
-- Gestión CRUD de medios por los que un cliente solicita una cita o servicio.
-- ========================================
DROP PROCEDURE IF EXISTS sp_medio_solicitud;
DELIMITER $$
CREATE PROCEDURE sp_medio_solicitud (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM medio_solicitud WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un medio de solicitud con ese nombre.';
        ELSE
            INSERT INTO medio_solicitud (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Medio de solicitud registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM medio_solicitud WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medio de solicitud no encontrado.';
        ELSE
            UPDATE medio_solicitud
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Medio de solicitud actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM medio_solicitud WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Medio de solicitud no encontrado.';
        ELSE
            DELETE FROM medio_solicitud WHERE id = p_id;
            SET p_mensaje = 'Medio de solicitud eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_estado_visita
-- Gestión CRUD de estados posibles de una visita física.
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_visita;
DELIMITER $$
CREATE PROCEDURE sp_estado_visita (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM estado_visita WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un estado de visita con ese nombre.';
        ELSE
            INSERT INTO estado_visita (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Estado de visita registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_visita WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado de visita no encontrado.';
        ELSE
            UPDATE estado_visita
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Estado de visita actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_visita WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Estado de visita no encontrado.';
        ELSE
            DELETE FROM estado_visita WHERE id = p_id;
            SET p_mensaje = 'Estado de visita eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- PROCEDIMIENTO: sp_tipo_servicios
-- Gestión CRUD del catálogo de servicios de la veterinaria.
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_servicios;
DELIMITER $$
CREATE PROCEDURE sp_tipo_servicios (
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'INSERT' THEN
        IF EXISTS (SELECT 1 FROM tipo_servicios WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de servicio con ese nombre.';
        ELSE
            INSERT INTO tipo_servicios (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, 1);
            SET p_mensaje = 'Tipo de servicio registrado correctamente.';
        END IF;

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_servicios WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de servicio no encontrado.';
        ELSE
            UPDATE tipo_servicios
            SET nombre = p_nombre, descripcion = p_descripcion
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de servicio actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_servicios WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: Tipo de servicio no encontrado.';
        ELSE
            DELETE FROM tipo_servicios WHERE id = p_id;
            SET p_mensaje = 'Tipo de servicio eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estado_historia_clinica'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_historia_clinica;
DELIMITER $$
CREATE PROCEDURE sp_estado_historia_clinica(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_historia_clinica WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado ya existe.';
        ELSE
            INSERT INTO estado_historia_clinica (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado historia clínica creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_historia_clinica;
        ELSE
            SELECT * FROM estado_historia_clinica WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_historia_clinica WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_historia_clinica WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro estado con ese nombre ya existe.';
        ELSE
            UPDATE estado_historia_clinica
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado historia clínica actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_historia_clinica WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSE
            DELETE FROM estado_historia_clinica WHERE id = p_id;
            SET p_mensaje = 'Estado historia clínica eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'tipos_archivo_clinico'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipos_archivo_clinico;
DELIMITER $$
CREATE PROCEDURE sp_tipos_archivo_clinico(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipos_archivo_clinico WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de archivo ya existe.';
        ELSE
            INSERT INTO tipos_archivo_clinico (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Tipo de archivo clínico creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipos_archivo_clinico;
        ELSE
            SELECT * FROM tipos_archivo_clinico WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_archivo_clinico WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de archivo no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipos_archivo_clinico WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro tipo de archivo con ese nombre ya existe.';
        ELSE
            UPDATE tipos_archivo_clinico
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de archivo clínico actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_archivo_clinico WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de archivo no existe.';
        ELSE
            DELETE FROM tipos_archivo_clinico WHERE id = p_id;
            SET p_mensaje = 'Tipo de archivo clínico eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'tipo_operacion'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_operacion;
DELIMITER $$
CREATE PROCEDURE sp_tipo_operacion(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(16),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_operacion WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de operación ya existe.';
        ELSE
            INSERT INTO tipo_operacion (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Tipo de operación creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_operacion;
        ELSE
            SELECT * FROM tipo_operacion WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_operacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de operación no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_operacion WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro tipo de operación con ese nombre ya existe.';
        ELSE
            UPDATE tipo_operacion
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de operación actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_operacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de operación no existe.';
        ELSE
            DELETE FROM tipo_operacion WHERE id = p_id;
            SET p_mensaje = 'Tipo de operación eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'tipo_movimiento'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_movimiento;
DELIMITER $$
CREATE PROCEDURE sp_tipo_movimiento(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_id_tipo_operacion INT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_movimiento WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento ya existe.';
        ELSEIF NOT EXISTS (SELECT 1 FROM tipo_operacion WHERE id = p_id_tipo_operacion) THEN
            SET p_mensaje = 'ERROR: El tipo de operación asociado no existe.';
        ELSE
            INSERT INTO tipo_movimiento (nombre, descripcion, id_tipo_operacion, activo)
            VALUES (p_nombre, p_descripcion, p_id_tipo_operacion, p_activo);
            SET p_mensaje = 'Tipo de movimiento creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_movimiento;
        ELSE
            SELECT * FROM tipo_movimiento WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_movimiento WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro tipo de movimiento con ese nombre ya existe.';
        ELSEIF NOT EXISTS (SELECT 1 FROM tipo_operacion WHERE id = p_id_tipo_operacion) THEN
            SET p_mensaje = 'ERROR: El tipo de operación asociado no existe.';
        ELSE
            UPDATE tipo_movimiento
            SET nombre = p_nombre, descripcion = p_descripcion, id_tipo_operacion = p_id_tipo_operacion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de movimiento actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento no existe.';
        ELSE
            DELETE FROM tipo_movimiento WHERE id = p_id;
            SET p_mensaje = 'Tipo de movimiento eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'estado_factura_compra'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_factura_compra;
DELIMITER $$
CREATE PROCEDURE sp_estado_factura_compra(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_factura_compra WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado de factura ya existe.';
        ELSE
            INSERT INTO estado_factura_compra (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado factura de compra creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_factura_compra;
        ELSE
            SELECT * FROM estado_factura_compra WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_factura_compra WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de factura no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_factura_compra WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro estado de factura con ese nombre ya existe.';
        ELSE
            UPDATE estado_factura_compra
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado factura de compra actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_factura_compra WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de factura no existe.';
        ELSE
            DELETE FROM estado_factura_compra WHERE id = p_id;
            SET p_mensaje = 'Estado factura de compra eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'almacenes'
-- ========================================
DROP PROCEDURE IF EXISTS sp_almacenes;
DELIMITER $$
CREATE PROCEDURE sp_almacenes(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM almacenes WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El almacén ya existe.';
        ELSE
            INSERT INTO almacenes (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Almacén creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM almacenes;
        ELSE
            SELECT * FROM almacenes WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM almacenes WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El almacén no existe.';
        ELSEIF EXISTS (SELECT 1 FROM almacenes WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otro almacén con ese nombre ya existe.';
        ELSE
            UPDATE almacenes
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Almacén actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM almacenes WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El almacén no existe.';
        ELSE
            DELETE FROM almacenes WHERE id = p_id;
            SET p_mensaje = 'Almacén eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'marcas'
-- ========================================
DROP PROCEDURE IF EXISTS sp_marcas;
DELIMITER $$
CREATE PROCEDURE sp_marcas(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM marcas WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: La marca ya existe.';
        ELSE
            INSERT INTO marcas (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Marca creada correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM marcas;
        ELSE
            SELECT * FROM marcas WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM marcas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La marca no existe.';
        ELSEIF EXISTS (SELECT 1 FROM marcas WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otra marca con ese nombre ya existe.';
        ELSE
            UPDATE marcas
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Marca actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM marcas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La marca no existe.';
        ELSE
            DELETE FROM marcas WHERE id = p_id;
            SET p_mensaje = 'Marca eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'presentaciones'
-- ========================================
DROP PROCEDURE IF EXISTS sp_presentaciones;
DELIMITER $$
CREATE PROCEDURE sp_presentaciones(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM presentaciones WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: La presentación ya existe.';
        ELSE
            INSERT INTO presentaciones (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Presentación creada correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM presentaciones;
        ELSE
            SELECT * FROM presentaciones WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM presentaciones WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La presentación no existe.';
        ELSEIF EXISTS (SELECT 1 FROM presentaciones WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otra presentación con ese nombre ya existe.';
        ELSE
            UPDATE presentaciones
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Presentación actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM presentaciones WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La presentación no existe.';
        ELSE
            DELETE FROM presentaciones WHERE id = p_id;
            SET p_mensaje = 'Presentación eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'categorias_productos'
-- ========================================
DROP PROCEDURE IF EXISTS sp_categorias_productos;
DELIMITER $$
CREATE PROCEDURE sp_categorias_productos(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion TEXT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM categorias_productos WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: La categoría ya existe.';
        ELSE
            INSERT INTO categorias_productos (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Categoría creada correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM categorias_productos;
        ELSE
            SELECT * FROM categorias_productos WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM categorias_productos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La categoría no existe.';
        ELSEIF EXISTS (SELECT 1 FROM categorias_productos WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Otra categoría con ese nombre ya existe.';
        ELSE
            UPDATE categorias_productos
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Categoría actualizada correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM categorias_productos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La categoría no existe.';
        ELSE
            DELETE FROM categorias_productos WHERE id = p_id;
            SET p_mensaje = 'Categoría eliminada correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_documento_venta'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_documento_venta;
DELIMITER $$
CREATE PROCEDURE sp_tipo_documento_venta(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_documento_venta WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de documento ya existe.';
        ELSE
            INSERT INTO tipo_documento_venta (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Tipo de documento creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_documento_venta;
        ELSE
            SELECT * FROM tipo_documento_venta WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_documento_venta WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de documento no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_documento_venta WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro tipo de documento con ese nombre.';
        ELSE
            UPDATE tipo_documento_venta
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de documento actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_documento_venta WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de documento no existe.';
        ELSE
            DELETE FROM tipo_documento_venta WHERE id = p_id;
            SET p_mensaje = 'Tipo de documento eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;


-- ========================================
-- CRUD: TABLA 'tipo_item_factura'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_item_factura;
DELIMITER $$
CREATE PROCEDURE sp_tipo_item_factura(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_item_factura WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de ítem ya existe.';
        ELSE
            INSERT INTO tipo_item_factura (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Tipo de ítem creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_item_factura;
        ELSE
            SELECT * FROM tipo_item_factura WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_item_factura WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de ítem no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_item_factura WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro tipo de ítem con ese nombre.';
        ELSE
            UPDATE tipo_item_factura
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de ítem actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_item_factura WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de ítem no existe.';
        ELSE
            DELETE FROM tipo_item_factura WHERE id = p_id;
            SET p_mensaje = 'Tipo de ítem eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estado_nota_credito'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_nota_credito;
DELIMITER $$
CREATE PROCEDURE sp_estado_nota_credito(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_nota_credito WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado ya existe.';
        ELSE
            INSERT INTO estado_nota_credito (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado de nota de crédito creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_nota_credito;
        ELSE
            SELECT * FROM estado_nota_credito WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_nota_credito WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro estado con ese nombre.';
        ELSE
            UPDATE estado_nota_credito
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSE
            DELETE FROM estado_nota_credito WHERE id = p_id;
            SET p_mensaje = 'Estado eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estado_nota_credito'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_nota_credito;
DELIMITER $$
CREATE PROCEDURE sp_estado_nota_credito(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_nota_credito WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado ya existe.';
        ELSE
            INSERT INTO estado_nota_credito (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado de nota de crédito creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_nota_credito;
        ELSE
            SELECT * FROM estado_nota_credito WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_nota_credito WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro estado con ese nombre.';
        ELSE
            UPDATE estado_nota_credito
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSE
            DELETE FROM estado_nota_credito WHERE id = p_id;
            SET p_mensaje = 'Estado eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_nota_credito'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_nota_credito;
DELIMITER $$
CREATE PROCEDURE sp_tipo_nota_credito(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_nota_credito WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de nota ya existe.';
        ELSE
            INSERT INTO tipo_nota_credito (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Tipo de nota de crédito creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_nota_credito;
        ELSE
            SELECT * FROM tipo_nota_credito WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_nota_credito WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro tipo con ese nombre.';
        ELSE
            UPDATE tipo_nota_credito
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_nota_credito WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo no existe.';
        ELSE
            DELETE FROM tipo_nota_credito WHERE id = p_id;
            SET p_mensaje = 'Tipo eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipo_movimiento_caja'
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipo_movimiento_caja;
DELIMITER $$
CREATE PROCEDURE sp_tipo_movimiento_caja(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipo_movimiento_caja WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento ya existe.';
        ELSE
            INSERT INTO tipo_movimiento_caja (nombre, activo)
            VALUES (p_nombre, p_activo);
            SET p_mensaje = 'Tipo de movimiento de caja creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipo_movimiento_caja;
        ELSE
            SELECT * FROM tipo_movimiento_caja WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_movimiento_caja WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento no existe.';
        ELSEIF EXISTS (SELECT 1 FROM tipo_movimiento_caja WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro tipo de movimiento con ese nombre.';
        ELSE
            UPDATE tipo_movimiento_caja
            SET nombre = p_nombre, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Tipo de movimiento de caja actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipo_movimiento_caja WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de movimiento no existe.';
        ELSE
            DELETE FROM tipo_movimiento_caja WHERE id = p_id;
            SET p_mensaje = 'Tipo de movimiento de caja eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estado_factura_venta'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_factura_venta;
DELIMITER $$
CREATE PROCEDURE sp_estado_factura_venta(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_factura_venta WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado ya existe.';
        ELSE
            INSERT INTO estado_factura_venta (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado de factura creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_factura_venta;
        ELSE
            SELECT * FROM estado_factura_venta WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_factura_venta WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_factura_venta WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro estado con ese nombre.';
        ELSE
            UPDATE estado_factura_venta
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado de factura actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_factura_venta WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado no existe.';
        ELSE
            DELETE FROM estado_factura_venta WHERE id = p_id;
            SET p_mensaje = 'Estado de factura eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estado_caja'
-- ========================================
DROP PROCEDURE IF EXISTS sp_estado_caja;
DELIMITER $$
CREATE PROCEDURE sp_estado_caja(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(32),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estado_caja WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: El estado de caja ya existe.';
        ELSE
            INSERT INTO estado_caja (nombre, descripcion, activo)
            VALUES (p_nombre, p_descripcion, p_activo);
            SET p_mensaje = 'Estado de caja creado correctamente.';
        END IF;

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estado_caja;
        ELSE
            SELECT * FROM estado_caja WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_caja WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de caja no existe.';
        ELSEIF EXISTS (SELECT 1 FROM estado_caja WHERE nombre = p_nombre AND id <> p_id) THEN
            SET p_mensaje = 'ERROR: Ya existe otro estado de caja con ese nombre.';
        ELSE
            UPDATE estado_caja
            SET nombre = p_nombre, descripcion = p_descripcion, activo = p_activo
            WHERE id = p_id;
            SET p_mensaje = 'Estado de caja actualizado correctamente.';
        END IF;

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estado_caja WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de caja no existe.';
        ELSE
            DELETE FROM estado_caja WHERE id = p_id;
            SET p_mensaje = 'Estado de caja eliminado correctamente.';
        END IF;

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;








