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