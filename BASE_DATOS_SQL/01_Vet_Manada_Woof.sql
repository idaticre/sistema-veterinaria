-- =========================================================
-- BASE DE DATOS: vet_manada_woof
-- Este script contiene la definición inicial de la base de datos
-- y las tablas principales relacionadas al núcleo (empresa),
-- seguridad (usuarios, roles), entidades (clientes, proveedores, colaboradores)
-- y gestión de personal (veterinarios, horarios, asistencia).
-- Al final del script podrá hacer un show tables para revisar mejor.
-- =========================================================

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
    codigo VARCHAR(16) NOT NULL UNIQUE,
    descripcion VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_persona_juridica
-- Clasifica la naturaleza legal de la entidad: NATURAL o JURÍDICA.
-- ========================================
CREATE TABLE tipo_persona_juridica (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE, -- Ej: 'NATURAL', 'JURIDICA'
    descripcion VARCHAR(64),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: usuarios
-- Guarda las credenciales de acceso al sistema para cada persona autorizada.
-- Ejemplo: username = “admin01”, password_hash = “$2a$10$JHdY...”
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    username VARCHAR(32) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_baja DATETIME NULL
);

-- ========================================
-- TABLA: roles
-- Define los roles asignables a usuarios del sistema.
-- Ejemplo: “ADMIN”, “VETERINARIO”
-- ========================================
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128) DEFAULT NULL,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: usuarios_roles
-- Relaciona usuarios con uno o varios roles.
-- Ejemplo: usuario_id = 1, rol_id = 2 (Usuario 1 tiene el rol 2 - VETERINARIO)
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios_roles (
    id_usuario INT,
    id_rol INT,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_rol)
);
ALTER TABLE usuarios_roles
    ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON DELETE CASCADE,
    ADD CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES roles(id)
    ON DELETE CASCADE;

-- Índice para búsquedas rápidas de roles asignados a un usuario específico.
CREATE INDEX idx_usuarios_roles_usuario ON usuarios_roles(id_usuario);

-- ========================================
-- TABLA: tipo_entidad
-- Lista los tipos de entidad que pueden existir en el sistema.
-- Ejemplo: “CLIENTE”, “PROVEEDOR”, “COLABORADOR”
-- ========================================
CREATE TABLE tipo_entidad (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) UNIQUE NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: entidades
-- Centraliza los datos generales de personas y empresas del sistema.
-- ========================================
CREATE TABLE IF NOT EXISTS entidades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_tipo_entidad INT NOT NULL,					-- cliente, colaborador, proveedor
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
	ADD CONSTRAINT fk_entidad_tipo FOREIGN KEY (id_tipo_entidad) REFERENCES tipo_entidad(id),
    ADD CONSTRAINT fk_entidad_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id),
    ADD CONSTRAINT fk_entidad_persona_juridica FOREIGN KEY (id_tipo_persona_juridica) REFERENCES tipo_persona_juridica(id)
    ON DELETE RESTRICT;

-- Índice para búsquedas rápidas de entidades según su tipo
-- (clientes, proveedores, colaboradores).
CREATE INDEX idx_entidades_tipo ON entidades(id_tipo_entidad);

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
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_entidad INT NOT NULL UNIQUE,
    fecha_ingreso DATE,
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
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_entidad INT NOT NULL UNIQUE,
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
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_entidad INT NOT NULL UNIQUE,
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
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: veterinarios
-- Contiene la información específica de los veterinarios de la clínica.
-- ========================================
CREATE TABLE IF NOT EXISTS veterinarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_colaborador INT NOT NULL UNIQUE,
    id_especialidad INT NOT NULL,
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
-- TABLA: tipos_dia
-- Lista tipos especiales de día (laborable, feriado, etc.).
-- ========================================
CREATE TABLE tipos_dia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(20) UNIQUE NOT NULL
);

-- ========================================
-- TABLA: horarios_trabajo
-- Define los turnos laborales establecidos para cada colaborador.
-- ========================================
-- NOTA: Esta tabla no evita solapamientos de turnos automáticamente.
-- Validar en la lógica de aplicación con una consulta
CREATE TABLE IF NOT EXISTS horarios_trabajo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_colaborador INT NOT NULL,
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
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_colaborador INT NOT NULL,
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
    codigo VARCHAR(16) NOT NULL UNIQUE,
	nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: razas
