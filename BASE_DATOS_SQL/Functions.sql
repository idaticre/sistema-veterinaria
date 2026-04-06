USE vet_manada_woof_loc;
-- ========================================
-- FUNCIÓN: obtener_horario_vigente
-- Devuelve el id del horario_base aplicable a un colaborador en una fecha dada.
-- Usa la tabla asignacion_horarios y respeta la vigencia más reciente.
-- ========================================
DROP FUNCTION IF EXISTS obtener_horario_vigente;
DELIMITER $$
CREATE FUNCTION obtener_horario_vigente(
    p_id_colaborador BIGINT,
    p_fecha DATE
) RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_id_horario INT DEFAULT NULL;  -- mejor NULL que 0
    DECLARE v_dia_semana INT;

    SET v_dia_semana = WEEKDAY(p_fecha) + 1; 
    
    SELECT ah.id_horario_base INTO v_id_horario
    FROM asignacion_horarios ah
    WHERE ah.id_colaborador = p_id_colaborador
      AND ah.id_dia_semana = v_dia_semana
      AND ah.activo = 1
      AND p_fecha >= ah.fecha_inicio_vigencia
      AND (ah.fecha_fin_vigencia IS NULL OR p_fecha <= ah.fecha_fin_vigencia)
    ORDER BY ah.fecha_inicio_vigencia DESC 
    LIMIT 1;

    RETURN COALESCE(v_id_horario, 0);
END$$
DELIMITER ;
