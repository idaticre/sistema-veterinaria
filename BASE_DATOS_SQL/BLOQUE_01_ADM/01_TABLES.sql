-- =========================================================
-- BASE DE DATOS: vet_manada_woof
-- Este script contiene la definición inicial de la base de datos
-- =========================================================

-- ========================================
-- 0. Creación de la Base de Datos
-- ========================================
DROP DATABASE IF EXISTS vet_manada_woof;
CREATE DATABASE vet_manada_woof;
USE vet_manada_woof;

-- ========================================
-- BLOQUE 01: Administracion
-- gestiona la información de entidades, colaboradores, entre otros
-- ========================================

-- ========================================
-- TABLA: empresa
-- Almacena la información de la veterinaria que opera el sistema.
-- ========================================
CREATE TABLE IF NOT EXISTS empresa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    razon_social VARCHAR(128) NOT NULL,
    ruc CHAR(11) NOT NULL UNIQUE,
    direccion VARCHAR(256),
    ciudad VARCHAR(64),
    distrito VARCHAR(64),
    telefono VARCHAR(15),
    correo VARCHAR(64),
    representante VARCHAR(64),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logo_empresa VARCHAR(255)  		-- Guardar solo la ruta al archivo
);

-- ========================================
-- EMPRESA 
-- Al ser información real se hace el insert y posterior hay un sp para visualizar y actualizar
-- ========================================
INSERT INTO empresa(razon_social, ruc, direccion, ciudad, distrito, telefono, correo, representante) VALUES 
('Manada Woof.S.A.C.S ', 
'20613366998', 
'Jiron Arequipa 238', 
'Lima', 
'Magdalena del Mar', 
'917 233 145', 
'manadawoof.vet@gmail.com', 
'Sandra Alexis Laguna De La Rosa'
);

-- ========================================
-- TABLA: tipo_documento
-- Contiene los diferentes tipos de documentos permitidos para identificar entidades.
-- Ejemplo: “DNI”, “RUC”, “PASAPORTE”
	-- ========================================
CREATE TABLE IF NOT EXISTS tipo_documento (
		id INT PRIMARY KEY AUTO_INCREMENT,
		descripcion VARCHAR(32) NOT NULL,
		activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE tipo_documento MODIFY descripcion VARCHAR(32) NOT NULL COLLATE utf8mb4_general_ci;

INSERT INTO tipo_documento (descripcion) VALUES 
('DNI'), 
('RUC'), 
('CARNET EXT.'), 
('P. NAC.'), 
('PASAPORTE'), 
('OTROS');

-- ========================================
-- TABLA: tipo_persona_juridica
-- Clasifica la naturaleza legal de la entidad: NATURAL o JURÍDICA.
-- ========================================
CREATE TABLE tipo_persona_juridica (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(64),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE tipo_persona_juridica MODIFY nombre VARCHAR(32) NOT NULL COLLATE utf8mb4_general_ci;

CREATE UNIQUE INDEX idx_tipo_perjur_nom ON tipo_persona_juridica(nombre);

INSERT INTO tipo_persona_juridica (nombre, descripcion) VALUES
('NATURAL', 'Persona natural que representa una entidad individual'),
('JURIDICA', 'Entidad jurídica con existencia legal y RUC propio');

-- ========================================
-- TABLA: usuarios
-- Guarda las credenciales de acceso al sistema para cada persona autorizada.
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_baja DATETIME NULL
);

INSERT INTO usuarios (username, password_hash) VALUES
('admin_woof',  'admin123'),   -- Administrador General (pwd: admin123)
('admin_g2',    'admin234'),   -- Administrador G2     (pwd: admin234)
('caja_milo',   'caja123'),   -- Auxiliar Caja         (pwd: caja123)
('gromer_luna', 'luna123');   -- Auxiliar Gromers      (pwd: luna123)

-- ========================================
-- TABLA: roles
-- Define los roles asignables a usuarios del sistema.
-- Ejemplo: “ADMIN”, “VETERINARIO”
-- ========================================
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128) DEFAULT NULL
);

INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN GENERAL 1', 'Tiene control total del sistema'),
('ADMIN GENERAL 2', 'Tiene control total del sistema'),
('AUX CAJA', 'Gestiona pagos y facturación'),
('AUX GROMERS', 'Atiende grooming y soporte de servicios'),
('AUX BAÑADOR', 'Atiende BAÑOS y soporte de servicios');

