-- =========================================================
-- BASE DE DATOS: vet_manada_woof
-- Este script contiene la definición inicial de la base de datos
-- y las tablas principales relacionadas al núcleo (empresa),
-- seguridad (usuarios, roles), entidades (clientes, proveedores, colaboradores)
-- y gestión de personal (veterinarios, horarios, asistencia).
-- Al final del script podrá hacer un show tables para revisar mejor.
-- =========================================================
-- select * from proveedores;
-- ========================================
-- 0. Creación de la Base de Datos
-- ========================================
DROP DATABASE IF EXISTS vet_manada_woof;
CREATE DATABASE vet_manada_woof;
USE vet_manada_woof;

-- ========================================
-- BLOQUE 01: Administracion inicial
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

	CREATE UNIQUE INDEX idx_tipo_documento_descripcion_ci ON tipo_documento(descripcion);
	-- ========================================
	-- TIPO DE DOCUMENTO
	-- ========================================
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
-- ========================================
-- TIPO DE NATURALEZA LEGAL DE LA ENTIDAD
-- Clasifica si la entidad es de tipo NATURAL (persona física) o JURÍDICA (empresa o institución con RUC propio).
-- ========================================
INSERT INTO tipo_persona_juridica (nombre, descripcion) VALUES
('NATURAL', 'Persona natural que representa una entidad individual'),
('JURIDICA', 'Entidad jurídica con existencia legal y RUC propio');

-- ========================================
-- TABLA: usuarios
-- Guarda las credenciales de acceso al sistema para cada persona autorizada.
-- Ejemplo: username = “admin01”, password_hash = “$2a$10$JHdY...”
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_baja DATETIME NULL
);

-- ========================================
-- Ejemplos de usuarios SOLO PRUEBAS)
-- ========================================
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
-- ========================================
-- ROLES DEL SISTEMA
-- ========================================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMINISTRADOR GENERAL', NULL),
('ADMINISTRADOR G 2', NULL),
('AUXILIAR CAJA', NULL),
('AUXILIAR GROMERS', NULL);

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
-- usuarios con roles (ejemplo)
-- ========================================
INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES
(1, 1),  -- admin_woof  → ADMINISTRADOR GENERAL
(2, 2),  -- admin_g2    → ADMINISTRADOR G 2
(3, 3),  -- caja_milo   → AUXILIAR CAJA
(4, 4);  -- gromer_luna → AUXILIAR GROMERS

-- ========================================
-- TABLA: entidades
-- Centraliza los datos generales de personas y empresas del sistema.
-- ========================================
CREATE TABLE IF NOT EXISTS entidades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
	id_tipo_persona_juridica INT NOT NULL,			-- natural o juridica
    nombre VARCHAR(128) NOT NULL,
    sexo VARCHAR(1),
    documento VARCHAR(20) NOT NULL UNIQUE,
    id_tipo_documento INT NOT NULL,
	telefono VARCHAR(15) CHECK (telefono REGEXP '^[0-9+ ]{6,15}$'),
	correo VARCHAR(64) UNIQUE,												-- la verificacion queda para el frontend
    direccion VARCHAR(128),
    ciudad VARCHAR(64) NOT NULL,
    distrito VARCHAR(64) NOT NULL,
    representante VARCHAR(64) NULL,								-- Deberá ser en tal caso el mismo nombre
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE entidades
    ADD CONSTRAINT fk_entidad_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id),
    ADD CONSTRAINT fk_entidad_persona_juridica FOREIGN KEY (id_tipo_persona_juridica) REFERENCES tipo_persona_juridica(id)
    ON DELETE RESTRICT;

-- Índice para acelerar las búsquedas de entidades por documento 
-- (útil en login, validaciones, formularios).
CREATE UNIQUE INDEX idx_entidades_documento ON entidades(documento);

-- Índice para acelerar la validación y búsqueda por correo 
-- (por ejemplo, recuperación de cuenta o contacto).
CREATE UNIQUE INDEX idx_entidades_correo ON entidades(correo);

