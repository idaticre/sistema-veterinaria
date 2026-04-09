-- =========================================================
-- SCRIPT DE DATOS DE PRUEBA: VetManadaWoof

use vet_manada_woof;

-- SP: registrar_colaborador
-- Colaborador 1 → admin_woof (id_usuario = 1)
CALL registrar_colaborador(
    1, 'Sandra Laguna De La Rosa', 'F', '45123456', 1,
    'sandra.laguna@manadawoof.com', '917233145',
    'Jiron Arequipa 238', 'Lima', 'Magdalena del Mar',
    '2020-01-15', 1, NULL,
    @cod_ent, @cod_col, @msg
);
SELECT @cod_ent, @cod_col, @msg;

-- Colaborador 2 → admin_g2 (id_usuario = 2)
CALL registrar_colaborador(
    1, 'Carlos Mendoza Rivera', 'M', '45234567', 1,
    'carlos.mendoza@manadawoof.com', '987654321',
    'Av. Brasil 450', 'Lima', 'Pueblo Libre',
    '2020-03-01', 2, NULL,
    @cod_ent, @cod_col, @msg
);
SELECT @cod_ent, @cod_col, @msg;

-- Colaborador 3 → caja_milo (id_usuario = 3)
CALL registrar_colaborador(
    1, 'Milagros Torres Huanca', 'F', '45345678', 1,
    'milagros.torres@manadawoof.com', '976543210',
    'Av. Universitaria 123', 'Lima', 'San Miguel',
    '2021-06-01', 3, NULL,
    @cod_ent, @cod_col, @msg
);
SELECT @cod_ent, @cod_col, @msg;

-- Colaborador 4 → gromer_luna (id_usuario = 4)
CALL registrar_colaborador(
    1, 'Luna Paredes Castillo', 'F', '45456789', 1,
    'luna.paredes@manadawoof.com', '965432109',
    'Calle Los Pinos 789', 'Lima', 'Lince',
    '2021-09-15', 4, NULL,
    @cod_ent, @cod_col, @msg
);
SELECT @cod_ent, @cod_col, @msg;

-- Colaborador 5 → veterinario (sin usuario)
CALL registrar_colaborador(
    1, 'Diego Alvarado Quispe', 'M', '45567890', 1,
    'diego.alvarado@manadawoof.com', '954321098',
    'Av. Salaverry 234', 'Lima', 'Jesus Maria',
    '2022-01-10', NULL, NULL,
    @cod_ent, @cod_col, @msg
);
SELECT @cod_ent, @cod_col, @msg;

-- SP: gestionar_rol_usuario
-- Auto-asigna horarios al asignar el primer rol

CALL gestionar_rol_usuario('ASIGNAR', 1, 1, @msg); SELECT @msg; -- admin_woof → ADMINISTRADOR GENERAL

-- SP: registrar_veterinario
-- Usamos id_entidad del colaborador 5 (ENT000005)

CALL registrar_veterinario(
    5, 1, 'Diego Alvarado Quispe', 'M', '45567890', 1,
    'diego.alvarado@manadawoof.com', '954321098',
    'Av. Salaverry 234', 'Lima', 'Jesus Maria', NULL,
    1, 'CMP-45678',
    @cod_ent, @cod_col, @cod_vet, @msg
);
SELECT @cod_ent, @cod_col, @cod_vet, @msg;

-- Asignar horario al veterinario (colaborador id=5)
-- Horario Aux Groomer - Jornada Completa (id=6) para Martes, Jueves, Sabado
CALL gestionar_asignar_rango(5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 MONTH),
    'ASIGNAR_HORARIO', 6, 1, 'Horario inicial veterinario');

-- SP: registrar_cliente
CALL registrar_cliente(
    1, 'Ana Lucia Romero Paz', 'F', '46123456', 1,
    'ana.romero@gmail.com', '991234567',
    'Av. Peru 123', 'Lima', 'San Miguel', NULL,
    @id_ent, @id_cli, @cod_ent, @cod_cli, @msg
);
SELECT @cod_ent, @cod_cli, @msg;

CALL registrar_cliente(
    1, 'Roberto Silva Campos', 'M', '46234567', 1,
    'roberto.silva@gmail.com', '992345678',
    'Jr. Huallaga 456', 'Lima', 'Cercado de Lima', NULL,
    @id_ent, @id_cli, @cod_ent, @cod_cli, @msg
);
SELECT @cod_ent, @cod_cli, @msg;

