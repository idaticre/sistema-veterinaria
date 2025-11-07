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