-- ========================================
-- TABLA: usuarios_roles
-- Relaciona usuarios con uno o varios roles.
-- Ejemplo: usuario_id = 1, rol_id = 2 (Usuario 1 tiene el rol 2 - VETERINARIO)
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios_roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_rol INT,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_usuario_rol (id_usuario, id_rol)    
);
ALTER TABLE usuarios_roles
    ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON DELETE CASCADE,
    ADD CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES roles(id)
    ON DELETE CASCADE;

-- Índice para búsquedas rápidas de roles asignados a un usuario específico.
CREATE INDEX idx_usuarios_roles_usuario ON usuarios_roles(id_usuario);

-- ========================================
-- TABLA: entidades
-- Centraliza los datos generales de personas y empresas del sistema.
-- ========================================
CREATE TABLE IF NOT EXISTS entidades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
	id_tipo_persona_juridica INT NOT NULL,									-- natural o juridica
    nombre VARCHAR(128) NOT NULL,
    sexo VARCHAR(1),
    documento VARCHAR(20) NOT NULL UNIQUE,
    id_tipo_documento INT NOT NULL,
	telefono VARCHAR(15) CHECK (telefono REGEXP '^[0-9+ ]{6,15}$'),
	correo VARCHAR(64) UNIQUE,												-- la verificacion queda para el frontend
    direccion VARCHAR(128),
    ciudad VARCHAR(64) NOT NULL,
    distrito VARCHAR(64) NOT NULL,
    representante VARCHAR(64) NULL,											-- Deberá ser en tal caso el mismo nombre
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE entidades
    ADD CONSTRAINT fk_entidad_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id),
    ADD CONSTRAINT fk_entidad_persona_juridica FOREIGN KEY (id_tipo_persona_juridica) REFERENCES tipo_persona_juridica(id)
    ON DELETE RESTRICT;

-- Índice para acelerar las búsquedas de entidades por documento 
CREATE UNIQUE INDEX idx_entidades_documento ON entidades(documento);

-- Índice para acelerar la validación y búsqueda por correo 
CREATE UNIQUE INDEX idx_entidades_correo ON entidades(correo);

-- Índice para consultas filtradas por tipo de documento (DNI, RUC...).
CREATE INDEX idx_entidades_tipo_documento ON entidades(id_tipo_documento);

-- ========================================
-- TABLA: colaboradores Registra a los trabajadores vinculados a la veterinaria
-- ========================================
CREATE TABLE IF NOT EXISTS colaboradores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_entidad BIGINT NOT NULL UNIQUE,
    fecha_ingreso DATE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    foto VARCHAR(128)
);
ALTER TABLE colaboradores
    ADD CONSTRAINT fk_colaborador_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id)
    ON DELETE RESTRICT,
    ADD CONSTRAINT fk_colaborador_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON DELETE RESTRICT;
    
-- Índice para acelerar las consultas que vinculan colaboradores con sus entidades.
CREATE INDEX idx_colaboradores_entidad ON colaboradores(id_entidad);

-- Índice para búsquedas cruzadas entre colaborador y usuario (útil para sesiones o perfil).
CREATE INDEX idx_colaboradores_usuario ON colaboradores(id_usuario);

-- Índice para mejorar filtros por estado en colaboradores.
CREATE INDEX idx_colaboradores_activo ON colaboradores(activo);

-- ========================================
-- TABLA: proveedores
-- ========================================
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_entidad BIGINT NOT NULL UNIQUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE proveedores 
    ADD CONSTRAINT fk_proveedor_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id)
    ON DELETE RESTRICT;

-- Índice para consultas rápidas que relacionan proveedores con sus entidades.
CREATE INDEX idx_proveedores_entidad ON proveedores(id_entidad);

-- Índice para mejorar filtros por estado en proveedores.
CREATE INDEX idx_proveedores_activo ON proveedores(activo);

-- ========================================
-- TABLA: clientes
-- ========================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_entidad BIGINT NOT NULL UNIQUE,    
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE clientes 
    ADD CONSTRAINT fk_cliente_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id)
    ON DELETE RESTRICT;

-- Índice para consultas rápidas que relacionan clientes con sus entidades.
CREATE INDEX idx_clientes_entidad ON clientes(id_entidad);

-- Índice para mejorar filtros por estado en clientes.
CREATE INDEX idx_clientes_activo ON clientes(activo);

