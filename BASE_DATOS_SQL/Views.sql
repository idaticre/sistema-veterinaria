USE vet_manada_woof;


CREATE OR REPLACE VIEW vista_asistencia_hoy AS
SELECT 
  c.id AS id_colaborador,
  CONCAT(e.nombre) AS colaborador,
  hb.nombre AS horario,
  ah.id_dia_semana,
  ra.hora_entrada,
  ra.hora_lunch_inicio,
  ra.hora_lunch_fin,
  ra.hora_salida,
  ra.estado
FROM asignacion_horarios ah
JOIN horarios_base hb ON ah.id_horario_base = hb.id
JOIN colaboradores c ON c.id = ah.id_colaborador
JOIN entidades e ON e.id = c.id_entidad   -- 🔹 se agrega esta unión
LEFT JOIN registro_asistencia ra 
  ON ra.id_colaborador = ah.id_colaborador 
 AND ra.fecha = CURDATE()
WHERE ah.id_dia_semana = CASE DAYOFWEEK(CURDATE())
                            WHEN 1 THEN 7
                            ELSE DAYOFWEEK(CURDATE()) - 1
                         END
  AND ah.activo = 1;


ALTER TABLE horarios_base 
ADD CONSTRAINT uq_nombre_descanso UNIQUE (nombre);



-- ========================================
-- SP: Reporte de asistencias por rango de fechas (el que te faltaba)
-- ========================================
DROP PROCEDURE IF EXISTS ver_asistencia_por_rango;
DELIMITER $$
CREATE PROCEDURE ver_asistencia_por_rango(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_id_estado INT  -- NULL = todos los estados
)
BEGIN
    SELECT 
        ra.id_colaborador AS idColaborador,
        CONCAT(col.nombres, ' ', col.apellidos) AS colaborador,
        COALESCE(hb.nombre, 'Descanso') AS horario,
        ra.fecha,
        ra.hora_entrada,
        ra.hora_lunch_inicio,
        ra.hora_lunch_fin,
        ra.hora_salida,
        ra.minutos_trabajados,
        ra.minutos_lunch,
        ra.tardanza_minutos,
        ea.nombre AS estadoAsistencia,
        ra.observaciones,
        ra.registro_origen
    FROM registro_asistencias ra
    JOIN colaboradores col ON col.id = ra.id_colaborador
    LEFT JOIN horarios_base hb ON hb.id = ra.id_horario_base
    JOIN estado_asistencia ea ON ea.id = ra.id_estado_asistencia
    WHERE ra.fecha BETWEEN p_fecha_inicio AND p_fecha_fin
      AND (p_id_estado IS NULL OR ra.id_estado_asistencia = p_id_estado)
    ORDER BY ra.fecha DESC, colaborador;
END$$
DELIMITER ;