-- Lista las razas asociadas a una especie específica.
-- Ejemplo: LABRADOR (especie PERRO), SIAMÉS (especie GATO).
-- ========================================
CREATE TABLE IF NOT EXISTS razas (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	id_especie INT,
	nombre VARCHAR(32) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE razas 
	ADD CONSTRAINT uq_raza_especie_nombre UNIQUE (id_especie, nombre),
	ADD CONSTRAINT fk_raza_especie FOREIGN KEY (id_especie) REFERENCES especies(id)
	ON DELETE RESTRICT
	ON UPDATE CASCADE;

-- Índice para búsquedas de razas según la especie seleccionada (útil en formularios o filtros).
CREATE INDEX idx_razas_especie ON razas(id_especie);

-- ========================================
-- TABLA: tamanos
-- Clasifica a las mascotas según su tamaño corporal.
-- Ejemplo: talla_equivalente = "S", descripcion = "Pequeño".
-- ========================================
CREATE TABLE IF NOT EXISTS tamanos (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	talla_equivalente VARCHAR(8) NOT NULL UNIQUE,
	descripcion VARCHAR(16) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: etapas_vida
-- Catálogo de etapas de desarrollo de una mascota.
-- Ejemplo: CACHORRO, ADULTO, SENIOR.
-- ========================================
CREATE TABLE IF NOT EXISTS etapas_vida (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	descripcion VARCHAR(16) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: vacunas
-- Catálogo de vacunas disponibles según especie.
-- Ejemplo: RABIA (especie PERRO), TRIPLE FELINA (especie GATO).
-- ========================================
CREATE TABLE IF NOT EXISTS vacunas (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	nombre VARCHAR(64) NOT NULL,
	id_especie INT NOT NULL,
    descripcion VARCHAR(64),
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

-- ========================================
-- TABLA: medicamento_tipo
-- Lista los tipos o clasificaciones de medicamentos utilizados.
-- Ejemplo: ANTIBIÓTICO, ANTIINFLAMATORIO.
-- ========================================
CREATE TABLE IF NOT EXISTS medicamento_tipo (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: vias_aplicacion
-- Vías por las cuales se aplican medicamentos o vacunas.
-- Ejemplo: ORAL, INYECTABLE, TÓPICA.
-- ========================================
CREATE TABLE IF NOT EXISTS vias_aplicacion (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	nombre VARCHAR(32) UNIQUE NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: medicamentos
-- Catálogo de medicamentos utilizados en tratamientos clínicos.
-- Ejemplo: AMOXICILINA (tipo ANTIBIÓTICO), PREDNISONA (tipo ANTIINFLAMATORIO).
-- ========================================
CREATE TABLE IF NOT EXISTS medicamentos (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
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
-- (útil en reportes o filtrado de inventarios por categoría).
CREATE INDEX idx_medicamentos_tipo ON medicamentos(id_tipo);

-- ========================================
-- TABLA: estado_mascota
-- Define el estado clínico o condición general de la mascota.
-- Ejemplo: ACTIVO (en control), EN TRATAMIENTO, RECUPERADO.
-- ========================================
CREATE TABLE estado_mascota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: mascotas
-- Almacena la información detallada de las mascotas registradas.
-- Incluye datos como raza, especie, edad, características físicas y estado clínico.
-- ========================================
CREATE TABLE IF NOT EXISTS mascotas (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	nombre VARCHAR(64) NOT NULL,
    sexo VARCHAR(1),
	id_cliente INT NOT NULL,
	id_raza INT NULL,
	id_especie INT NOT NULL,
    id_estado INT NOT NULL,
    id_colaborador INT NULL,
    id_veterinario INT NULL,
	fecha_nacimiento DATE NOT NULL,
	pelaje VARCHAR(16),
	id_tamano INT NOT NULL,
	id_etapa INT NOT NULL,
	esterilizado TINYINT NOT NULL DEFAULT 0 CHECK (esterilizado IN (0,1)),
	alergias VARCHAR(128),
	peso DECIMAL(6,2) CHECK (peso >= 0),
	chip TINYINT NOT NULL DEFAULT 0 CHECK (chip IN (0,1)),
	pedigree TINYINT NOT NULL DEFAULT 0 CHECK (pedigree IN (0,1)),
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
-- (permite listar todas las mascotas de un cliente en particular).
CREATE INDEX idx_mascotas_cliente ON mascotas(id_cliente);
    
-- Índice para búsquedas eficientes de mascotas por estado
-- (útil en reportes, listados filtrados o análisis clínico).
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


-- ========================================
-- TABLA: medicamentos_mascota
-- Registro histórico de medicamentos administrados a cada mascota.
-- Incluye vía, dosis, fecha y responsable de la aplicación.
-- ========================================
CREATE TABLE IF NOT EXISTS medicamentos_mascota (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	id_mascota INT NOT NULL,
	id_medicamento INT NOT NULL,
	id_via INT NOT NULL,
	dosis VARCHAR(32),
	fecha_aplicacion DATE NOT NULL,
	id_colaborador INT NULL,
    id_veterinario INT NULL,
	observaciones VARCHAR(64),
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
-- aplicados a una mascota específica.
CREATE INDEX idx_medicamentos_mascota_mascota ON medicamentos_mascota(id_mascota);

-- Índice para búsquedas por colaborador en historial de medicamentos.
CREATE INDEX idx_med_mascota_colaborador ON medicamentos_mascota(id_colaborador);

-- Índice para búsquedas por veterinario en historial de medicamentos.
CREATE INDEX idx_med_mascota_veterinario ON medicamentos_mascota(id_veterinario);

-- Índice para reportes históricos de aplicación de medicamentos.
CREATE INDEX idx_med_mascota_fecha ON medicamentos_mascota(fecha_aplicacion);


-- ========================================
-- TABLA: vacunas_mascota
-- Registro histórico de vacunas aplicadas a las mascotas.
-- Incluye vía, dosis, durabilidad, próxima dosis y colaborador responsable.
-- ========================================
CREATE TABLE IF NOT EXISTS vacunas_mascota (
	id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
	id_vacuna INT NOT NULL,
	id_mascota INT NOT NULL,
	id_via INT NOT NULL,
	dosis VARCHAR(32),
	fecha_aplicacion DATE NOT NULL,
	durabilidad_anios INT,
	proxima_dosis DATE NULL,
	id_colaborador INT NULL,
    id_veterinario INT NULL,
	observaciones VARCHAR(128),
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- ========================================
-- BLOQUE 03: agenda y servicios
-- gestiona los tipos de servicios, su registro,
-- citas programadas, visitas y recordatorios.
-- ========================================

-- ========================================
-- TABLA: canales_comunicacion
-- Define los canales de contacto utilizados.
-- Ejemplo: WHATSAPP, EMAIL, LLAMADA.
-- ========================================
CREATE TABLE IF NOT EXISTS canales_comunicacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: medios_pago
-- Enumera los medios de pago aceptados en ventas.
-- ========================================
CREATE TABLE IF NOT EXISTS medios_pago (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_agenda
-- Lista los posibles estados de una cita agendada.
-- Ejemplo: PENDIENTE, CONFIRMADA, CANCELADA, ATENDIDA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_recordatorio
-- Catálogo que define los tipos de recordatorios que pueden generarse 
-- en el sistema para eventos clínicos, estéticos o administrativos.
-- ========================================
CREATE TABLE tipo_recordatorio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: medio_solicitud
-- Enumera los medios por los que un cliente solicita una cita o servicio.
-- Ejemplo: TELÉFONO, WEB, PRESENCIAL.
-- ========================================
CREATE TABLE IF NOT EXISTS medio_solicitud (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_visita
-- Estados posibles de una visita física a la veterinaria.
-- Ejemplo: EN PROCESO, FINALIZADA, HOSPITALIZADO.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_visita (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_servicios
-- Catálogo de servicios que ofrece la veterinaria.
-- Ejemplo: CONSULTA GENERAL, VACUNACIÓN, BAÑO, CIRUGÍA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: ingresos_servicios
-- Registra los servicios realizados, su costo, responsable y observaciones.
-- Relaciona al colaborador o veterinario que prestó el servicio.
-- ========================================
CREATE TABLE IF NOT EXISTS ingresos_servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_servicio INT NOT NULL,
    id_colaborador INT,
    id_veterinario INT,
	cantidad INT CHECK (cantidad >= 0),
    duracion_min INT CHECK (duracion_min >= 0),
    adicionales VARCHAR(64),
    observaciones VARCHAR(64),
    precio_unitario DECIMAL(10,2) CHECK (precio_unitario >= 0),
    subtotal DECIMAL(10,2) CHECK (subtotal >= 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE ingresos_servicios
    ADD CONSTRAINT fk_ingreso_servicio FOREIGN KEY (id_servicio) REFERENCES tipo_servicios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ingreso_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ingreso_vet FOREIGN KEY (id_veterinario) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para acelerar las búsquedas por tipo de servicio
-- (consultas de todos los ingresos de un servicio específico).
CREATE INDEX idx_ingresos_servicios_servicio ON ingresos_servicios(id_servicio);

-- Índice para búsquedas rápidas por colaborador
-- (reportes de servicios atendidos por un colaborador).
CREATE INDEX idx_ingresos_servicios_colab ON ingresos_servicios(id_colaborador);

-- Índice para búsquedas rápidas por veterinario
-- (reportes de servicios atendidos por un veterinario).
CREATE INDEX idx_ingresos_servicios_vet ON ingresos_servicios(id_veterinario);

-- Índice compuesto para reportes de servicios
-- por colaborador y rango de fechas.
CREATE INDEX idx_ingresos_servicios_colab_fecha ON ingresos_servicios (id_colaborador, fecha_registro);

-- ========================================
-- TABLA: agenda
-- Agenda de citas programadas entre cliente, mascota y servicio.
-- Incluye fecha, hora, duración estimada en minutos y observaciones.
-- Ejemplo: Cita el 2025-08-01 a las 10:00am para consulta médica de la mascota "FIRULAIS".
-- ========================================
CREATE TABLE IF NOT EXISTS agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_cliente INT NOT NULL,
    id_mascota INT NOT NULL,
    id_tipo_servicio INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    duracion_estimada_min INT CHECK (duracion_estimada_min >= 0),
    id_estado INT NOT NULL, 
    observaciones VARCHAR(256),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE agenda
    ADD CONSTRAINT fk_agenda_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_servicio FOREIGN KEY (id_tipo_servicio) REFERENCES tipo_servicios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_agenda_estado FOREIGN KEY (id_estado) REFERENCES estado_agenda(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de citas por cliente.
CREATE INDEX idx_agenda_cliente ON agenda(id_cliente);

-- Índice para búsquedas rápidas de citas por mascota.
CREATE INDEX idx_agenda_mascota ON agenda(id_mascota);

-- Índice para filtrar o agrupar citas según el estado
-- (pendiente, atendida, cancelada, etc.).
CREATE INDEX idx_agenda_estado ON agenda(id_estado);

-- Índice compuesto para consultar las citas de un cliente
-- en un rango de fechas (consultas comunes en reportes y agenda).
CREATE INDEX idx_agenda_cliente_fecha ON agenda (id_cliente, fecha);

-- Índice para consultas directas por fecha de cita (cuando no filtra por cliente, sino por calendario).
CREATE INDEX idx_agenda_fecha ON agenda(fecha);


-- ========================================
-- TABLA: visitas_ingresos
-- Controla ingresos físicos de mascotas por hospitalización o servicios extendidos.
-- Incluye datos de ingreso, retiro, medio de solicitud, estado y monto abonado.
-- ========================================
CREATE TABLE IF NOT EXISTS visitas_ingresos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda INT NULL,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    id_mascota INT NOT NULL,
    id_ingreso_servicio INT NOT NULL,
    fecha_retiro TIMESTAMP NULL DEFAULT NULL ,
    id_medio_solicitud INT NOT NULL,
    id_estado INT NOT NULL,
    total DECIMAL(10,2) CHECK (total >= 0),
    CHECK (fecha_retiro IS NULL OR fecha_retiro >= fecha_ingreso),
    observaciones VARCHAR(255)
);
ALTER TABLE visitas_ingresos
    ADD CONSTRAINT fk_visita_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_visita_ingreso FOREIGN KEY (id_ingreso_servicio) REFERENCES ingresos_servicios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_visita_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_visita_medio FOREIGN KEY (id_medio_solicitud) REFERENCES medio_solicitud(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_visita_estado FOREIGN KEY (id_estado) REFERENCES estado_visita(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de visitas por mascota.
CREATE INDEX idx_visitas_mascota ON visitas_ingresos(id_mascota);

-- Índice para consultas rápidas por estado de la visita
-- (pendiente, en proceso, finalizada).
CREATE INDEX idx_visitas_estado ON visitas_ingresos(id_estado);

-- Índice compuesto para filtrar visitas por mascota y estado
-- (muy útil en listados de hospitalizaciones activas por mascota).
CREATE INDEX idx_visitas_mascota_estado ON visitas_ingresos (id_mascota, id_estado);

-- Índice para reportes o búsquedas de ingresos por fecha
-- (muy útil para filtrar visitas del día, semana o mes actual).
CREATE INDEX idx_visitas_fecha_ingreso ON visitas_ingresos(fecha_ingreso);

-- ========================================
-- TABLA: recordatorios_agenda
-- Registra recordatorios automáticos relacionados a citas de la agenda.
-- Incluye fecha, hora, mensaje personalizado y si ya fue enviado.
-- Ejemplo: Recordatorio de cita médica enviado al cliente un día antes.
-- ========================================
CREATE TABLE IF NOT EXISTS recordatorios_agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda INT NOT NULL,
    id_tipo_recordatorio INT NOT NULL DEFAULT 1,				-- 'AGENDA GENERAL'
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
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipos_archivo_clinico
-- Catálogo de tipos de archivos médicos que pueden asociarse a una historia clínica.
-- Ejemplo: RADIOGRAFÍA, ANÁLISIS DE SANGRE.
-- ========================================
CREATE TABLE IF NOT EXISTS tipos_archivo_clinico (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) UNIQUE NOT NULL,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: historia_clinica
-- Registra atenciones médicas realizadas a cada mascota.
-- Se vincula con la visita, servicio, colaborador y veterinario responsable.
-- ========================================
CREATE TABLE IF NOT EXISTS historia_clinica (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_mascota INT NOT NULL,
    id_colaborador INT NULL,
    id_veterinario INT NULL,
    id_visita INT NULL,
    motivo_consulta VARCHAR(128) NULL,
    diagnostico TEXT NULL,
    tratamiento TEXT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    descripcion VARCHAR(128),
    observaciones VARCHAR(128),
    fecha_registro_inicial TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_estado INT NULL
);
ALTER TABLE historia_clinica
    ADD CONSTRAINT fk_hist_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hist_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hist_vet FOREIGN KEY (id_veterinario) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_hist_visita FOREIGN KEY (id_visita) REFERENCES visitas_ingresos(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
	ADD CONSTRAINT fk_hist_estado FOREIGN KEY (id_estado) REFERENCES estado_historia_clinica(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de historias clínicas por mascota
-- (útil para listar todo el historial médico de una mascota específica).
CREATE INDEX idx_historia_clinica_mascota ON historia_clinica(id_mascota);

-- Índice para consultas que agrupan o filtran por veterinario responsable.
-- Muy útil si se generan reportes de actividades por veterinario.
CREATE INDEX idx_historia_clinica_veterinario ON historia_clinica(id_veterinario);

-- Índice para búsquedas rápidas de historias clínicas por visita
-- (facilita obtener todos los registros asociados a una hospitalización).
CREATE INDEX idx_historia_clinica_visita ON historia_clinica(id_visita);

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
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_historia_clinica INT NOT NULL,
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

-- ========================================
-- BLOQUE 05: productos e inventario
-- gestiona las marcas, categorías, productos,
-- control de stock, movimientos e integración
-- con facturas de compra.
-- ========================================

-- ========================================
-- TABLA: tipo_operacion
-- Clasifica el sentido lógico de un movimiento de inventario.
-- Se usa para diferenciar si un movimiento incrementa,
-- reduce o ajusta el stock, independientemente del motivo específico.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_operacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(16) NOT NULL UNIQUE, 					-- ENTRADA, SALIDA, AJUSTE
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_movimiento
-- Lista los tipos posibles de movimiento de productos en inventario.
-- Ejemplo: INGRESO POR COMPRA, SALIDA POR VENTA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_movimiento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    id_tipo_operacion INT NOT NULL,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE tipo_movimiento
ADD CONSTRAINT fk_tipo_operacion FOREIGN KEY (id_tipo_operacion) REFERENCES tipo_operacion(id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- ========================================
-- TABLA: estado_factura_compra
-- Enumera los distintos estados de una factura de compra.
-- Ejemplo: PENDIENTE, PAGADA, ANULADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_factura_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: almacenes
-- Lista las ubicaciones físicas disponibles para almacenamiento de productos.
-- Ejemplo: BODEGA CENTRAL, SUCURSAL MAGDALENA.
-- ========================================
CREATE TABLE IF NOT EXISTS almacenes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: marcas
-- Registra las marcas de los productos disponibles.
-- ========================================
CREATE TABLE IF NOT EXISTS marcas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: presentaciones
-- Define las formas físicas en que se presentan los productos.
-- Ejemplo: TABLETAS, SOLUCIÓN ORAL, SPRAY.
-- ========================================
CREATE TABLE IF NOT EXISTS presentaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: categorias_productos
-- Agrupa los productos según su tipo o función.
-- Ejemplo: MEDICAMENTOS, ACCESORIOS, ALIMENTOS.
-- ========================================
CREATE TABLE IF NOT EXISTS categorias_productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: productos
-- Contiene los datos completos de cada producto, incluyendo precio, presentación, marca y categoría.
-- ========================================
CREATE TABLE IF NOT EXISTS productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL,
    descripcion VARCHAR(64),
    referencia VARCHAR(64),
    id_presentacion INT NOT NULL,
    id_marca INT NOT NULL,
    precio_compra_base DECIMAL(10,2) CHECK (precio_compra_base >= 0),
    precio_sugerido_venta DECIMAL(10,2) CHECK (precio_sugerido_venta >= 0),
    precio_venta DECIMAL(10,2) CHECK (precio_venta >= 0) NOT NULL,
    id_categoria_producto INT NOT NULL,
    id_medicamento INT,
    fecha_vencimiento DATE NULL,
    controla_vencimiento TINYINT NOT NULL DEFAULT 0 CHECK (controla_vencimiento IN (0,1)),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE productos 
	ADD CONSTRAINT uq_prod_nombre_marca_pres UNIQUE(nombre, id_marca, id_presentacion),
    ADD CONSTRAINT fk_producto_presentacion FOREIGN KEY (id_presentacion) REFERENCES presentaciones(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_marca FOREIGN KEY (id_marca) REFERENCES marcas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria_producto) REFERENCES categorias_productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de productos por categoría
-- (útil al listar productos filtrados por tipo).
CREATE INDEX idx_productos_categoria ON productos(id_categoria_producto);

-- Índice para búsquedas rápidas de productos por nombre
-- (útil para filtros, autocompletado o búsqueda parcial de productos).
CREATE INDEX idx_productos_nombre ON productos(nombre);

-- Índice para búsquedas rápidas de productos por marca
-- (permite filtrar inventarios por fabricante).
CREATE INDEX idx_productos_marca ON productos(id_marca);

-- Índice para búsquedas rápidas de productos que son medicamentos
-- (permite separar insumos médicos de otros productos).
CREATE INDEX idx_productos_medicamento ON productos(id_medicamento);



-- ========================================
-- TABLA: facturas_compras
-- Registra todas las facturas de compra ingresadas al sistema, con proveedor, usuario y estado.
-- ========================================
CREATE TABLE IF NOT EXISTS facturas_compras (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    numero_factura VARCHAR(64) NOT NULL,
    fecha_factura DATE NOT NULL,
    total DECIMAL(12,2) CHECK (total >= 0) NOT NULL ,
    observaciones VARCHAR(64),
    id_estado INT DEFAULT 1
);
ALTER TABLE facturas_compras
    ADD CONSTRAINT fk_factura_compra_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_compra_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_compra_estado FOREIGN KEY (id_estado) REFERENCES estado_factura_compra(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de facturas por proveedor
-- (muy usado en reportes y conciliaciones).
CREATE INDEX idx_facturas_compras_proveedor ON facturas_compras(id_proveedor);

-- Índice para búsquedas de facturas por usuario (quién registró)
CREATE INDEX idx_facturas_compras_usuario ON facturas_compras(id_usuario);

-- Índice para consultas eficientes por fecha de emisión de factura
-- (muy útil en reportes de compras por rango de fechas).
CREATE INDEX idx_facturas_compras_fec_fac ON facturas_compras(fecha_factura);

-- ========================================
-- TABLA: detalle_factura_compra
-- Detalla los productos y cantidades contenidos en una factura de compra.
-- Ejemplo: 3 unidades de "Ivermectina 10ml" a S/ 15.00 cada una, subtotal: S/ 45.00.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_factura_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED
);
ALTER TABLE detalle_factura_compra
    ADD CONSTRAINT fk_detalle_factura_factura FOREIGN KEY (id_factura) REFERENCES facturas_compras(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detalle_factura_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para obtener rápidamente los detalles de una factura
CREATE INDEX idx_detalle_fact_compra_factura ON detalle_factura_compra(id_factura);

-- Índice para consultas de facturas por producto
-- (permite conocer en qué facturas se ha comprado un producto específico).
CREATE INDEX idx_detalle_fact_compra_producto ON detalle_factura_compra(id_producto);

-- ========================================
-- TABLA: inventarios
-- Gestiona el stock actual por producto y almacén, incluyendo lote y vencimiento.
-- ========================================
CREATE TABLE IF NOT EXISTS inventarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_producto INT NOT NULL,
    id_almacen INT NOT NULL,
    stock_actual INT CHECK (stock_actual >= 0) NOT NULL,
    stock_minimo INT CHECK (stock_minimo >= 0) NOT NULL,
    fecha_actualizacion DATE NOT NULL,
    lote VARCHAR(32),
    fecha_vencimiento DATE DEFAULT NULL
);
ALTER TABLE inventarios 
    ADD CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_inventario_almacen FOREIGN KEY (id_almacen) REFERENCES almacenes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de inventario por producto
CREATE INDEX idx_inventarios_producto ON inventarios(id_producto);

-- Índice para búsquedas rápidas de inventario por almacén
CREATE INDEX idx_inventarios_almacen ON inventarios(id_almacen);

-- Índice compuesto para consultas frecuentes de inventarios por producto y almacén
CREATE INDEX idx_inventarios_producto_almacen ON inventarios(id_producto, id_almacen);

-- ========================================
-- TABLA: movimientos_inventario
-- Registra todos los movimientos de inventario realizados (entradas, salidas, ajustes).
-- ========================================
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_producto INT NOT NULL,
    id_usuario INT NOT NULL,
    id_tipo_movimiento INT NOT NULL,
    cantidad INT CHECK (cantidad >= 0) NOT NULL ,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NULL,
    observaciones VARCHAR(64),
    id_factura_compra INT DEFAULT NULL
);
ALTER TABLE movimientos_inventario
    ADD CONSTRAINT fk_movimiento_inventario_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_tipo FOREIGN KEY (id_tipo_movimiento) REFERENCES tipo_movimiento(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_factura FOREIGN KEY (id_factura_compra) REFERENCES facturas_compras(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para consultas rápidas de movimientos de inventario por producto
CREATE INDEX idx_movimientos_producto ON movimientos_inventario(id_producto);

-- Índice para búsquedas rápidas de movimientos por usuario
CREATE INDEX idx_movimientos_usuario ON movimientos_inventario(id_usuario);

-- Índice para búsquedas rápidas de movimientos por tipo de movimiento
CREATE INDEX idx_movimientos_tipo ON movimientos_inventario(id_tipo_movimiento);

-- Índice compuesto para consultas de movimientos asociados a facturas de compra
CREATE INDEX idx_movimientos_factura ON movimientos_inventario(id_factura_compra);

-- Índice para consultas cronológicas de movimientos de inventario
-- (útil en reportes históricos o análisis por fechas).
CREATE INDEX idx_movimientos_fecha ON movimientos_inventario(fecha_movimiento);

-- Índice compuesto para auditorías por usuario y fecha de movimiento
-- (acelera consultas para saber qué hizo un usuario en un periodo determinado).
CREATE INDEX idx_movimientos_usuario_fecha ON movimientos_inventario(id_usuario, fecha_movimiento);

-- ========================================
-- BLOQUE 06: ventas y caja
-- gestiona facturación, medios de pago, control de caja,
-- arqueos, pagos de clientes y notas de crédito.
-- ========================================

-- ========================================
-- TABLA: tipo_documento_venta
-- Define el tipo de documento emitido a clientes.
-- Ejemplo: FACTURA, BOLETA, NOTA DE VENTA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_documento_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_item_factura
-- Clasifica ítems en la factura como productos o servicios.
-- Ejemplo: PRODUCTO, SERVICIO, OTRO.
-- ========================================
CREATE TABLE tipo_item_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) UNIQUE NOT NULL,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_nota_credito
-- Indica el estado actual de una nota de crédito.
-- Ejemplo: ACTIVA, ANULADA.
-- ========================================
CREATE TABLE estado_nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_nota_credito
-- Describe el tipo o motivo general de una nota de crédito.
-- Ejemplo: DEVOLUCIÓN, ERROR DE FACTURACIÓN.
-- ========================================
CREATE TABLE tipo_nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_movimiento_caja
-- Lista los tipos de movimientos registrados en la caja.
-- Ejemplo: INGRESO, EGRESO, AJUSTE.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_movimiento_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_factura_venta
-- Representa los posibles estados de una factura emitida.
-- Ejemplo: EMITIDA, PAGADA, ANULADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_factura_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_caja
-- Indica el estado general de la caja diaria.
-- Ejemplo: ABIERTA, CERRADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: caja_general
-- Controla ingresos y egresos registrados en la caja diaria.
-- ========================================
CREATE TABLE IF NOT EXISTS caja_general (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    tipo_movimiento INT NOT NULL,
    descripcion VARCHAR(128),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    saldo_inicial DECIMAL(10,2),
    saldo_final DECIMAL(10,2) DEFAULT 0,
    id_usuario INT NOT NULL,
    observaciones TEXT
);

ALTER TABLE caja_general
    ADD CONSTRAINT fk_caja_tipo FOREIGN KEY (tipo_movimiento) REFERENCES tipo_movimiento_caja(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_caja_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: mejora reportes y listados por fecha
CREATE INDEX idx_caja_fecha ON caja_general(fecha);

-- Índice: facilita consultas filtradas por usuario
CREATE INDEX idx_caja_usuario ON caja_general(id_usuario);

-- ========================================
-- TABLA: pagos_clientes
-- Registra pagos realizados por clientes, asociados o no a facturas.
-- ========================================
CREATE TABLE IF NOT EXISTS pagos_clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_usuario INT NOT NULL,
    id_cliente INT NOT NULL,
    id_visita_ingreso INT NULL,
    id_caja_general INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    id_medio_pago INT,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(64)
);
ALTER TABLE pagos_clientes
    ADD CONSTRAINT fk_pago_clientes_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_clientes_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_clientes_caja FOREIGN KEY (id_caja_general) REFERENCES caja_general(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_pago_visita FOREIGN KEY (id_visita_ingreso) REFERENCES visitas_ingresos(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_cliente_medio FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: agiliza búsquedas por cliente
CREATE INDEX idx_pagos_cliente ON pagos_clientes(id_cliente);

-- Índice: permite filtrar por usuario que registró el pago
CREATE INDEX idx_pagos_usuario ON pagos_clientes(id_usuario);

-- Índice: optimiza reportes de pagos por fecha
CREATE INDEX idx_pagos_fecha ON pagos_clientes(fecha_pago);

-- ========================================
-- TABLA: arqueo_caja
-- Realiza el cierre diario de caja, validando saldos y registrando diferencias.
-- ========================================
CREATE TABLE IF NOT EXISTS arqueo_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    id_usuario INT NOT NULL,
    saldo_apertura DECIMAL(10,2),
    total_ingresos DECIMAL(10,2) CHECK (total_ingresos >= 0),
    total_egresos DECIMAL(10,2) CHECK (total_egresos >= 0),
    saldo_calculado DECIMAL(10,2),
    saldo_declarado DECIMAL(10,2),
    diferencia DECIMAL(10,2),
    referencia_pago_cliente INT,
    id_estado_caja INT NOT NULL DEFAULT 1,
    observaciones TEXT
);

ALTER TABLE arqueo_caja
    ADD CONSTRAINT fk_estado_caja FOREIGN KEY (id_estado_caja) REFERENCES estado_caja(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_arqueo_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_referencia_pago_cliente FOREIGN KEY (referencia_pago_cliente) REFERENCES pagos_clientes(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: mejora consultas por fecha de arqueo
CREATE INDEX idx_arqueo_fecha ON arqueo_caja(fecha);

-- Índice: optimiza búsquedas filtradas por usuario
CREATE INDEX idx_arqueo_usuario ON arqueo_caja(id_usuario);

-- Índice: agiliza consultas por estado de caja (ej. abiertos o cerrados)
CREATE INDEX idx_arqueo_estado ON arqueo_caja(id_estado_caja);

-- ========================================
-- TABLA: facturas_venta
-- Registra facturas emitidas a clientes por productos o servicios.
-- ========================================
CREATE TABLE IF NOT EXISTS facturas_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_tipo_documento INT NOT NULL,
    fecha_factura DATE NOT NULL,
    id_usuario INT NOT NULL,
    id_cliente INT NOT NULL,
    id_medio_pago INT NOT NULL,
    total_pagado DECIMAL(10,2) DEFAULT 0 CHECK (total_pagado >= 0)  DEFAULT 0,
    subtotal DECIMAL(10,2) CHECK (subtotal >= 0) DEFAULT 0,
    impuestos DECIMAL(10,2) CHECK (impuestos >= 0) DEFAULT 0,
    descuentos DECIMAL(10,2) CHECK (descuentos >= 0) DEFAULT 0,
    total_factura DECIMAL(10,2) CHECK (total_factura >= 0) DEFAULT 0,
    saldo_pendiente DECIMAL(10,2) CHECK (saldo_pendiente >= 0) DEFAULT 0 ,
    id_estado INT NOT NULL DEFAULT 1,
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE facturas_venta
    ADD CONSTRAINT fk_factura_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_pago FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_estado FOREIGN KEY (id_estado) REFERENCES estado_factura_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: mejora las consultas filtradas por cliente
CREATE INDEX idx_facturas_cliente ON facturas_venta(id_cliente);

-- Índice: permite filtrar por el usuario que emitió la factura
CREATE INDEX idx_facturas_usuario ON facturas_venta(id_usuario);

-- Índice: optimiza las búsquedas por estado de la factura
CREATE INDEX idx_facturas_estado ON facturas_venta(id_estado);

-- Índice: facilita listados y ordenación por fecha
CREATE INDEX idx_facturas_fecha ON facturas_venta(fecha_factura);

-- ========================================
-- TABLA: detalle_factura
-- Detalla los productos o servicios incluidos en cada factura.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_tipo_item INT NOT NULL,
    id_producto INT,
    id_visita_ingreso INT,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) CHECK (precio_unitario >= 0) NOT NULL,
    total_item DECIMAL(10,2) CHECK (total_item >= 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE detalle_factura
    ADD CONSTRAINT fk_detalle_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_tipo_item_factura FOREIGN KEY (id_tipo_item) REFERENCES tipo_item_factura(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detfac_visita FOREIGN KEY (id_visita_ingreso) REFERENCES visitas_ingresos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: recupera rápidamente todos los ítems de una factura
CREATE INDEX idx_detalle_fact_vta_factura ON detalle_factura(id_factura);

-- Índice: permite consultar en qué facturas se vendió un producto específico
CREATE INDEX idx_detalle_fact_vta_producto ON detalle_factura(id_producto);


-- ========================================
-- TABLA: detalle_pagos_factura
-- Asocia montos pagados por clientes a facturas específicas.
-- Ejemplo: Pago de S/ 120.00 aplicado a factura FV-0341.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_pagos_factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_pago INT NOT NULL,
    monto_asignado DECIMAL(10,2) NOT NULL CHECK (monto_asignado > 0)
);

ALTER TABLE detalle_pagos_factura
    ADD CONSTRAINT fk_det_pago_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_det_pago_clientes FOREIGN KEY (id_pago) REFERENCES pagos_clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: permite obtener pagos asignados a una factura
CREATE INDEX idx_detalle_pago_factura ON detalle_pagos_factura(id_factura);

-- Índice: agiliza consultas de facturas asociadas a un pago
CREATE INDEX idx_detalle_pago_pago ON detalle_pagos_factura(id_pago);

-- ========================================
-- TABLA: nota_credito
-- Emite notas de crédito relacionadas a facturas de venta.
-- ========================================
CREATE TABLE IF NOT EXISTS nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_usuario INT NOT NULL,
    id_factura INT NOT NULL,
    id_estado INT NOT NULL DEFAULT 1,
    id_tipo INT NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(128),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE nota_credito
    ADD CONSTRAINT fk_nota_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_nota_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_estado_nota_credito FOREIGN KEY (id_estado) REFERENCES estado_nota_credito(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_tipo_nota_credito FOREIGN KEY (id_tipo) REFERENCES tipo_nota_credito(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: optimiza consultas de notas por factura
CREATE INDEX idx_nota_factura ON nota_credito(id_factura);

-- Índice: permite listar notas emitidas por usuario
CREATE INDEX idx_nota_usuario ON nota_credito(id_usuario);

-- Índice: mejora reportes y listados por fecha de emisión de la nota de crédito
CREATE INDEX idx_nota_fecha ON nota_credito(fecha);

-- ========================================
-- BLOQUE 07: comunicación con clientes
-- Gestiona los canales de contacto y los mensajes enviados
-- a clientes, incluyendo su estado y trazabilidad.
-- ========================================

-- ========================================
-- TABLA: estado_mensaje_cliente
-- Lista los estados posibles de los mensajes enviados a clientes.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_mensaje_cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: mensajes_cliente
-- Guarda los mensajes enviados a clientes, con información del canal utilizado,
-- estado del mensaje, contenido, respuesta del cliente y colaborador que lo gestionó.
-- ========================================
CREATE TABLE IF NOT EXISTS mensajes_cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_cliente INT NOT NULL,
    id_canal INT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME DEFAULT CURRENT_TIMESTAMP,
    asunto VARCHAR(64),
    mensaje TEXT,
    respuesta TEXT,
    id_estado INT NOT NULL,
    id_colaborador INT NULL
);

ALTER TABLE mensajes_cliente 
    ADD CONSTRAINT fk_mensaje_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_estado FOREIGN KEY (id_estado) REFERENCES estado_mensaje_cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_canal FOREIGN KEY (id_canal) REFERENCES canales_comunicacion(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: mejora las consultas por cliente
CREATE INDEX idx_mensajes_cliente_cliente ON mensajes_cliente(id_cliente);

-- Índice: optimiza búsquedas filtradas por canal
CREATE INDEX idx_mensajes_cliente_canal ON mensajes_cliente(id_canal);

-- Índice: permite reportes por estado de mensaje
CREATE INDEX idx_mensajes_cliente_estado ON mensajes_cliente(id_estado);

-- Índice: agiliza consultas filtradas por colaborador
CREATE INDEX idx_mensajes_cliente_colaborador ON mensajes_cliente(id_colaborador);

-- Índice: facilita listados por fecha de envío
CREATE INDEX idx_mensajes_cliente_fecha ON mensajes_cliente(fecha_envio);

show tables;