CALL registrar_cliente(
    1, 'Maria Fernanda Diaz Torres', 'F', '46345678', 1,
    'maria.diaz@gmail.com', '993456789',
    'Calle Las Flores 789', 'Lima', 'Miraflores', NULL,
    @id_ent, @id_cli, @cod_ent, @cod_cli, @msg
);
SELECT @cod_ent, @cod_cli, @msg;

CALL registrar_cliente(
    1, 'Jorge Luis Vargas Rios', 'M', '46456789', 1,
    'jorge.vargas@gmail.com', '994567890',
    'Av. Arequipa 1234', 'Lima', 'San Isidro', NULL,
    @id_ent, @id_cli, @cod_ent, @cod_cli, @msg
);
SELECT @cod_ent, @cod_cli, @msg;

CALL registrar_cliente(
    1, 'Patricia Gutierrez Mora', 'F', '46567890', 1,
    'patricia.gutierrez@gmail.com', '995678901',
    'Jr. Tacna 567', 'Lima', 'Breña', NULL,
    @id_ent, @id_cli, @cod_ent, @cod_cli, @msg
);
SELECT @cod_ent, @cod_cli, @msg;

-- SP: registrar_mascota
CALL registrar_mascota(
    'Rocky', 'M', 1, 24, 1,
    '2020-05-10', 'Dorado', 4, 3,
    0, NULL, 28.50, 1, 0, 1, 0, NULL,
    @id_masc, @cod_masc, @msg
);
SELECT @id_masc, @cod_masc, @msg;

CALL registrar_mascota(
    'Luna', 'F', 2, 33, 1,
    '2021-08-15', 'Negro', 2, 2,
    1, 'Polen', 12.00, 0, 0, 0, 0, NULL,
    @id_masc, @cod_masc, @msg
);
SELECT @id_masc, @cod_masc, @msg;

CALL registrar_mascota(
    'Max', 'M', 3, 1, 1,
    '2019-03-20', 'Blanco y negro', 5, 3,
    0, NULL, 35.00, 1, 1, 1, 1, NULL,
    @id_masc, @cod_masc, @msg
);
SELECT @id_masc, @cod_masc, @msg;

CALL registrar_mascota(
    'Mishi', 'F', 4, 51, 2,
    '2022-11-05', 'Crema', 1, 1,
    0, NULL, 3.20, 0, 0, 0, 0, NULL,
    @id_masc, @cod_masc, @msg
);
SELECT @id_masc, @cod_masc, @msg;

CALL registrar_mascota(
    'Toby', 'M', 5, 40, 1,
    '2020-07-30', 'Beige', 2, 3,
    1, 'Pollo', 8.50, 1, 0, 0, 0, NULL,
    @id_masc, @cod_masc, @msg
);
SELECT @id_masc, @cod_masc, @msg;

-- SP: gestionar_agenda
-- p_accion: CREAR
CALL gestionar_agenda(
    'CREAR', NULL, 1, 1, 2,
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00:00',
    60, 1, 0.00, 'Primera consulta general',
    @id_ag, @cod_ag, @msg
);
SELECT @id_ag, @cod_ag, @msg;

CALL gestionar_agenda(
    'CREAR', NULL, 2, 2, 1,
    DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00',
    90, 1, 50.00, 'Bano y corte',
    @id_ag, @cod_ag, @msg
);
SELECT @id_ag, @cod_ag, @msg;

CALL gestionar_agenda(
    'CREAR', NULL, 3, 3, 2,
    DATE_ADD(CURDATE(), INTERVAL 4 DAY), '11:00:00',
    45, 2, 0.00, 'Vacunacion anual',
    @id_ag, @cod_ag, @msg
);
SELECT @id_ag, @cod_ag, @msg;

CALL gestionar_agenda(
    'CREAR', NULL, 4, 4, 3,
    DATE_ADD(CURDATE(), INTERVAL 5 DAY), '14:00:00',
    30, 1, 0.00, 'Control de peso',
    @id_ag, @cod_ag, @msg
);
SELECT @id_ag, @cod_ag, @msg;

CALL gestionar_agenda(
    'CREAR', NULL, 5, 5, 2,
    DATE_ADD(CURDATE(), INTERVAL 6 DAY), '15:00:00',
    60, 1, 0.00, 'Desparasitacion',
    @id_ag, @cod_ag, @msg
);
SELECT @id_ag, @cod_ag, @msg;

-- SP: gestionar_ingreso_servicio
-- p_accion: CREAR
-- Cita 1 → consulta general con veterinario (id=1)
CALL gestionar_ingreso_servicio(
    'CREAR', NULL, 1, 1, NULL, 1,
    1, 60, 80.00, 'Consulta general',
    @id_ing, @cod_ing, @total, @msg
);
SELECT @cod_ing, @total, @msg;

