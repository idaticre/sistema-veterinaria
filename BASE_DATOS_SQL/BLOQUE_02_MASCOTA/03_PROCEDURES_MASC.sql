-- ================================================================
-- SCRIPT: PROCEDIMIENTOS ALMACENADOS CRUD - SISTEMA VETERINARIA_WOOF
-- ================================================================
USE vet_manada_woof;

-- ========================================
-- CRUD: TABLA 'especies'
-- Manejo de especies de mascotas con validaciones y códigos generados
-- ========================================
DROP PROCEDURE IF EXISTS sp_especies;
DELIMITER $$
CREATE PROCEDURE sp_especies(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    -- Normalizamos acción
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM especies WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una especie con ese nombre.';
            LEAVE main_block;
        END IF;
        INSERT INTO especies (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE especies
        SET codigo = CONCAT('ESP', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Especie creada correctamente con código ', (SELECT codigo FROM especies WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM especies;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe especie con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM especies WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especie no existe.';
            LEAVE main_block;
        END IF;
        UPDATE especies
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;
        SET p_mensaje = CONCAT('Especie con código ', (SELECT codigo FROM especies WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La especie no existe.';
            LEAVE main_block;
        END IF;
        SET @codigo = (SELECT codigo FROM especies WHERE id = p_id);
        DELETE FROM especies WHERE id = p_id;
        SET p_mensaje = CONCAT('Especie con código ', @codigo, ' eliminada correctamente.');
        
    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'razas'
-- Manejo de razas de mascotas con validaciones y códigos generados
-- ========================================
DROP PROCEDURE IF EXISTS sp_razas;
DELIMITER $$
CREATE PROCEDURE sp_razas(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_id_especie INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    -- Normalizamos acción
    SET p_accion = UPPER(TRIM(p_accion));

    -- CREATE
    IF p_accion = 'CREATE' THEN
        -- Validación: existencia de especie
        IF NOT EXISTS (SELECT 1 FROM especies WHERE id = p_id_especie) THEN
            SET p_mensaje = 'ERROR: La especie indicada no existe.';
            LEAVE main_block;
        END IF;

        -- Validación: nombre único por especie
        IF EXISTS (SELECT 1 FROM razas WHERE nombre = p_nombre AND id_especie = p_id_especie) THEN
            SET p_mensaje = 'ERROR: Ya existe una raza con ese nombre en la especie indicada.';
            LEAVE main_block;
        END IF;

        INSERT INTO razas (id_especie, nombre, descripcion, activo)
        VALUES (p_id_especie, p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE razas
        SET codigo = CONCAT('RAZ', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Raza creada correctamente con código ', (SELECT codigo FROM razas WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT r.*, e.nombre AS especie
            FROM razas r
            INNER JOIN especies e ON r.id_especie = e.id;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM razas WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe raza con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT r.*, e.nombre AS especie
            FROM razas r
            INNER JOIN especies e ON r.id_especie = e.id
            WHERE r.id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM razas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La raza no existe.';
            LEAVE main_block;
        END IF;

        UPDATE razas
        SET id_especie = p_id_especie,
            nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Raza con código ', (SELECT codigo FROM razas WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM razas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La raza no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM razas WHERE id = p_id);
        DELETE FROM razas WHERE id = p_id;

        SET p_mensaje = CONCAT('Raza con código ', @codigo, ' eliminada correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tamanios'
-- Manejo de tamaños de mascotas
-- ========================================
DROP PROCEDURE IF EXISTS sp_tamanios;
DELIMITER $$
CREATE PROCEDURE sp_tamanios(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tamanios WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tamaño con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO tamanios (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE tamanios
        SET codigo = CONCAT('TAM', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Tamaño creado correctamente con código ', (SELECT codigo FROM tamanios WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tamanios;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tamanios WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe tamaño con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM tamanios WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tamanios WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tamaño no existe.';
            LEAVE main_block;
        END IF;

        UPDATE tamanios
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Tamaño con código ', (SELECT codigo FROM tamanios WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tamanios WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tamaño no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM tamanios WHERE id = p_id);
        DELETE FROM tamanios WHERE id = p_id;

        SET p_mensaje = CONCAT('Tamaño con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'etapas_vida'
-- Manejo de etapas de vida de mascotas
-- ========================================
DROP PROCEDURE IF EXISTS sp_etapas_vida;
DELIMITER $$
CREATE PROCEDURE sp_etapas_vida(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_descripcion VARCHAR(128),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM etapas_vida WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una etapa con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO etapas_vida (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE etapas_vida
        SET codigo = CONCAT('ETA', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Etapa creada correctamente con código ', (SELECT codigo FROM etapas_vida WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM etapas_vida;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM etapas_vida WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe etapa con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM etapas_vida WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM etapas_vida WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La etapa no existe.';
            LEAVE main_block;
        END IF;

        UPDATE etapas_vida
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Etapa con código ', (SELECT codigo FROM etapas_vida WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM etapas_vida WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La etapa no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM etapas_vida WHERE id = p_id);
        DELETE FROM etapas_vida WHERE id = p_id;

        SET p_mensaje = CONCAT('Etapa con código ', @codigo, ' eliminada correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'vacunas'
-- Manejo del catálogo de vacunas para mascotas
-- ========================================
DROP PROCEDURE IF EXISTS sp_vacunas;
DELIMITER $$
CREATE PROCEDURE sp_vacunas(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(128),
    IN p_descripcion VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM vacunas WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una vacuna con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO vacunas (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE vacunas
        SET codigo = CONCAT('VAC', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Vacuna creada correctamente con código ', (SELECT codigo FROM vacunas WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM vacunas;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe vacuna con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM vacunas WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La vacuna no existe.';
            LEAVE main_block;
        END IF;

        UPDATE vacunas
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Vacuna con código ', (SELECT codigo FROM vacunas WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La vacuna no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM vacunas WHERE id = p_id);
        DELETE FROM vacunas WHERE id = p_id;

        SET p_mensaje = CONCAT('Vacuna con código ', @codigo, ' eliminada correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'tipos_medicamento'
-- Manejo de categorías de tipos de medicamento
-- ========================================
DROP PROCEDURE IF EXISTS sp_tipos_medicamento;
DELIMITER $$
CREATE PROCEDURE sp_tipos_medicamento(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(128),
    IN p_descripcion VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM tipos_medicamento WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un tipo de medicamento con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO tipos_medicamento (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE tipos_medicamento
        SET codigo = CONCAT('TPM', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Tipo de medicamento creado correctamente con código ', (SELECT codigo FROM tipos_medicamento WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM tipos_medicamento;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM tipos_medicamento WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe tipo de medicamento con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM tipos_medicamento WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_medicamento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de medicamento no existe.';
            LEAVE main_block;
        END IF;

        UPDATE tipos_medicamento
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Tipo de medicamento con código ', (SELECT codigo FROM tipos_medicamento WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM tipos_medicamento WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El tipo de medicamento no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM tipos_medicamento WHERE id = p_id);
        DELETE FROM tipos_medicamento WHERE id = p_id;

        SET p_mensaje = CONCAT('Tipo de medicamento con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'medicamentos'
-- Manejo del catálogo de medicamentos veterinarios
-- ========================================
DROP PROCEDURE IF EXISTS sp_medicamentos;
DELIMITER $$
CREATE PROCEDURE sp_medicamentos(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(128),
    IN p_descripcion VARCHAR(255),
    IN p_id_tipo_medicamento INT,
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM medicamentos WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un medicamento con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO medicamentos (nombre, descripcion, id_tipo_medicamento, activo)
        VALUES (p_nombre, p_descripcion, p_id_tipo_medicamento, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE medicamentos
        SET codigo = CONCAT('MED', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Medicamento creado correctamente con código ', (SELECT codigo FROM medicamentos WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT m.*, tm.nombre AS tipo_medicamento
            FROM medicamentos m
            INNER JOIN tipos_medicamento tm ON m.id_tipo_medicamento = tm.id;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe medicamento con id ', p_id, '.');
                LEAVE main_block;
            END IF;

            SELECT m.*, tm.nombre AS tipo_medicamento
            FROM medicamentos m
            INNER JOIN tipos_medicamento tm ON m.id_tipo_medicamento = tm.id
            WHERE m.id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El medicamento no existe.';
            LEAVE main_block;
        END IF;

        UPDATE medicamentos
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            id_tipo_medicamento = p_id_tipo_medicamento,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Medicamento con código ', (SELECT codigo FROM medicamentos WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El medicamento no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM medicamentos WHERE id = p_id);
        DELETE FROM medicamentos WHERE id = p_id;

        SET p_mensaje = CONCAT('Medicamento con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'vias_aplicacion'
-- Manejo de catálogo de vías de aplicación de medicamentos
-- ========================================
DROP PROCEDURE IF EXISTS sp_vias_aplicacion;
DELIMITER $$
CREATE PROCEDURE sp_vias_aplicacion(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(128),
    IN p_descripcion VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM vias_aplicacion WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe una vía de aplicación con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO vias_aplicacion (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE vias_aplicacion
        SET codigo = CONCAT('VIA', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Vía de aplicación creada correctamente con código ', (SELECT codigo FROM vias_aplicacion WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM vias_aplicacion;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe vía de aplicación con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM vias_aplicacion WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La vía de aplicación no existe.';
            LEAVE main_block;
        END IF;

        UPDATE vias_aplicacion
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Vía de aplicación con código ', (SELECT codigo FROM vias_aplicacion WHERE id = p_id), ' actualizada correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM vias_aplicacion WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: La vía de aplicación no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM vias_aplicacion WHERE id = p_id);
        DELETE FROM vias_aplicacion WHERE id = p_id;

        SET p_mensaje = CONCAT('Vía de aplicación con código ', @codigo, ' eliminada correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'estados_mascota'
-- Manejo de catálogo de estados de la mascota (activo, en tratamiento, fallecido, etc.)
-- ========================================
DROP PROCEDURE IF EXISTS sp_estados_mascota;
DELIMITER $$
CREATE PROCEDURE sp_estados_mascota(
    IN p_accion VARCHAR(10),
    IN p_id INT,
    IN p_nombre VARCHAR(128),
    IN p_descripcion VARCHAR(255),
    IN p_activo TINYINT,
    OUT p_mensaje VARCHAR(255)
)
main_block: BEGIN
    SET p_accion = UPPER(TRIM(p_accion));

    IF p_accion = 'CREATE' THEN
        IF EXISTS (SELECT 1 FROM estados_mascota WHERE nombre = p_nombre) THEN
            SET p_mensaje = 'ERROR: Ya existe un estado de mascota con ese nombre.';
            LEAVE main_block;
        END IF;

        INSERT INTO estados_mascota (nombre, descripcion, activo)
        VALUES (p_nombre, p_descripcion, p_activo);

        SET @last_id = LAST_INSERT_ID();
        UPDATE estados_mascota
        SET codigo = CONCAT('EST', LPAD(@last_id, 3, '0'))
        WHERE id = @last_id;

        SET p_mensaje = CONCAT('Estado de mascota creado correctamente con código ', (SELECT codigo FROM estados_mascota WHERE id = @last_id));

    ELSEIF p_accion = 'READ' THEN
        IF p_id IS NULL THEN
            SELECT * FROM estados_mascota;
        ELSE
            IF NOT EXISTS (SELECT 1 FROM estados_mascota WHERE id = p_id) THEN
                SET p_mensaje = CONCAT('ERROR: No existe estado de mascota con id ', p_id, '.');
                LEAVE main_block;
            END IF;
            SELECT * FROM estados_mascota WHERE id = p_id;
        END IF;
        SET p_mensaje = 'Consulta realizada correctamente.';

    ELSEIF p_accion = 'UPDATE' THEN
        IF NOT EXISTS (SELECT 1 FROM estados_mascota WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de mascota no existe.';
            LEAVE main_block;
        END IF;

        UPDATE estados_mascota
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            activo = p_activo
        WHERE id = p_id;

        SET p_mensaje = CONCAT('Estado de mascota con código ', (SELECT codigo FROM estados_mascota WHERE id = p_id), ' actualizado correctamente.');

    ELSEIF p_accion = 'DELETE' THEN
        IF NOT EXISTS (SELECT 1 FROM estados_mascota WHERE id = p_id) THEN
            SET p_mensaje = 'ERROR: El estado de mascota no existe.';
            LEAVE main_block;
        END IF;

        SET @codigo = (SELECT codigo FROM estados_mascota WHERE id = p_id);
        DELETE FROM estados_mascota WHERE id = p_id;

        SET p_mensaje = CONCAT('Estado de mascota con código ', @codigo, ' eliminado correctamente.');

    ELSE
        SET p_mensaje = 'ERROR: Acción no válida. Use CREATE, READ, UPDATE o DELETE.';
    END IF;
END$$
DELIMITER ;

-- ========================================
-- CRUD: TABLA 'mascotas'
-- Manejo de registro, consulta y actualización (incluye eliminación lógica vía id_estado)
-- ========================================

-- ========================================
-- SP: Registrar Mascota
-- Inserta nueva mascota, valida cliente y duplicados, crea historia clínica inicial
-- ========================================
DROP PROCEDURE IF EXISTS registrar_mascota;
DELIMITER $$

CREATE PROCEDURE registrar_mascota (
    IN p_codigo VARCHAR(16),
    IN p_nombre VARCHAR(64),
    IN p_sexo VARCHAR(1),
    IN p_id_cliente INT,
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
    IN p_foto VARCHAR(255)
)
BEGIN
    DECLARE v_estado_activa INT;

    -- Validar cliente
    IF NOT EXISTS (SELECT 1 FROM clientes WHERE id = p_id_cliente) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El cliente no existe';
    END IF;

    -- Prevenir duplicados
    IF EXISTS (
        SELECT 1 FROM mascotas
        WHERE nombre = p_nombre 
          AND id_cliente = p_id_cliente 
          AND id_especie = p_id_especie
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe una mascota con el mismo nombre para este cliente y especie';
    END IF;

    -- Estado por defecto = ACTIVA
    SELECT id INTO v_estado_activa 
    FROM estado_mascota 
    WHERE nombre = 'ACTIVA';

    -- Insertar mascota
    INSERT INTO mascotas (
        codigo, nombre, sexo, id_cliente, id_raza, id_especie,
        id_estado, fecha_nacimiento, pelaje, id_tamano, id_etapa,
        esterilizado, alergias, peso, chip, pedigree, factor_dea,
        agresividad, foto
    )
    VALUES (
        p_codigo, p_nombre, p_sexo, p_id_cliente, p_id_raza, p_id_especie,
        v_estado_activa, p_fecha_nacimiento, p_pelaje, p_id_tamano, p_id_etapa,
        p_esterilizado, p_alergias, p_peso, p_chip, p_pedigree, p_factor_dea,
        p_agresividad, p_foto
    );

    -- Crear historia clínica inicial
    INSERT INTO historia_clinica (id_mascota, id_estado, fecha_apertura)
    VALUES (LAST_INSERT_ID(), 1, NOW()); -- 1 = "Abierta"
END $$

DELIMITER ;

-- ========================================
-- SP: Consultar Mascota
-- Devuelve info de la mascota con estado y cliente
-- ========================================
DROP PROCEDURE IF EXISTS consultar_mascota;
DELIMITER $$

CREATE PROCEDURE consultar_mascota (IN p_id INT)
BEGIN
    SELECT m.*, e.nombre AS estado_nombre, c.id AS id_cliente
    FROM mascotas m
    INNER JOIN estado_mascota e ON m.id_estado = e.id
    INNER JOIN clientes c ON m.id_cliente = c.id
    WHERE m.id = p_id;
END $$

DELIMITER ;

-- ========================================
-- SP: Actualizar Mascota
-- Permite modificar atributos generales y también el estado (para eliminación lógica)
-- ========================================
DROP PROCEDURE IF EXISTS actualizar_mascota;
DELIMITER $$

CREATE PROCEDURE actualizar_mascota (
    IN p_id INT,
    IN p_nombre VARCHAR(64),
    IN p_sexo VARCHAR(1),
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
    IN p_foto VARCHAR(255)
)
BEGIN
    -- Validar existencia
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE id = p_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La mascota no existe';
    END IF;

    -- Actualizar mascota (incluye cambio de estado si aplica)
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
    WHERE id = p_id;
END $$

DELIMITER ;
