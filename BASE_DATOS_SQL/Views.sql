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
