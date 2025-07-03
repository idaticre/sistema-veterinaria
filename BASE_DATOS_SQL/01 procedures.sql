-- PROCEDIMIENTO: generar_factura
DELIMITER $$
CREATE PROCEDURE generar_factura (
    IN p_id_cliente INT,
    IN p_id_medio_pago INT,
    IN p_impuestos DECIMAL(10,2),
    IN p_descuentos DECIMAL(10,2)
)
BEGIN
    DECLARE v_id_factura INT;
    DECLARE v_subtotal DECIMAL(10,2) DEFAULT 0.00;

    -- Crear la cabecera con subtotal temporalmente en 0
    INSERT INTO factura_cabecera (fecha_factura, id_cliente, id_medio_pago, subtotal, impuestos, descuentos, total)
    VALUES (CURRENT_DATE, p_id_cliente, p_id_medio_pago, 0, p_impuestos, p_descuentos, 0);

    SET v_id_factura = LAST_INSERT_ID();

    -- Aquí se espera que detalle_factura esté poblado temporalmente (por app o lógica previa)
    SELECT SUM(total_item) INTO v_subtotal FROM detalle_factura WHERE id_factura = v_id_factura;

    -- Actualizar la cabecera con los totales correctos
    UPDATE factura_cabecera
    SET subtotal = v_subtotal,
        total = v_subtotal + p_impuestos - p_descuentos
    WHERE id = v_id_factura;

    -- Retornar el ID de la factura creada
    SELECT v_id_factura AS factura_generada;
END $$
DELIMITER ;
-- ============================================================
-- CUANDO SE AGENDE SE GENERE EL RECORDATORIO
DELIMITER $$

CREATE TRIGGER crear_recordatorio_al_agendar
AFTER INSERT ON agenda
FOR EACH ROW
BEGIN
    INSERT INTO recordatorios_agenda (id_agenda, fecha_recordatorio, hora, mensaje, enviado)
    VALUES (
        NEW.id,
        DATE_SUB(NEW.fecha, INTERVAL 1 DAY),  -- 1 día antes
        NEW.hora,
        CONCAT('Recordatorio: cita para el ', DATE_FORMAT(NEW.fecha, '%d/%m/%Y'), ' a las ', TIME_FORMAT(NEW.hora, '%H:%i')),
        0
    );
END$$

DELIMITER ;

-- ============================================================
-- Aqui se actualiza el precio del producto cuando se registra la orden de compra

DELIMITER $$

CREATE TRIGGER trg_actualizar_precio_producto
AFTER INSERT ON detalle_orden_compra
FOR EACH ROW
BEGIN
    DECLARE nuevo_precio DECIMAL(10,2);
    DECLARE margen DECIMAL(5,2);

    SET nuevo_precio = NEW.precio_unitario;
    SET margen = 1.30; -- 30% margen sugerido

    UPDATE productos
    SET 
        precio_compra_base = nuevo_precio,
        precio_sugerido_venta = ROUND(nuevo_precio * margen, 2)
    WHERE id = NEW.id_producto;
END$$

DELIMITER ;

-- ===============================================================
-- se creara una historia clinica general al momento de registrar la mascota, 
-- en adelante cada visita y consulta medica ira en un solo registro por mascota
DELIMITER $$

CREATE TRIGGER trg_crear_historia_clinica
AFTER INSERT ON mascotas
FOR EACH ROW
BEGIN
    INSERT INTO historia_clinica (
        id_mascota,
        fecha,
        tipo_servicio,
        descripcion,
        observaciones
    )
    VALUES (
        NEW.id,
        CURDATE(),
        'Registro Inicial',
        CONCAT('Inicio de historia clínica para la mascota: ', NEW.nombre),
        'Creación automática al registrar mascota'
    );
END$$

DELIMITER ;

-- ===============================================================================
-- este procedimiento guarda las visitas en la historia clinica general cuando se han cambiado a completado

DELIMITER $$

CREATE TRIGGER trg_insert_historia_clinica
AFTER UPDATE ON visitas_ingresos
FOR EACH ROW
BEGIN
    DECLARE servicio_nombre VARCHAR(64); -- Primero las declaraciones

    -- Solo cuando cambia a 'completado'
    IF NEW.estado = 'completado' AND OLD.estado <> 'completado' THEN

        -- Obtenemos el nombre del servicio
        SELECT ts.nombre INTO servicio_nombre
        FROM ingresos_servicios iser
        JOIN tipo_servicios ts ON ts.id = iser.id_servicio
        WHERE iser.id = NEW.id_ingreso_servicio;

        -- Insertamos en historia clínica
        INSERT INTO historia_clinica (
            id_mascota,
            id_visita,
            id_ingreso_servicio,
            fecha,
            tipo_servicio,
            descripcion
        ) VALUES (
            NEW.id_mascota,
            NEW.id,
            NEW.id_ingreso_servicio,
            NEW.fecha_ingreso,
            servicio_nombre,
            CONCAT('Servicio registrado como ', servicio_nombre, ' (visita completada)')
        );
    END IF;
END$$

DELIMITER ;
-- ==================================================================================== 
-- este procedimiento registra a un cliente cuando el frontend hace uso del formulario clientes
-- registra los datos en entidades
DELIMITER $$

CREATE PROCEDURE registrar_cliente(
    IN p_tipo_entidad ENUM('natural','juridica'),
    IN p_nombre VARCHAR(128),
    IN p_documento BIGINT,
    IN p_id_tipo_documento INT,
    IN p_correo VARCHAR(64),
    IN p_telefono VARCHAR(15),
    IN p_direccion VARCHAR(256),
    IN p_ciudad VARCHAR(64),
    IN p_distrito VARCHAR(64),
    IN p_representante VARCHAR(64)
)
BEGIN
    DECLARE v_id_entidad INT;
    DECLARE v_id_cliente INT;

    -- ¿Ya existe la entidad?
    SELECT id INTO v_id_entidad
    FROM entidades
    WHERE documento = p_documento;

    -- Si no existe, insertamos en entidades
    IF v_id_entidad IS NULL THEN
        INSERT INTO entidades (
            tipo_entidad, nombre, documento, id_tipo_documento,
            correo, telefono, direccion, ciudad, distrito, representante
        ) VALUES (
            p_tipo_entidad, p_nombre, p_documento, p_id_tipo_documento,
            p_correo, p_telefono, p_direccion, p_ciudad, p_distrito, p_representante
        );

        SET v_id_entidad = LAST_INSERT_ID();
    END IF;

    -- ¿Ya es cliente?
    SELECT id INTO v_id_cliente
    FROM clientes
    WHERE id_entidad = v_id_entidad;

    -- Si no es cliente, insertamos
    IF v_id_cliente IS NULL THEN
        INSERT INTO clientes (id_entidad, activo)
        VALUES (v_id_entidad, 1);
    END IF;

END $$

DELIMITER ;