-- ========================================
-- TABLA: especialidades
-- Catálogo de especialidades médicas veterinarias.
-- ========================================
CREATE TABLE IF NOT EXISTS especialidades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

ALTER TABLE especialidades MODIFY nombre VARCHAR(64) NOT NULL COLLATE utf8mb4_general_ci;

CREATE UNIQUE INDEX idx_especialidades_nombre_ci ON especialidades(nombre);
-- ========================================
-- ESPECIALIDADES VETERINARIAS
-- ========================================
INSERT INTO especialidades (nombre) VALUES
('MEDICINA GENERAL'), ('CIRUGÍA'), ('DERMATOLOGÍA'), 
('OFTALMOLOGÍA'), ('TRAUMATOLOGÍA'),('CARDIOLOGÍA'), 
('ODONTOLOGÍA VETERINARIA'), ('ONCOLOGÍA'), ('NEUROLOGÍA'),
('ANESTESIOLOGÍA'), ('EMERGENCIAS Y CUIDADOS CRÍTICOS'), 
('REHABILITACIÓN Y FISIOTERAPIA'),('ETOLOGÍA Y COMPORTAMIENTO ANIMAL');

-- ========================================
-- TABLA: veterinarios
-- ========================================
CREATE TABLE IF NOT EXISTS veterinarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_colaborador BIGINT NOT NULL UNIQUE,
    id_especialidad BIGINT NOT NULL,
    cmp VARCHAR(32) UNIQUE NULL,
    experiencia_meses INT CHECK (experiencia_meses >= 0),
    observaciones VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE veterinarios
    ADD CONSTRAINT fk_vet_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_vet_especialidad FOREIGN KEY (id_especialidad) REFERENCES especialidades(id)
    ON DELETE RESTRICT;

-- Índice para búsquedas rápidas de veterinarios asociados a un colaborador.
CREATE INDEX idx_veterinarios_colaborador ON veterinarios(id_colaborador);

-- Índice para facilitar consultas por especialidad (ej: veterinarios de dermatología).
CREATE INDEX idx_veterinarios_especialidad ON veterinarios(id_especialidad);

-- ========================================
-- TABLA: dias_semana en orden lógico (1=Lunes...7=Domingo)
-- ========================================
CREATE TABLE IF NOT EXISTS dias_semana (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) UNIQUE NOT NULL,
    orden INT NOT NULL UNIQUE
);

INSERT INTO dias_semana (nombre, orden) VALUES
('LUNES', 1),
('MARTES', 2),
('MIERCOLES', 3),
('JUEVES', 4),
('VIERNES', 5),
('SABADO', 6),
('DOMINGO', 7)
ON DUPLICATE KEY UPDATE nombre=nombre;

-- ========================================
-- TABLA: horarios_base de horarios laborales.
-- Cada horario puede tener tolerancia de entrada y tiempo estándar del almuerzo.
-- ========================================
CREATE TABLE IF NOT EXISTS horarios_base (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    hora_inicio TIME NULL,              -- NULL si es descanso
    hora_fin TIME NULL,                 -- NULL si es descanso
    minutos_tolerancia_entrada INT DEFAULT 0,
    minutos_lunch INT NOT NULL DEFAULT 60,
    overnight TINYINT NOT NULL DEFAULT 0 CHECK (overnight IN (0,1)),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

INSERT INTO horarios_base (nombre, descripcion, hora_inicio, hora_fin, minutos_tolerancia_entrada)
VALUES 
-- HORARIOS ADMINISTRADORES
('Admin - Mañana M-J-S', 'Martes, jueves y sábado de 9:00 a 13:00', '09:00:00', '13:00:00', 15),
('Admin - Tarde M-J-S',  'Martes, jueves y sábado de 14:00 a 18:00', '14:00:00', '18:00:00', 15),
('Admin - Mañana M-V-D', 'Miércoles, viernes y domingo de 9:00 a 13:00', '09:00:00', '13:00:00', 15),
('Admin - Tarde M-V-D',  'Miércoles, viernes y domingo de 14:00 a 18:00', '14:00:00', '18:00:00', 15),
-- HORARIO AUXILIAR
('Aux Caja- Jornada Completa', 'Martes a domingo de 8:30 a 18:30', '08:30:00', '18:30:00', 15),
-- HORARIO GROOMER
('Aux Groomer - Jornada Completa', 'Martes, jueves y sábado de 9:30 a 18:30', '09:30:00', '18:30:00', 15),
-- HORARIO BAÑADOR
('Aux Bañador - Jornada Completa', 'Miércoles, viernes y domingo de 9:00 a 18:00', '09:00:00', '18:00:00', 15),
-- DESCANSO LUNES
('Descanso', 'Día libre (lunes)', NULL, NULL, 0)
ON DUPLICATE KEY UPDATE nombre = nombre;

ALTER TABLE horarios_base
    ADD CONSTRAINT chk_hora_fin CHECK (
        (overnight = 0 AND (hora_inicio IS NULL OR hora_fin IS NULL OR hora_fin >= hora_inicio))
        OR 
        (overnight = 1 AND hora_fin < hora_inicio)
    );

-- ========================================
-- TABLA: horarios_base_roles
-- Relaciona cada rol con un horario base para un día de la semana.
-- Permite asignaciones predeterminadas por rol y día.
-- ========================================
CREATE TABLE IF NOT EXISTS horarios_base_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_rol INT NOT NULL,
    id_horario_base INT NOT NULL,
    id_dia_semana INT NOT NULL, -- 1=LUNES ... 7=DOMINGO
    UNIQUE KEY uq_rol_horario_dia (id_rol, id_horario_base, id_dia_semana)
);

