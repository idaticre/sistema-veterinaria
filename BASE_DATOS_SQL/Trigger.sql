USE vet_manada_woof;

DELIMITER $$

CREATE TRIGGER trg_validar_abono_agenda
BEFORE INSERT ON agenda_pagos
FOR EACH ROW
BEGIN
    DECLARE total_cita DECIMAL(10,2);
    DECLARE suma_abonos DECIMAL(10,2);

    SELECT total_cita INTO total_cita FROM agenda WHERE id = NEW.id_agenda;
    SELECT IFNULL(SUM(monto), 0) INTO suma_abonos FROM agenda_pagos WHERE id_agenda = NEW.id_agenda;

    IF (suma_abonos + NEW.monto) > total_cita THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El abono total no puede superar el valor total de la cita';
    END IF;
END$$

DELIMITER ;




-- COPIAR AUTOMÁTICAMENTE HORARIOS DEL ROL
    IF EXISTS (SELECT 1 FROM colaboradores WHERE id = v_id_colaborador AND id_rol IS NOT NULL) THEN
        INSERT INTO asignacion_horarios (
            id_colaborador,
            id_horario_base,
            id_dia_semana,
            fecha_inicio_vigencia,
            motivo_cambio,
            activo
        )
        SELECT 
            v_id_colaborador,
            hbr.id_horario_base,
            hbr.id_dia_semana,
            p_fecha_ingreso,
            CONCAT('Copia automática del rol al ingresar'),
            1
        FROM horarios_base_roles hbr
        WHERE hbr.id_rol = (SELECT id_rol FROM colaboradores WHERE id = v_id_colaborador)
        ON DUPLICATE KEY UPDATE activo = 1;  -- por si algún día se vuelve a ejecutar
    END IF;