-- Cita 2 → bano y corte con colaborador groomer (id=4)
CALL gestionar_ingreso_servicio(
    'CREAR', NULL, 2, 3, 4, NULL,
    1, 90, 60.00, 'Bano completo con corte',
    @id_ing, @cod_ing, @total, @msg
);
SELECT @cod_ing, @total, @msg;

-- Cita 3 → vacunacion con veterinario (id=1)
CALL gestionar_ingreso_servicio(
    'CREAR', NULL, 3, 1, NULL, 1,
    1, 45, 50.00, 'Vacuna rabia',
    @id_ing, @cod_ing, @total, @msg
);
SELECT @cod_ing, @total, @msg;

-- Cita 4 → control con veterinario (id=1)
CALL gestionar_ingreso_servicio(
    'CREAR', NULL, 4, 1, NULL, 1,
    1, 30, 40.00, 'Control de peso y revision',
    @id_ing, @cod_ing, @total, @msg
);
SELECT @cod_ing, @total, @msg;

-- Cita 5 → desparasitacion con veterinario (id=1)
CALL gestionar_ingreso_servicio(
    'CREAR', NULL, 5, 2, NULL, 1,
    1, 30, 35.00, 'Desparasitacion interna',
    @id_ing, @cod_ing, @total, @msg
);
SELECT @cod_ing, @total, @msg;

-- SP: gestionar_pago_agenda
-- id_medio_pago: 1=EFECTIVO, 5=YAPE
-- id_usuario: 3=caja_milo (quien registra el pago)
CALL gestionar_pago_agenda(
    'CREAR', NULL, 1, 1, 3, 80.00, 'Pago completo efectivo',
    @id_pago, @cod_pago, @total_ab, @saldo, @msg
);
SELECT @cod_pago, @total_ab, @saldo, @msg;

CALL gestionar_pago_agenda(
    'CREAR', NULL, 2, 5, 3, 30.00, 'Abono inicial Yape',
    @id_pago, @cod_pago, @total_ab, @saldo, @msg
);
SELECT @cod_pago, @total_ab, @saldo, @msg;

CALL gestionar_pago_agenda(
    'CREAR', NULL, 3, 1, 3, 50.00, 'Pago completo efectivo',
    @id_pago, @cod_pago, @total_ab, @saldo, @msg
);
SELECT @cod_pago, @total_ab, @saldo, @msg;

CALL gestionar_pago_agenda(
    'CREAR', NULL, 4, 5, 3, 20.00, 'Abono Yape',
    @id_pago, @cod_pago, @total_ab, @saldo, @msg
);
SELECT @cod_pago, @total_ab, @saldo, @msg;

CALL gestionar_pago_agenda(
    'CREAR', NULL, 5, 1, 3, 35.00, 'Pago completo efectivo',
    @id_pago, @cod_pago, @total_ab, @saldo, @msg
);
SELECT @cod_pago, @total_ab, @saldo, @msg;

-- INSERT RECORDATORIOS directo — no hay SP para esto
INSERT INTO recordatorios_agenda
    (codigo, id_agenda, id_tipo_recordatorio, fecha_recordatorio, hora, mensaje, id_canal_comunicacion, enviado)
VALUES
    ('REC000001', 1, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00',
     'Recordatorio: Rocky tiene cita manana a las 9:00am', 1, 0),
    ('REC000002', 2, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '08:00:00',
     'Recordatorio: Luna tiene bano y corte manana a las 10:00am', 1, 0),
    ('REC000003', 3, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '08:00:00',
     'Recordatorio: Max tiene vacunacion manana a las 11:00am', 1, 0),
    ('REC000004', 4, 4, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '08:00:00',
     'Recordatorio: Mishi tiene control manana a las 2:00pm', 1, 0),
    ('REC000005', 5, 4, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '08:00:00',
     'Recordatorio: Toby tiene desparasitacion manana a las 3:00pm', 1, 0);

-- SP: gestionar_asistencia
-- Registramos jornada completa del colaborador 1 hoy

CALL gestionar_asistencia(1, CURDATE(), '09:05:00', 'ENTRADA',  @msg, @ok, @tard, @estado);
SELECT @msg, @ok, @tard, @estado;

CALL gestionar_asistencia(1, CURDATE(), '13:00:00', 'LUNCH_IN', @msg, @ok, @tard, @estado);
SELECT @msg, @ok, @tard, @estado;

