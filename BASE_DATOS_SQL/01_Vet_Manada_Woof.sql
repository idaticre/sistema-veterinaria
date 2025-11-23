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

-- =============================================================================================================================================

-- ========================================
-- BLOQUE 02: mascotas y salud veterinaria
-- gestiona la información de mascotas, su clasificación,
-- vacunas, medicamentos e historial clínico.
-- ========================================

-- ========================================
-- TABLA: especies
-- Define las especies de mascotas atendidas por la veterinaria.
-- Ejemplo: PERRO, GATO, CONEJO.
-- ========================================
CREATE TABLE IF NOT EXISTS especies (
	id INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

INSERT INTO especies (nombre) VALUES 
('CANINO'), ('FELINO'), ('CONEJO');

-- ========================================
-- TABLA: razas
-- Lista las razas asociadas a una especie específica.
-- Ejemplo: LABRADOR (especie PERRO), SIAMÉS (especie GATO).
-- ========================================
CREATE TABLE IF NOT EXISTS razas (
	id INT PRIMARY KEY AUTO_INCREMENT,
	id_especie INT,
	nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE razas 
	ADD CONSTRAINT fk_raza_especie FOREIGN KEY (id_especie) REFERENCES especies(id)
	ON DELETE RESTRICT
	ON UPDATE CASCADE;

-- Índice para búsquedas de razas según la especie seleccionada (útil en formularios o filtros).
CREATE INDEX idx_razas_especie ON razas(id_especie);

-- CANINO (id_especie = 1)
INSERT INTO razas (id_especie, nombre) VALUES
(1, 'AKITA INU'), (1, 'AMERICAN STAFFORDSHIRE TERRIER'), (1, 'BASSET HOUND'), (1, 'BEAGLE'),
(1, 'BICHÓN FRISÉ'), (1, 'BORDER COLLIE'), (1, 'BORDER TERRIER'), (1, 'BOSTON TERRIER'), (1, 'BOXER'),
(1, 'BULL TERRIER'), (1, 'BULLDOG FRANCÉS'), (1, 'BULLDOG INGLÉS'), (1, 'CANICHE (POODLE)'), (1, 'CANE CORSO'),
(1, 'CAVALIER KING CHARLES SPANIEL'), (1, 'CHIHUAHUA'), (1, 'CHOW CHOW'), (1, 'COCKER SPANIEL'), (1, 'DÁLMATA'),
(1, 'DOBERMAN'), (1, 'DOGO ARGENTINO'), (1, 'FOX TERRIER'), (1, 'GALGO'), (1, 'GOLDEN RETRIEVER'),
(1, 'GREAT DANE (GRAN DANÉS)'), (1, 'HUSKY SIBERIANO'), (1, 'JACK RUSSELL TERRIER'), (1, 'LABRADOODLE'), (1, 'LABRADOR RETRIEVER'),
(1, 'LHASA APSO'), (1, 'MALAMUTE DE ALASKA'), (1, 'MALTÉS'), (1, 'MESTIZO'), (1, 'PASTOR ALEMAN'), (1, 'PASTOR AUSTRALIANO'),
(1, 'PASTOR BELGA'), (1, 'PEKÍNES'), (1, 'PITBULL TERRIER'), (1, 'POMERANIA'), (1, 'PUG'), (1, 'ROTTWEILER'), (1, 'SAMOYEDO'),
(1, 'SAN BERNARDO'), (1, 'SCHNAUZER MINIATURA'), (1, 'SHIBA INU'), (1, 'SHIH TZU'), (1, 'TERRIER AUSTRALIANO'), (1, 'TERRIER ESCOCÉS'),
(1, 'WEIMARANER'), (1, 'WHIPPET'), (1, 'YORKSHIRE TERRIER');

-- FELINO (id_especie = 2)
INSERT INTO razas (id_especie, nombre) VALUES
(2, 'SIAMÉS'), (2, 'PERSA'), (2, 'MAINE COON'), (2, 'BENGALÍ'), (2, 'BRITISH SHORTHAIR'), (2, 'SPHYNX'),
(2, 'ANGORA TURCO'), (2, 'AZUL RUSO'), (2, 'ABISINIO'), (2, 'SCOTTISH FOLD'); 

-- CONEJO (id_especie = 3)
INSERT INTO razas (id_especie, nombre) VALUES
(3, 'CONEJO ENANO HOLANDÉS'), (3, 'CONEJO ANGORA');

-- ========================================
-- TABLA: tamanos
-- Clasifica a las mascotas según su tamaño corporal.
-- Ejemplo: talla_equivalente = "S", descripcion = "Pequeño".
-- ========================================
CREATE TABLE IF NOT EXISTS tamanos (
	id INT PRIMARY KEY AUTO_INCREMENT,
	tamano VARCHAR(8) NOT NULL UNIQUE,
	descripcion VARCHAR(16) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TAMAÑOS
-- ========================================
INSERT INTO tamanos (tamano, descripcion) VALUES
('XS','MUY PEQUEÑO'), ('S','PEQUEÑO'), ('M','MEDIANO'), ('L','GRANDE'), ('XL','MUY GRANDE');

-- ========================================
-- TABLA: etapas_vida
-- Catálogo de etapas de desarrollo de una mascota.
-- Ejemplo: CACHORRO, ADULTO, SENIOR.
-- ========================================
CREATE TABLE IF NOT EXISTS etapas_vida (
	id INT PRIMARY KEY AUTO_INCREMENT,
	descripcion VARCHAR(16) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

INSERT INTO etapas_vida (descripcion) VALUES
('CACHORRO'), ('JOVEN'), ('ADULTO'), ('SENIOR');

-- ========================================
-- TABLA: vacunas
-- Catálogo de vacunas disponibles según especie.
-- Ejemplo: RABIA (especie PERRO), TRIPLE FELINA (especie GATO).
-- ========================================
CREATE TABLE IF NOT EXISTS vacunas (
	id INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(64) NOT NULL,
	id_especie INT NOT NULL,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE vacunas 
	ADD CONSTRAINT uq_vacuna_nom_esp UNIQUE(nombre, id_especie),
	ADD CONSTRAINT fk_vacuna_especie FOREIGN KEY (id_especie) REFERENCES especies(id)
	ON DELETE RESTRICT
	ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de vacunas por especie
-- (útil para listar vacunas específicas de una especie).
CREATE INDEX idx_vacunas_especie ON vacunas(id_especie);

-- VACUNAS (CANINOS)
INSERT INTO vacunas (nombre, id_especie, descripcion) VALUES
-- Básicas y esenciales
('RABIA', 1, 'Vacuna anual contra el virus de la rabia.'),
('MOQUILLO', 1, 'Protege contra el virus del moquillo canino.'),
('PARVOVIRUS', 1, 'Previene infecciones graves por parvovirus.'),
('HEPATITIS INFECCIOSA CANINA', 1, 'Protección frente al adenovirus tipo 1.'),
('PARAINFLUENZA CANINA', 1, 'Previene infecciones respiratorias por parainfluenza.'),
('TRIPLE CANINA', 1, 'Combinación contra moquillo, hepatitis y parvovirus.'),
('QUÍNTUPLE CANINA', 1, 'Combinación contra 5 virus principales caninos.'),
('SÉXTUPLE CANINA', 1, 'Versión ampliada con protección contra 2 cepas de leptospira.'),
('TOS DE LAS PERRERAS', 1, 'Protección frente a Bordetella bronchiseptica y parainfluenza.'),
('LEPTOSPIROSIS', 1, 'Previene infecciones por bacterias del género Leptospira.'),
('CORONAVIRUS CANINO', 1, 'Prevención de la enteritis por coronavirus canino.'),
('GIARDIASIS', 1, 'Vacuna contra el parásito Giardia intestinalis.'),
('LYME', 1, 'Protege contra la enfermedad de Lyme causada por Borrelia burgdorferi.'),
('INFLUENZA CANINA', 1, 'Vacuna contra los virus H3N2 y H3N8 de la gripe canina.'),
('LEISHMANIASIS', 1, 'Previene la infección por Leishmania infantum.'),
('BABESIOSIS', 1, 'Previene la babesiosis transmitida por garrapatas.'),
('EHRLICHIOSIS', 1, 'Ayuda a prevenir la ehrlichiosis canina, también transmitida por garrapatas.');

-- VACUNAS (FELINOS)
INSERT INTO vacunas (nombre, id_especie, descripcion) VALUES
-- Vacunas básicas (obligatorias o altamente recomendadas)
('RABIA', 2, 'Vacuna obligatoria contra el virus de la rabia felina.'),
('TRIPLE FELINA', 2, 'Combinación contra calicivirus, herpesvirus y panleucopenia felina.'),
('PENTA FELINA', 2, 'Amplía la triple felina con clamidiosis y leucemia felina.'),
('LEUCEMIA FELINA', 2, 'Protege frente al virus de la leucemia felina (FeLV).'),
('PANLEUCOPENIA FELINA', 2, 'Previene infecciones graves por el parvovirus felino.'),
('CALICIVIRUS FELINO', 2, 'Vacuna contra el virus responsable de infecciones respiratorias.'),
('HERPESVIRUS FELINO (RINOTRAQUEÍTIS)', 2, 'Previene infecciones respiratorias por herpesvirus felino tipo 1.'),
('CLAMIDIOSIS FELINA', 2, 'Protección contra Chlamydia felis, causante de conjuntivitis y afecciones respiratorias.'),
('BORDETELLA BRONCHISEPTICA', 2, 'Previene infecciones respiratorias en ambientes con alta densidad felina.'),
('INMUNODEFICIENCIA FELINA (FIV)', 2, 'Vacuna para gatos con riesgo de exposición al virus de la inmunodeficiencia felina.'),
('PERITONITIS INFECCIOSA FELINA (PIF)', 2, 'Vacuna preventiva frente al coronavirus felino que causa la PIF.'),
('GIARDIASIS', 2, 'Previene la infección intestinal causada por Giardia intestinalis.'),
('MICOSIS (DERMATOFITOSIS)', 2, 'Protege frente a hongos dermatofitos como Microsporum canis.');

-- VACUNAS (CONEJOS)
INSERT INTO vacunas (nombre, id_especie, descripcion) VALUES
('MIXOMATOSIS', 3, 'Protege contra el virus de la mixomatosis, transmitido por mosquitos y pulgas.'),
('ENFERMEDAD VÍRICA HEMORRÁGICA (VHD1)', 3, 'Previene la enfermedad hemorrágica clásica causada por el calicivirus tipo 1.'),
('ENFERMEDAD VÍRICA HEMORRÁGICA TIPO 2 (VHD2)', 3, 'Vacuna contra la cepa más reciente y agresiva del virus hemorrágico tipo 2.'),
('TRIPLE CONEJOS (COMBINADA)', 3, 'Vacuna combinada que protege frente a mixomatosis y ambas variantes de VHD.'),
('PASTEURELOSIS', 3, 'Ayuda a prevenir infecciones respiratorias por Pasteurella multocida, comunes en conejos domésticos.');

-- ========================================
-- TABLA: medicamento_tipo
-- Lista los tipos o clasificaciones de medicamentos utilizados.
-- Ejemplo: ANTIBIÓTICO, ANTIINFLAMATORIO.
-- ========================================
CREATE TABLE IF NOT EXISTS medicamento_tipo (
	id INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

INSERT INTO medicamento_tipo (nombre, descripcion) VALUES
('ANTIBIÓTICO', 'Medicamentos que combaten infecciones bacterianas.'),
('ANTIINFLAMATORIO', 'Reducen la inflamación y el dolor en tejidos y articulaciones.'),
('ANALGÉSICO', 'Alivian el dolor leve, moderado o postoperatorio.'),
('ANTIPIRÉTICO', 'Ayudan a reducir la fiebre y malestar asociado.'),
('ANTIFÚNGICO', 'Tratan infecciones causadas por hongos o levaduras.'),
('ANTIPARASITARIO INTERNO', 'Eliminan parásitos intestinales como nematodos o cestodos.'),
('ANTIPARASITARIO EXTERNO', 'Controlan pulgas, garrapatas y ácaros en piel o pelaje.'),
('VITAMÍNICO / SUPLEMENTO', 'Aportan nutrientes o refuerzan el sistema inmunológico.'),
('SEDANTE / ANESTÉSICO', 'Inducen relajación o anestesia para procedimientos clínicos.'),
('CARDIOLÓGICO', 'Medicamentos para control de enfermedades del corazón.'),
('HORMONAL / REPRODUCTIVO', 'Regulan funciones hormonales o reproductivas.'),
('DIGESTIVO / GASTROINTESTINAL', 'Ayudan en trastornos digestivos y protección gástrica.'),
('OTRO', 'Otros tipos de medicamentos no clasificados.');

-- ========================================
-- TABLA: vias_aplicacion
-- Vías por las cuales se aplican medicamentos o vacunas.
-- Ejemplo: ORAL, INYECTABLE, TÓPICA.
-- ========================================
CREATE TABLE IF NOT EXISTS vias_aplicacion (
	id INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(32) UNIQUE NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

INSERT INTO vias_aplicacion (nombre) VALUES 
('ORAL'), ('TÓPICA'), ('SUBCUTÁNEA'), ('INTRAMUSCULAR'), ('INTRAVENOSA'), ('OTRA');

-- ========================================
-- TABLA: medicamentos
-- Catálogo de medicamentos utilizados en tratamientos clínicos.
-- ========================================
CREATE TABLE IF NOT EXISTS medicamentos (
	id INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(64) NOT NULL,
	id_tipo INT NOT NULL,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE medicamentos
	ADD CONSTRAINT uq_medicamento_nom_tip UNIQUE(nombre,id_tipo),
	ADD CONSTRAINT fk_medicamento_tipo FOREIGN KEY (id_tipo) REFERENCES medicamento_tipo(id)
	ON DELETE RESTRICT
	ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de medicamentos por tipo
CREATE INDEX idx_medicamentos_tipo ON medicamentos(id_tipo);

INSERT INTO medicamentos (nombre, id_tipo, descripcion) VALUES
-- ANTIBIÓTICOS (id_tipo = 1)
('AMOXICILINA 500MG', 1, 'Antibiótico de amplio espectro para infecciones bacterianas.'),
('ENROFLOXACINA 5%', 1, 'Antibiótico fluoroquinolona para infecciones respiratorias y urinarias.'),
('CEFTRIAXONA 1G', 1, 'Antibiótico de tercera generación de amplio espectro.'),
('DOXICICLINA 100MG', 1, 'Antibiótico tetraciclina para infecciones respiratorias y cutáneas.'),
('CEFALEXINA 500MG', 1, 'Antibiótico cefalosporínico de uso común en piel y vías respiratorias.'),
('GENTAMICINA 10%', 1, 'Antibiótico aminoglucósido inyectable de amplio espectro.'),
('CLAVAMOX 250MG', 1, 'Combinación de amoxicilina y ácido clavulánico para infecciones resistentes.'),

-- ANTIINFLAMATORIOS (id_tipo = 2)
('KETOPROFENO 100MG', 2, 'AINE utilizado en procesos inflamatorios y dolor.'),
('CARPROFENO 50MG', 2, 'Antiinflamatorio y analgésico para perros.'),
('MELOXICAM 1.5MG/ML', 2, 'AINE utilizado en perros y gatos para control del dolor y fiebre.'),
('DEXAMETASONA 4MG/ML', 2, 'Corticosteroide de acción rápida para inflamaciones agudas.'),
('PREDNISOLONA 5MG', 2, 'Corticoide antiinflamatorio sistémico.'),
('FLUNIXINA MEGLUMINA', 2, 'AINE potente para inflamaciones y fiebre.'),

-- DESPARASITANTES (id_tipo = 3)
('ALBENDAZOL 10%', 3, 'Desparasitante oral de amplio espectro para uso veterinario.'),
('IVERMECTINA 1%', 3, 'Antiparasitario interno y externo en dosis controladas.'),
('FENBENDAZOL 10%', 3, 'Antiparasitario intestinal para perros, gatos y conejos.'),
('PRAZIQUANTEL 50MG', 3, 'Desparasitante eficaz contra tenias y cestodos.'),
('MILBEMAX', 3, 'Combinación antiparasitaria de amplio espectro (milbemicina + praziquantel).'),
('SELAMECTINA (REVOLUTION)', 3, 'Antiparasitario externo tópico para pulgas, garrapatas y ácaros.'),

-- ANTIFÚNGICOS (id_tipo = 4)
('CLOTRIMAZOL SPRAY', 4, 'Antifúngico tópico para micosis cutáneas.'),
('KETOCONAZOL 200MG', 4, 'Antifúngico sistémico para infecciones por hongos.'),
('MICONAZOL CREMA', 4, 'Tratamiento tópico de infecciones por hongos en piel.'),
('TERBINAFINA 250MG', 4, 'Antifúngico sistémico para dermatitis micótica.'),
('ITRACONAZOL 100MG', 4, 'Antifúngico de amplio espectro, especialmente en gatos.'),

-- ANALGÉSICOS (id_tipo = 5)
('TRAMADOL 50MG', 5, 'Analgésico opiáceo utilizado en manejo del dolor moderado a severo.'),
('BUXTONAL (BUTORFANOL)', 5, 'Analgésico opiáceo leve para control del dolor agudo.'),
('METAMIZOL SÓDICO', 5, 'Analgésico y antipirético no opioide de uso veterinario.'),
('MORFINA 10MG/ML', 5, 'Analgésico opioide para control del dolor intenso.'),
('GABAPENTINA 300MG', 5, 'Control del dolor neuropático y ansiedad en felinos.'),

-- VITAMÍNICOS Y SUPLEMENTOS (id_tipo = 6)
('MULTIVITAMÍNICO PETS', 6, 'Suplemento nutricional multivitamínico para perros y gatos.'),
('VITAMINA B COMPLEX', 6, 'Refuerza el sistema nervioso y metabolismo energético.'),
('VITAMINA C ORAL', 6, 'Antioxidante y refuerzo inmunológico en conejos y roedores.'),
('OMEGA 3 + 6 PETS', 6, 'Suplemento para piel, pelaje y función cardiovascular.'),
('CALCIO PET', 6, 'Suplemento mineral para huesos y crecimiento.'),
('PROBIÓTICO VET', 6, 'Apoya la salud intestinal y el equilibrio de la flora digestiva.');

-- ========================================
-- TABLA: estado_mascota
-- Define el estado clínico o condición general de la mascota.
-- Ejemplo: ACTIVO (en control), EN TRATAMIENTO, RECUPERADO.
-- ========================================
CREATE TABLE estado_mascota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- ESTADO DE LAS MASCOTAS (CATÁLOGO AMPLIADO)
-- ========================================
INSERT INTO estado_mascota (nombre, descripcion) VALUES
('ACTIVA', 'Mascota con atención vigente en la veterinaria.'),
('EN TRATAMIENTO', 'En tratamiento médico o quirúrgico.'),
('RECUPERADA', 'Ha finalizado su tratamiento con éxito.'),
('EN OBSERVACIÓN', 'Bajo control o evaluación médica.'),
('EN CIRUGÍA', 'Actualmente en procedimiento quirúrgico.'),
('EN REHABILITACIÓN', 'En terapia física o recuperación postoperatoria.'),
('CRÓNICA', 'Con enfermedad crónica de seguimiento continuo.'),
('CRÍTICA', 'En estado grave o internada.'),
('INACTIVA', 'Sin actividad reciente o controles pendientes.'),
('TRANSFERIDA', 'Trasladada a otro propietario o centro.'),
('FALLECIDA', 'Mascota registrada como fallecida.'),
('EXTRAVIADA', 'Reportada como perdida.'),
('EN ADOPCIÓN', 'Disponible para adopción.'),
('ADOPTADA', 'Entregada en adopción.'),
('RESCATADA', 'Rescatada y en evaluación inicial.'),
('EN CUARENTENA', 'En aislamiento preventivo.'),
('EN ACOGIDA TEMPORAL', 'En hogar temporal de acogida.'),
('DEVUELTA', 'Mascota devuelta tras adopción fallida.');

-- ========================================
-- TABLA: mascotas
-- Almacena la información detallada de las mascotas registradas.
-- Incluye datos como raza, especie, edad, características físicas y estado clínico.
-- ========================================
CREATE TABLE IF NOT EXISTS mascotas (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
	nombre VARCHAR(64) NOT NULL,
    sexo VARCHAR(1),
	id_cliente BIGINT NOT NULL,
	id_raza INT NULL,
	id_especie INT NOT NULL,
    id_estado INT NOT NULL,
    id_colaborador BIGINT NULL,
    id_veterinario BIGINT NULL,
	fecha_nacimiento DATE NOT NULL,
	pelaje VARCHAR(16),
	id_tamano INT NOT NULL,
	id_etapa INT NOT NULL,
	esterilizado TINYINT NOT NULL DEFAULT 0 CHECK (esterilizado IN (0,1)),
	alergias VARCHAR(128),
	peso DECIMAL(6,2) CHECK (peso >= 0),
	chip TINYINT NOT NULL DEFAULT 0 CHECK (chip IN (0,1)),
	pedigree TINYINT NOT NULL DEFAULT 0 CHECK (pedigree IN (0,1)),
    -- factor_dea: Aplica solo para especie CANINA
	factor_dea TINYINT NOT NULL DEFAULT 0 CHECK (factor_dea IN (0,1)),
	agresividad TINYINT NOT NULL DEFAULT 0 CHECK (agresividad IN (0,1)),
	foto VARCHAR(255),
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE mascotas
	ADD CONSTRAINT fk_mascota_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mascota_estado FOREIGN KEY (id_estado) REFERENCES estado_mascota(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_raza FOREIGN KEY (id_raza) REFERENCES razas(id)
		ON DELETE SET NULL ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_especie FOREIGN KEY (id_especie) REFERENCES especies(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_tamano FOREIGN KEY (id_tamano) REFERENCES tamanos(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_mascota_etapa FOREIGN KEY (id_etapa) REFERENCES etapas_vida(id)
		ON DELETE RESTRICT ON UPDATE CASCADE;
	
-- Índice para consultas rápidas de mascotas por cliente
CREATE INDEX idx_mascotas_cliente ON mascotas(id_cliente);
    
-- Índice para búsquedas eficientes de mascotas por estado
CREATE INDEX idx_mascotas_estado ON mascotas(id_estado);

-- Índice para mejorar las búsquedas de mascotas por raza.
CREATE INDEX idx_mascotas_raza ON mascotas(id_raza);

-- Índice para mejorar las búsquedas de mascotas por especie.
CREATE INDEX idx_mascotas_especie ON mascotas(id_especie);

-- Índice para búsquedas de mascotas por tamaño (útil en reportes o filtros).
CREATE INDEX idx_mascotas_tamano ON mascotas(id_tamano);

-- Índice para facilitar búsquedas de mascotas por etapa de vida (cachorro, adulto, etc.).
CREATE INDEX idx_mascotas_etapa ON mascotas(id_etapa);

-- Índice para ordenar o filtrar mascotas por fecha de registro.
CREATE INDEX idx_mascotas_fecha_registro ON mascotas(fecha_registro);

-- Índice para acelerar búsquedas o filtros por fecha de nacimiento,
-- útil en reportes o estadísticas de edad.
CREATE INDEX idx_mascotas_fecha_nac ON mascotas(fecha_nacimiento);

-- ========================================
-- TABLA: medicamentos_mascota
-- Registro histórico de medicamentos administrados a cada mascota.
-- Incluye vía, dosis, fecha y responsable de la aplicación.
-- ========================================
CREATE TABLE IF NOT EXISTS medicamentos_mascota (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	id_mascota BIGINT NOT NULL,
	id_medicamento INT NOT NULL,
	id_via INT NOT NULL,
	dosis VARCHAR(32),
	fecha_aplicacion DATE NOT NULL,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	id_colaborador BIGINT NULL,
    id_veterinario BIGINT NULL,
	observaciones VARCHAR(64),
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE medicamentos_mascota
	ADD CONSTRAINT fk_med_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_med_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_med_via FOREIGN KEY (id_via) REFERENCES vias_aplicacion(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_med_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
		ON DELETE SET NULL ON UPDATE CASCADE,
	ADD CONSTRAINT fk_med_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
		ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para consultas rápidas del historial de medicamentos
CREATE INDEX idx_medicamentos_mascota_mascota ON medicamentos_mascota(id_mascota);

-- Índice para búsquedas por colaborador en historial de medicamentos.
CREATE INDEX idx_med_mascota_colaborador ON medicamentos_mascota(id_colaborador);

-- Índice para búsquedas por veterinario en historial de medicamentos.
CREATE INDEX idx_med_mascota_veterinario ON medicamentos_mascota(id_veterinario);

-- Índice para reportes históricos de aplicación de medicamentos.
CREATE INDEX idx_med_mascota_fecha ON medicamentos_mascota(fecha_aplicacion);

-- Índice para optimizar las búsquedas por medicamento administrado
CREATE INDEX idx_medicamentos_mascota_medicamento ON medicamentos_mascota(id_medicamento);

-- ========================================
-- TABLA: vacunas_mascota
-- Registro histórico de vacunas aplicadas a las mascotas.
-- Incluye vía, dosis, durabilidad, próxima dosis y colaborador responsable.
-- ========================================
CREATE TABLE IF NOT EXISTS vacunas_mascota (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	id_vacuna INT NOT NULL,
	id_mascota BIGINT NOT NULL,
	id_via INT NOT NULL,
	dosis VARCHAR(32),
	fecha_aplicacion DATE NOT NULL,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	durabilidad_anios INT,
	proxima_dosis DATE NULL,
	id_colaborador BIGINT NULL,
    id_veterinario BIGINT NULL,
	observaciones VARCHAR(128),
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE vacunas_mascota
	ADD CONSTRAINT fk_vacuna FOREIGN KEY (id_vacuna) REFERENCES vacunas(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_vacuna_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_vacuna_via FOREIGN KEY (id_via) REFERENCES vias_aplicacion(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_vacuna_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
		ON DELETE SET NULL ON UPDATE CASCADE,
	ADD CONSTRAINT fk_vacuna_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
		ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para consultas rápidas del historial de vacunas
-- aplicadas a una mascota específica.
CREATE INDEX idx_vacunas_mascota_mascota ON vacunas_mascota(id_mascota);

-- Índice para búsquedas por colaborador en historial de vacunas.
CREATE INDEX idx_vacuna_mascota_colaborador ON vacunas_mascota(id_colaborador);

-- Índice para búsquedas por veterinario en historial de vacunas.
CREATE INDEX idx_vacuna_mascota_veterinario ON vacunas_mascota(id_veterinario);

-- Índice para consultar rápidamente las próximas dosis programadas.
CREATE INDEX idx_vacuna_mascota_proxima_dosis ON vacunas_mascota(proxima_dosis);

-- Índice para reportes históricos de vacunación.
CREATE INDEX idx_vacuna_mascota_fecha ON vacunas_mascota(fecha_aplicacion);

-- Índice para mejorar consultas por tipo de vacuna aplicada,
-- usado en reportes de vacunación y seguimiento clínico.
CREATE INDEX idx_vacuna_mascota_vacuna ON vacunas_mascota(id_vacuna);

-- =============================================================================================================================================

-- ========================================
-- BLOQUE 03: agenda y servicios
-- gestiona los tipos de servicios, su registro,
-- citas programadas, visitas y recordatorios.
-- ========================================
-- USE vet_manada_woof;
-- ========================================
-- TABLA: canales_comunicacion
-- Define los canales de contacto utilizados.
-- Ejemplo: WHATSAPP, EMAIL, LLAMADA.
-- ========================================
CREATE TABLE IF NOT EXISTS canales_comunicacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE
);
INSERT INTO canales_comunicacion (nombre) VALUES
('WHATSAPP'),
('EMAIL'),
('LLAMADA TELEFÓNICA'),
('SMS'),
('REDES SOCIALES'),
('MOSTRADOR');

-- ========================================
-- TABLA: medios_pago
-- ========================================
CREATE TABLE IF NOT EXISTS medios_pago (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128)
);
INSERT INTO medios_pago (nombre, descripcion) VALUES
('EFECTIVO', 'Pago directo en caja.'),
('TARJETA DE CRÉDITO', 'Pago mediante tarjeta de crédito Visa, MasterCard, etc.'),
('TARJETA DE DÉBITO', 'Pago con tarjeta de débito bancaria.'),
('TRANSFERENCIA BANCARIA', 'Transferencia directa a cuenta de la veterinaria.'),
('YAPE', 'Pago rápido por aplicación móvil Yape.'),
('PLIN', 'Pago rápido por aplicación móvil Plin.'),
('LINK DE PAGO', 'Pago mediante link enviado por WhatsApp o correo.');

-- ========================================
-- TABLA: estado_agenda
-- Lista los posibles estados de una cita agendada.
-- Ejemplo: PENDIENTE, CONFIRMADA, CANCELADA, ATENDIDA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128)
);
INSERT INTO estado_agenda (nombre, descripcion) VALUES
('PENDIENTE', 'Cita registrada en espera de confirmación.'),
('CONFIRMADA', 'Cita confirmada por el cliente o personal.'),
('REPROGRAMADA', 'Cita movida a otra fecha o hora.'),
('CANCELADA', 'Cita cancelada por el cliente o el personal.'),
('ATENDIDA', 'Cita finalizada y registrada como atendida.'),
('NO ASISTIÓ', 'El cliente no se presentó a la cita.');

-- ========================================
-- TABLA: tipo_recordatorio
-- Catálogo que define los tipos de recordatorios que pueden generarse 
-- en el sistema para eventos clínicos, estéticos o administrativos.
-- ========================================
CREATE TABLE tipo_recordatorio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128)
);
INSERT INTO tipo_recordatorio (nombre, descripcion) VALUES
('VACUNACIÓN', 'Recordatorio para aplicación o refuerzo de vacunas.'),
('CONTROL MÉDICO', 'Recordatorio de control o revisión general.'),
('DESPARASITACIÓN', 'Aviso para próxima dosis de desparasitación.'),
('CITA AGENDADA', 'Recordatorio de cita próxima confirmada.'),
('RENOVACIÓN PLAN SALUD', 'Aviso para renovación de plan médico o membresía.'),
('ANIVERSARIO MASCOTA', 'Mensaje conmemorativo por el cumpleaños o adopción de la mascota.');

-- ========================================
-- TABLA: medio_solicitud
-- Enumera los medios por los que un cliente solicita una cita o servicio.
-- Ejemplo: TELÉFONO, WEB, PRESENCIAL.
-- ========================================
CREATE TABLE IF NOT EXISTS medio_solicitud (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128)
);
INSERT INTO medio_solicitud (nombre, descripcion) VALUES
('TELÉFONO', 'Solicitud realizada por llamada telefónica.'),
('WHATSAPP', 'Solicitud recibida mediante mensaje de WhatsApp.'),
('WEB', 'Solicitud enviada a través del sitio web.'),
('PRESENCIAL', 'Solicitud directa en mostrador o recepción.'),
('REDES SOCIALES', 'Solicitud por mensaje de Facebook, Instagram u otra red.');

-- ========================================
-- TABLA: servicios
-- Catálogo de servicios que ofrece la veterinaria.
-- ========================================
CREATE TABLE IF NOT EXISTS servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128)
);
INSERT INTO servicios (nombre, descripcion) VALUES
-- ÁREA MÉDICA
('VACUNACIÓN', 'Aplicación de vacunas preventivas según plan sanitario.'),
('DESPARASITACIÓN', 'Administración de antiparasitarios internos o externos según protocolo.'),

-- ÁREA ESTÉTICA Y SPA
('BAÑO Y CORTE', 'Baño completo con shampoo medicado o cosmético y corte según raza.'),
('BAÑO MEDICADO', 'Baño terapéutico con productos específicos para condiciones dermatológicas.'),
('CORTE DE UÑAS', 'Limpieza y recorte de uñas de forma segura.'),
('LIMPIEZA DENTAL', 'Limpieza bucal no invasiva o profilaxis dental veterinaria.'),
('SPA RELAJANTE', 'Baño aromático, masaje y cepillado profesional para relajación.'),
('PEINADO Y DESENREDADO', 'Acondicionamiento del pelaje y desenredado profundo.'),
('TRATAMIENTO CAPILAR', 'Aplicación de mascarillas o tratamientos especiales para el pelaje.'),

-- ÁREA HOSPEDAJE Y CUIDADO
('HOSPEDAJE DIARIO', 'Alojamiento temporal con alimentación y supervisión veterinaria.'),
('GUARDERÍA DIURNA', 'Cuidado y recreación durante el día para mascotas activas.'),
('PASEO CONTROLADO', 'Servicio de paseo en áreas seguras y monitoreadas.'),
('ALIMENTACIÓN PERSONALIZADA', 'Planes de comida específicos según edad, peso o condición médica.'),

-- SERVICIOS COMPLEMENTARIOS
('VENTA DE PRODUCTOS', 'Adquisición de alimentos, accesorios, medicamentos o juguetes.'),
('RECOJO A DOMICILIO', 'Transporte seguro de la mascota desde o hacia la veterinaria.'),
('SERVICIO A DOMICILIO', 'Consulta o atención médica veterinaria en el hogar.');

-- ========================================
-- TABLA: agenda
-- Agenda de citas programadas entre cliente, mascota y servicio.
-- Incluye fecha, hora, duración estimada en minutos, observaciones y abono inicial.
-- Ejemplo: Cita el 2025-08-01 a las 10:00am para consulta médica de la mascota "FIRULAIS".
-- ========================================
CREATE TABLE IF NOT EXISTS agenda (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_cliente BIGINT NOT NULL,
    id_mascota BIGINT NOT NULL,
    id_medio_solicitud INT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    duracion_estimada_min INT CHECK (duracion_estimada_min >= 0),
    abono_inicial DECIMAL(10,2) DEFAULT 0 CHECK (abono_inicial >= 0), -- 50
    total_cita DECIMAL(10,2) DEFAULT 0 CHECK (total_cita >= 0), -- de la cita completa es decir uno o mas servicios
    id_estado INT NOT NULL, 
    observaciones VARCHAR(256),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE agenda
    ADD CONSTRAINT fk_agenda_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_medio FOREIGN KEY (id_medio_solicitud) REFERENCES medio_solicitud(id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_estado FOREIGN KEY (id_estado) REFERENCES estado_agenda(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de citas por cliente
CREATE INDEX idx_agenda_cliente ON agenda(id_cliente);

-- Índice para búsquedas rápidas de citas por mascota
CREATE INDEX idx_agenda_mascota ON agenda(id_mascota);

-- Índice para filtrar o agrupar citas según el estado
CREATE INDEX idx_agenda_estado ON agenda(id_estado);

-- Índice compuesto para consultar las citas de un cliente en un rango de fechas
CREATE INDEX idx_agenda_cliente_fecha ON agenda (id_cliente, fecha);

-- Índice para consultas directas por fecha de cita (calendario)
CREATE INDEX idx_agenda_fecha ON agenda(fecha);

-- ========================================
-- TABLA: ingresos_servicios
-- Registra los servicios realizados, su costo, responsable y observaciones.
-- ========================================
CREATE TABLE IF NOT EXISTS ingresos_servicios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda BIGINT NOT NULL,
    id_servicio INT NOT NULL,
    id_colaborador BIGINT,
    id_veterinario BIGINT,
	cantidad INT CHECK (cantidad >= 0),
    duracion_min INT CHECK (duracion_min >= 0),
    observaciones VARCHAR(128),
    valor_servicio DECIMAL(10,2) NOT NULL CHECK (valor_servicio >= 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE ingresos_servicios
	ADD CONSTRAINT fk_ingreso_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id)
		ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ingreso_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ingreso_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ingreso_vet FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índices con comentarios
-- consultas de todos los ingresos de un servicio específico
CREATE INDEX idx_ingresos_servicios_servicio ON ingresos_servicios(id_servicio); 
-- reportes de servicios atendidos por un colaborador
CREATE INDEX idx_ingresos_servicios_colab ON ingresos_servicios(id_colaborador);
-- reportes de servicios atendidos por un veterinario 
CREATE INDEX idx_ingresos_servicios_vet ON ingresos_servicios(id_veterinario); 
-- reportes de servicios por colaborador y rango de fechas
CREATE INDEX idx_ingresos_servicios_colab_fecha ON ingresos_servicios (id_colaborador, fecha_registro); 

-- ========================================
-- TABLA: agenda_pagos
-- Registra pagos o abonos realizados para citas agendadas.
-- Permite vincular un abono a la cita antes de generar la factura formal.
-- Incluye fecha, monto y medio de pago.
-- ========================================
CREATE TABLE IF NOT EXISTS agenda_pagos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda BIGINT NOT NULL,
    id_medio_pago INT NOT NULL,
    id_usuario INT NULL,
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0), 
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(128)
);

ALTER TABLE agenda_pagos
    ADD CONSTRAINT fk_agendapago_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agendapago_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agendapago_mediopago FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de pagos de una cita
CREATE INDEX idx_agendapagos_agenda ON agenda_pagos(id_agenda);

-- Índice para filtrar pagos por medio de pago
CREATE INDEX idx_agendapagos_mediopago ON agenda_pagos(id_medio_pago);

-- Índice para consultas por fecha de pago (útil para reportes o conciliaciones internas)
CREATE INDEX idx_agendapagos_fecha ON agenda_pagos(fecha_pago);

-- ========================================
-- TABLA: recordatorios_agenda
-- Registra recordatorios automáticos relacionados a citas de la agenda.
-- Incluye fecha, hora, mensaje personalizado y si ya fue enviado.
-- Ejemplo: Recordatorio de cita médica enviado al cliente un día antes.
-- ========================================
CREATE TABLE IF NOT EXISTS recordatorios_agenda (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda BIGINT NOT NULL,
    id_tipo_recordatorio INT NOT NULL DEFAULT 1,
    fecha_recordatorio DATE NOT NULL,
    hora TIME NULL,
    mensaje TEXT NOT NULL,
    id_canal_comunicacion INT NULL,
    enviado TINYINT NOT NULL DEFAULT 0 CHECK (enviado IN (0,1)),
    fecha_envio DATETIME NULL
);
ALTER TABLE recordatorios_agenda
    ADD CONSTRAINT fk_recordatorio_tipo FOREIGN KEY (id_tipo_recordatorio) REFERENCES tipo_recordatorio(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_recordatorio_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_recordatorio_canal FOREIGN KEY (id_canal_comunicacion) REFERENCES canales_comunicacion(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de recordatorios asociados a una cita específica.
CREATE INDEX idx_recordatorio_agenda ON recordatorios_agenda(id_agenda);

-- Índice para buscar recordatorios programados para hoy o próximos días
--  (por ejemplo, los que el sistema debe enviar mañana).
CREATE INDEX idx_recordatorio_fecha ON recordatorios_agenda(fecha_recordatorio);

-- Índice para filtrar recordatorios aún no enviados
-- (en combinación con fecha_recordatorio, si se requiere).
CREATE INDEX idx_recordatorio_enviado ON recordatorios_agenda(enviado);

-- =============================================================================================================================================
-- =============================================================================================================================================
-- =============================================================================================================================================

-- ========================================
-- BLOQUE 04: HISTORIA CLÍNICA Y ARCHIVOS ASOCIADOS
-- Gestiona el registro de atenciones médicas de cada mascota,
-- vinculando la información clínica con los ingresos/visitas
-- y permitiendo almacenar archivos relacionados.
-- ========================================

-- ========================================
-- TABLA: estado_historia_clinica
-- Define los estados posibles del ciclo de una historia clínica.
-- Ejemplo: ABIERTA, EN REVISIÓN, CERRADA.
-- ========================================
CREATE TABLE estado_historia_clinica (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
INSERT INTO estado_historia_clinica (nombre, descripcion) VALUES
('ABIERTA', 'Historia clínica en proceso de registro o atención activa.'),
('EN EVALUACIÓN', 'Pendiente de diagnóstico definitivo o revisión médica.'),
('EN TRATAMIENTO', 'Paciente con tratamiento activo y evolución en curso.'),
('EN REVISIÓN', 'En seguimiento por control o revaloración médica.'),
('DERIVADA', 'Derivada a otro veterinario o especialidad.'),
('EN LABORATORIO', 'En espera o análisis de resultados clínicos o de imagen.'),
('EN HOSPITALIZACIÓN', 'Historia activa mientras la mascota se encuentra internada.'),
('POST-OPERATORIA', 'Historia en seguimiento tras una cirugía.'),
('TEMPORAL', 'Historia generada por atención esporádica o sin ficha completa.'),
('EN ESPERA DE CIERRE', 'Atención completada, pendiente de firma o revisión final.'),
('CERRADA', 'Historia clínica concluida y validada por el veterinario.'),
('ARCHIVADA', 'Historia clínica cerrada y almacenada en el sistema.'),
('REABIERTA', 'Historia previamente cerrada, reactivada por un nuevo evento médico.'),
('ANULADA', 'Historia cancelada o creada por error administrativo.');

-- ========================================
-- TABLA: tipos_archivo_clinico
-- Catálogo de tipos de archivos médicos que pueden asociarse a una historia clínica.
-- Ejemplo: RADIOGRAFÍA, ANÁLISIS DE SANGRE.
-- ========================================
CREATE TABLE IF NOT EXISTS tipos_archivo_clinico (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) UNIQUE NOT NULL,
    descripcion VARCHAR(128)
);
INSERT INTO tipos_archivo_clinico (nombre, descripcion) VALUES
-- IMÁGENES DIAGNÓSTICAS
('RADIOGRAFÍA', 'Imagen diagnóstica obtenida mediante rayos X.'),
('ECOGRAFÍA', 'Estudio de diagnóstico por ultrasonido.'),
('TOMOGRAFÍA', 'Imagen médica avanzada por TAC o escáner.'),
('FOTOGRAFÍA CLÍNICA', 'Imagen de heridas, lesiones o condiciones físicas.'),

-- LABORATORIO Y EXÁMENES
('ANÁLISIS DE SANGRE', 'Resultados de hemograma o bioquímica sanguínea.'),
('ANÁLISIS DE ORINA', 'Informe de análisis de orina.'),
('ANÁLISIS COPROLÓGICO', 'Resultados de examen de heces.'),
('CITOLOGÍA', 'Informe microscópico de células.'),
('HISTOPATOLOGÍA', 'Informe de biopsia o tejido analizado.'),
('MICROBIOLOGÍA', 'Informe de cultivo o antibiograma.'),

--  DOCUMENTOS CLÍNICOS
('CONSENTIMIENTO INFORMADO', 'Documento firmado por el propietario antes de un procedimiento.'),
('FORMULARIO DE INGRESO', 'Ficha inicial de ingreso médico.'),
('RECETA MÉDICA', 'Prescripción de medicamentos y dosis.'),
('CERTIFICADO MÉDICO', 'Documento oficial emitido por el veterinario.'),
('PLAN DE TRATAMIENTO', 'Cronograma de terapias, medicamentos y controles.'),
('EVOLUCIÓN CLÍNICA', 'Notas o actualizaciones del seguimiento del paciente.'),
('HOJA DE ALTA', 'Resumen final de la hospitalización o tratamiento.'),

-- ESTÉTICA Y SPA
('REGISTRO DE GROOMING', 'Ficha o fotos del servicio estético realizado.'),
('EVALUACIÓN DERMATOLÓGICA', 'Informe visual o técnico del estado de piel y pelaje.'),
('CONTROL POST-GROOMING', 'Registro de revisión posterior al servicio estético.'),

-- HOSPEDAJE Y CONTROL
('FICHA DE HOSPEDAJE', 'Registro de ingreso, control y cuidados durante la estadía.'),
('CONTROL DE ALIMENTACIÓN', 'Registro de dieta y horarios de alimentación.'),
('CONTROL DE ACTIVIDAD', 'Bitácora de paseo, juego o descanso.'),

-- ADMINISTRATIVOS Y OTROS
('AUTORIZACIÓN DE PROCEDIMIENTO', 'Permiso firmado para intervención o anestesia.'),
('REPORTE DE INCIDENTE', 'Registro de eventos o accidentes durante la atención.'),
('ARCHIVO ADICIONAL', 'Documento o archivo complementario no clasificado.');

-- ========================================
-- TABLA: historia_clinica
-- Registra atenciones médicas realizadas a cada mascota.
-- Se vincula con la visita, servicio, colaborador y veterinario responsable.
-- ========================================
CREATE TABLE IF NOT EXISTS historia_clinica (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_mascota BIGINT NOT NULL,
    id_colaborador BIGINT NULL,
    id_veterinario BIGINT NULL,
    motivo_consulta VARCHAR(128) NULL,
    diagnostico TEXT NULL,
    tratamiento TEXT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    descripcion TEXT NULL,
    observaciones TEXT NULL,
    fecha_registro_inicial TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_estado INT NULL
);
ALTER TABLE historia_clinica
    ADD CONSTRAINT fk_hist_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hist_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hist_vet FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
	ADD CONSTRAINT fk_hist_estado FOREIGN KEY (id_estado) REFERENCES estado_historia_clinica(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de historias clínicas por mascota
-- (útil para listar todo el historial médico de una mascota específica).
CREATE INDEX idx_historia_clinica_mascota ON historia_clinica(id_mascota);

-- Índice para consultas que agrupan o filtran por veterinario responsable.
-- Muy útil si se generan reportes de actividades por veterinario.
CREATE INDEX idx_historia_clinica_veterinario ON historia_clinica(id_veterinario);

-- Índice para facilitar búsquedas cronológicas
-- (permite ordenar o filtrar historias clínicas por fecha de atención,
-- útil en listados por rango de fechas o reportes médicos).
CREATE INDEX idx_historia_clinica_fecha ON historia_clinica(fecha);

-- Índice para consultas que buscan historias clínicas por colaborador que registró la atención.
-- Útil para filtros del tipo: "atenciones realizadas por el colaborador X".
CREATE INDEX idx_historia_clinica_colaborador ON historia_clinica(id_colaborador);

-- Índice para facilitar filtros por estado clínico
-- (por ejemplo: mostrar solo historias abiertas o en revisión).
CREATE INDEX idx_historia_clinica_estado ON historia_clinica(id_estado);


-- ========================================
-- TABLA: historia_clinica_archivos
-- Almacena archivos digitales asociados a un registro clínico.
-- Incluye imágenes, análisis y otros documentos relevantes.
-- Ejemplo: Imagen de radiografía torácica asociada al historial médico #58.
-- ========================================
CREATE TABLE IF NOT EXISTS historia_clinica_archivos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_historia_clinica BIGINT NOT NULL,
    id_t_archivo INT NULL,
    nombre_archivo VARCHAR(128) NOT NULL,
    extension_archivo VARCHAR(128) NOT NULL,
    descripcion VARCHAR(128),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE historia_clinica_archivos
    ADD CONSTRAINT fk_archivo_tipo FOREIGN KEY (id_t_archivo) REFERENCES tipos_archivo_clinico(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_archivo_historia FOREIGN KEY (id_historia_clinica) REFERENCES historia_clinica(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de archivos clínicos por historia clínica
-- (permite listar rápidamente todos los archivos asociados a un registro médico).
CREATE INDEX idx_historia_archivos_historia ON historia_clinica_archivos(id_historia_clinica);

-- Índice para listar archivos según tipo (ej. ver solo radiografías o solo análisis).
-- Útil para vistas o filtros en la interfaz de archivos clínicos.
CREATE INDEX idx_archivo_tipo ON historia_clinica_archivos(id_t_archivo);

