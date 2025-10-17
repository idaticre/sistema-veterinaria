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
    ubicacion_espacio VARCHAR(10),
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