ALTER TABLE horarios_base_roles
    ADD CONSTRAINT fk_hbr_rol FOREIGN KEY (id_rol) REFERENCES roles(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hbr_horario FOREIGN KEY (id_horario_base) REFERENCES horarios_base(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hbr_dia FOREIGN KEY (id_dia_semana) REFERENCES dias_semana(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índices
CREATE INDEX idx_hbr_rol ON horarios_base_roles(id_rol);
CREATE INDEX idx_hbr_horario ON horarios_base_roles(id_horario_base);
CREATE INDEX idx_hbr_dia ON horarios_base_roles(id_dia_semana);

-- ADMIN GENERAL 1
INSERT INTO horarios_base_roles (id_rol, id_horario_base, id_dia_semana) VALUES
(1, 1, 2),(1, 2, 2),(1, 1, 4),(1, 2, 4),(1, 1, 6),(1, 2, 6);

-- ADMIN GENERAL 2
INSERT INTO horarios_base_roles (id_rol, id_horario_base, id_dia_semana) VALUES
(2, 3, 3),(2, 4, 3),(2, 3, 5),(2, 4, 5),(2, 3, 7),(2, 4, 7);

-- AUX CAJA
INSERT INTO horarios_base_roles (id_rol, id_horario_base, id_dia_semana) VALUES
(3, 5, 2),(3, 5, 3),(3, 5, 4),(3, 5, 5),(3, 5, 6),(3, 5, 7);

-- AUX GROMERS
INSERT INTO horarios_base_roles (id_rol, id_horario_base, id_dia_semana) VALUES
(4, 6, 2),(4, 6, 4),(4, 6, 6);

-- AUX BAÑADOR
INSERT INTO horarios_base_roles (id_rol, id_horario_base, id_dia_semana) VALUES
(5, 7, 3),(5, 7, 5),(5, 7, 7);

-- ========================================
-- TABLA: asignacion_horarios
-- Asigna a cada colaborador un horario base según el día de la semana.
-- Permite definir períodos de vigencia para cambios históricos.
-- ========================================
CREATE TABLE IF NOT EXISTS asignacion_horarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador BIGINT NOT NULL,
    id_horario_base INT NOT NULL,
    id_dia_semana INT NOT NULL,
    fecha_inicio_vigencia DATE NOT NULL,
    fecha_fin_vigencia DATE NULL,
    motivo_cambio VARCHAR(255) NULL,
    fecha_asignacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    CONSTRAINT uq_colab_dia UNIQUE (id_colaborador, id_dia_semana, fecha_inicio_vigencia, activo)
);

ALTER TABLE asignacion_horarios
    ADD CONSTRAINT chk_vigencia CHECK (
        fecha_fin_vigencia IS NULL OR fecha_fin_vigencia >= fecha_inicio_vigencia
    ),
    ADD CONSTRAINT fk_asignacion_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_asignacion_horario FOREIGN KEY (id_horario_base) REFERENCES horarios_base(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_asignacion_dia FOREIGN KEY (id_dia_semana) REFERENCES dias_semana(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índices
CREATE INDEX idx_asignacion_colaborador ON asignacion_horarios(id_colaborador);
CREATE INDEX idx_asignacion_dia ON asignacion_horarios(id_dia_semana);
CREATE INDEX idx_asignacion_vigencia ON asignacion_horarios(fecha_inicio_vigencia, fecha_fin_vigencia);

-- ========================================
-- TABLA: asignacion_horarios_detalle
-- Detalla el horario real por día de cada asignación.
-- Permite excepciones por día y soporta lógica de asistencia y agenda.
-- ========================================
CREATE TABLE IF NOT EXISTS asignacion_horarios_detalle (
    id_detalle BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_asignacion BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NULL,      --  NULL para soportar descansos
    hora_fin TIME NULL,         --  NULL para soportar descansos
    es_excepcion TINYINT DEFAULT 0, 
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE asignacion_horarios_detalle
    ADD CONSTRAINT fk_asig_det_asignacion FOREIGN KEY (id_asignacion) REFERENCES asignacion_horarios(id)
				ON DELETE CASCADE;

ALTER TABLE asignacion_horarios_detalle
    ADD CONSTRAINT uq_asig_fecha UNIQUE (id_asignacion, fecha);

-- Índices optimizados
CREATE INDEX idx_asig_det_asignacion_fecha ON asignacion_horarios_detalle(id_asignacion, fecha);
CREATE INDEX idx_asig_det_fecha ON asignacion_horarios_detalle(fecha);

-- ========================================
-- TABLA: estado_asistencia
-- Catálogo maestro de estados válidos para cada registro de asistencia.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_asistencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

INSERT INTO estado_asistencia (nombre, descripcion) VALUES
('PRESENTE', 'Asistencia completa y puntual'),
('TARDANZA', 'Llegó después de la hora de entrada'),
('SALIDA_TEMPRANA', 'Salió antes de completar horario'),
('AUSENTE_INJUSTIFICADO', 'No asistió sin justificación'),
('PERMISO', 'Permiso autorizado con goce de haber'),
('LICENCIA_MEDICA', 'Descanso médico certificado'),
('VACACIONES', 'Día de vacaciones programado'),
('FERIADO', 'Día feriado no laborable'),
('SUSPENSION', 'Suspensión disciplinaria'),
('DESCANSO_SEMANAL', 'Día de descanso según cronograma'),
('COMPLETADO', 'Jornada completa finalizada')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- ========================================
-- TABLA: registro_asistencias
-- Controla los registros diarios de asistencia del personal.
-- ========================================
CREATE TABLE IF NOT EXISTS registro_asistencias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador BIGINT NOT NULL,
    id_horario_base INT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME NULL,
    hora_lunch_inicio TIME NULL,
    hora_lunch_fin TIME NULL,
    hora_salida TIME NULL,
    minutos_trabajados INT NULL,
    minutos_lunch INT NULL,
    tardanza_minutos INT NULL,
    id_estado_asistencia INT NOT NULL,
    observaciones TEXT,
    registro_origen VARCHAR(32) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT NULL,
    updated_by BIGINT NULL
);

ALTER TABLE registro_asistencias
    ADD CONSTRAINT chk_hora_orden CHECK (
       (hora_entrada IS NULL OR hora_salida IS NULL OR hora_entrada <= hora_salida)
        AND (hora_lunch_inicio IS NULL OR hora_entrada <= hora_lunch_inicio)
        AND (hora_lunch_fin IS NULL OR hora_lunch_inicio <= hora_lunch_fin)
        AND (hora_salida IS NULL OR (hora_lunch_fin IS NULL OR hora_lunch_fin <= hora_salida))
    );

ALTER TABLE registro_asistencias
    ADD CONSTRAINT uq_asistencia_colab_fecha UNIQUE (id_colaborador, fecha);

ALTER TABLE registro_asistencias
    ADD CONSTRAINT fk_asistencia_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE registro_asistencias
    ADD CONSTRAINT fk_asistencia_horario FOREIGN KEY (id_horario_base) REFERENCES horarios_base(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE registro_asistencias
    ADD CONSTRAINT fk_registro_asistencia_estado FOREIGN KEY (id_estado_asistencia) REFERENCES estado_asistencia (id)
        ON UPDATE CASCADE ON DELETE RESTRICT;

-- Índices
CREATE INDEX idx_asistencia_colaborador ON registro_asistencias(id_colaborador);
CREATE INDEX idx_asistencia_fecha ON registro_asistencias(fecha);