CALL gestionar_asistencia(1, CURDATE(), '14:00:00', 'LUNCH_OUT', @msg, @ok, @tard, @estado);
SELECT @msg, @ok, @tard, @estado;

CALL gestionar_asistencia(1, CURDATE(), '18:00:00', 'SALIDA',   @msg, @ok, @tard, @estado);
SELECT @msg, @ok, @tard, @estado;

-- SP: registrar_cita_atendida  CITA ATENDIDA + HISTORIA CLINICA
-- Atendemos la cita 1 (Rocky - consulta general)
CALL registrar_cita_atendida(
    1,          -- id_agenda
    1,          -- id_veterinario
    NULL,       -- id_colaborador
    'MEDICA',   -- tipo_visita
    'Revision general anual',               -- motivo_consulta
    'Paciente activo sin antecedentes',     -- anamnesis
    'Sin hallazgos relevantes',             -- examen_fisico
    'FC:80 FR:20 TLL:2seg',                -- signos_vitales
    28.50,      -- peso_kg
    38.50,      -- temperatura_c
    'Paciente sano',                        -- diagnostico
    'Vitaminas y control en 6 meses',       -- tratamiento
    DATE_ADD(CURDATE(), INTERVAL 6 MONTH), -- proximo_control
    NULL, NULL, NULL,                       -- datos esteticos
    NULL, NULL, NULL,                       -- datos hospedaje
    'Primera visita del año',               -- observaciones
    @id_reg, @cod_reg, @msg
);
SELECT @id_reg, @cod_reg, @msg;

-- SP: subir_archivo_clinico
CALL subir_archivo_clinico(
    1, 4,
    'fotografia_rocky_consulta_general',
    'jpg',
    'Foto de revision general Rocky',
    @id_arch, @cod_arch, @msg
);
SELECT @id_arch, @cod_arch, @msg;

-- SPs: registrar_vacuna_mascota
CALL registrar_vacuna_mascota(
    1, 1, 3,
    '1ml', CURDATE(), 1,
    1, NULL, 'Vacuna rabia anual',
    @msg
);
SELECT @msg;

-- SPs: registrar_medicamento_mascota
CALL registrar_medicamento_mascota(
    1, 1, 1,
    '250mg cada 12h', CURDATE(),
    NULL, 1,
    'Preventivo post consulta',
    @msg
);
SELECT @msg;

SELECT 'empresa'                    AS tabla, COUNT(*) AS registros FROM empresa
UNION ALL
SELECT 'usuarios',                            COUNT(*) FROM usuarios
UNION ALL
SELECT 'roles',                               COUNT(*) FROM roles
UNION ALL
SELECT 'usuarios_roles',                      COUNT(*) FROM usuarios_roles
UNION ALL
SELECT 'entidades',                           COUNT(*) FROM entidades
UNION ALL
SELECT 'colaboradores',                       COUNT(*) FROM colaboradores
UNION ALL
SELECT 'veterinarios',                        COUNT(*) FROM veterinarios
UNION ALL
SELECT 'clientes',                            COUNT(*) FROM clientes
UNION ALL
SELECT 'proveedores',                         COUNT(*) FROM proveedores
UNION ALL
SELECT 'asignacion_horarios',                 COUNT(*) FROM asignacion_horarios
UNION ALL
SELECT 'asignacion_horarios_detalle',         COUNT(*) FROM asignacion_horarios_detalle
UNION ALL
SELECT 'registro_asistencias',                COUNT(*) FROM registro_asistencias
UNION ALL
SELECT 'mascotas',                            COUNT(*) FROM mascotas
UNION ALL
SELECT 'vacunas_mascota',                     COUNT(*) FROM vacunas_mascota
UNION ALL
SELECT 'medicamentos_mascota',                COUNT(*) FROM medicamentos_mascota
UNION ALL
SELECT 'agenda',                              COUNT(*) FROM agenda
UNION ALL
SELECT 'ingresos_servicios',                  COUNT(*) FROM ingresos_servicios
UNION ALL
SELECT 'agenda_pagos',                        COUNT(*) FROM agenda_pagos
UNION ALL
SELECT 'recordatorios_agenda',                COUNT(*) FROM recordatorios_agenda
UNION ALL
SELECT 'historia_clinica',                    COUNT(*) FROM historia_clinica
UNION ALL
SELECT 'historia_clinica_registros',          COUNT(*) FROM historia_clinica_registros
UNION ALL
SELECT 'historia_clinica_archivos',           COUNT(*) FROM historia_clinica_archivos;