-- Índice para consultas filtradas por tipo de documento (DNI, RUC...).
CREATE INDEX idx_entidades_tipo_documento ON entidades(id_tipo_documento);

-- ========================================
-- TABLA: colaboradores
-- Registra a los trabajadores vinculados a la veterinaria (personal interno).
-- ========================================
CREATE TABLE IF NOT EXISTS colaboradores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_entidad BIGINT NOT NULL UNIQUE,
    fecha_ingreso DATE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
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
-- Representa a las personas o empresas que suministran productos o servicios.
-- Ejemplo: entidad = “Laboratorios ACME”, activo = 1
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
-- Representa a las personas o empresas que reciben servicios de la veterinaria.
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
-- Ejemplo: “DERMATOLOGÍA”, “CIRUGÍA GENERAL”
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
-- Contiene la información específica de los veterinarios de la clínica.
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
-- TABLA: dias_semana
-- Lista los días de la semana para asignación de horarios.
-- ========================================
CREATE TABLE dias_semana (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) UNIQUE NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
-- ========================================
-- DÍAS DE LA SEMANA
-- ========================================
INSERT INTO dias_semana (nombre) VALUES 
('LUNES'), ('MARTES'), ('MIÉRCOLES'), ('JUEVES'), ('VIERNES'), ('SÁBADO'), ('DOMINGO');

-- ========================================
-- TABLA: tipos_dia
-- Lista tipos especiales de día (laborable, feriado, etc.).
-- ========================================
CREATE TABLE tipos_dia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE tipos_dia MODIFY nombre VARCHAR(32) NOT NULL COLLATE utf8mb4_general_ci;

CREATE UNIQUE INDEX idx_tipos_dia_nombre_ci ON tipos_dia(nombre);

-- ========================================
-- TIPOS DE DÍA
-- ========================================
INSERT INTO tipos_dia (nombre) VALUES 
('FERIADO'), ('LABORAL'), ('DÍA PUENTE'), ('DÍA NO LABORABLE');

-- ========================================
-- TABLA: horarios_trabajo
-- Define los turnos laborales establecidos para cada colaborador.
-- ========================================
-- NOTA: Esta tabla no evita solapamientos de turnos automáticamente.
-- Validar en la lógica de aplicación con una consulta
CREATE TABLE IF NOT EXISTS horarios_trabajo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NULL UNIQUE,
    id_colaborador BIGINT NOT NULL,
    id_dia_semana INT NOT NULL,
    id_tipo_dia INT DEFAULT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    CHECK (hora_fin > hora_inicio) 
);
ALTER TABLE horarios_trabajo
    ADD CONSTRAINT fk_horario_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_horario_colab_dias FOREIGN KEY (id_dia_semana) REFERENCES dias_semana(id)
    ON DELETE RESTRICT,
    ADD CONSTRAINT fk_horario_tipo_dia FOREIGN KEY (id_tipo_dia) REFERENCES tipos_dia(id)
    ON DELETE RESTRICT;

-- Índice para consultas rápidas de horarios por colaborador
-- (útil para generar la grilla semanal de turnos).
CREATE INDEX idx_horarios_colaborador ON horarios_trabajo(id_colaborador);

-- Índice para facilitar búsquedas por tipo de día en horarios laborales.
CREATE INDEX idx_horarios_tipo_dia ON horarios_trabajo(id_tipo_dia);

-- ========================================
-- TABLA: registro_asistencia
-- Controla los registros de asistencia diarios del personal.
-- ========================================
CREATE TABLE IF NOT EXISTS registro_asistencia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    observaciones TEXT,
    CHECK (hora_salida IS NULL OR hora_salida >= hora_entrada)
);
ALTER TABLE registro_asistencia
    ADD CONSTRAINT fk_asistencia_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice compuesto para consultas rápidas de asistencia:
-- permite buscar registros de asistencia de un colaborador en una fecha específica
-- o en un rango de fechas.
CREATE INDEX idx_asistencia_colaborador_fecha ON registro_asistencia(id_colaborador, fecha);

-- Índice para búsquedas generales por fecha en asistencia.
CREATE INDEX idx_asistencia_fecha ON registro_asistencia(fecha);
