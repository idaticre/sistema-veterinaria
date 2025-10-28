-- BLOQUE 03: agenda y servicios
-- gestiona los tipos de servicios, su registro,
-- citas programadas, visitas y recordatorios.
-- ========================================
USE vet_manada_woof;
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
INSERT INTO canales_comunicacion (nombre) VALUES
('WHATSAPP'),
('EMAIL'),
('LLAMADA TELEFÓNICA'),
('SMS'),
('REDES SOCIALES'),
('MOSTRADOR');

-- ========================================
-- TABLA: medios_pago
-- Enumera los medios de pago aceptados en ventas.
-- ========================================
CREATE TABLE IF NOT EXISTS medios_pago (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
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
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
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
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
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
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
INSERT INTO medio_solicitud (nombre, descripcion) VALUES
('TELÉFONO', 'Solicitud realizada por llamada telefónica.'),
('WHATSAPP', 'Solicitud recibida mediante mensaje de WhatsApp.'),
('WEB', 'Solicitud enviada a través del sitio web.'),
('PRESENCIAL', 'Solicitud directa en mostrador o recepción.'),
('REDES SOCIALES', 'Solicitud por mensaje de Facebook, Instagram u otra red.');

-- ========================================
-- TABLA: estado_visita
-- Estados posibles de una visita física a la veterinaria.
-- Ejemplo: EN PROCESO, FINALIZADA, HOSPITALIZADO.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_visita (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
INSERT INTO estado_visita (nombre, descripcion) VALUES
('EN PROCESO', 'La visita está en curso o la mascota está siendo atendida.'),
('FINALIZADA', 'Visita completada y registrada.'),
('HOSPITALIZADO', 'Mascota internada bajo observación o tratamiento.'),
('DERIVADA', 'Visita derivada a otra especialidad o veterinario.'),
('CANCELADA', 'Visita anulada o no realizada.');

-- ========================================
-- TABLA: tipo_servicios
-- Catálogo de servicios que ofrece la veterinaria.
-- Ejemplo: CONSULTA GENERAL, VACUNACIÓN, BAÑO, CIRUGÍA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
INSERT INTO tipo_servicios (nombre, descripcion) VALUES
-- ÁREA MÉDICA
('CONSULTA GENERAL', 'Evaluación médica completa para diagnóstico y orientación del tratamiento.'),
('CONSULTA ESPECIALIZADA', 'Atención por veterinario especialista en dermatología, oftalmología, odontología, etc.'),
('VACUNACIÓN', 'Aplicación de vacunas preventivas según plan sanitario.'),
('DESPARASITACIÓN', 'Administración de antiparasitarios internos o externos según protocolo.'),
('CONTROL POST-OPERACIÓN', 'Revisión y seguimiento posterior a una intervención quirúrgica.'),
('CIRUGÍA', 'Procedimientos quirúrgicos programados o de emergencia.'),
('ECOGRAFÍA', 'Diagnóstico por imagen mediante ultrasonido.'),
('RADIOGRAFÍA', 'Diagnóstico por imagen mediante rayos X.'),
('ANÁLISIS DE LABORATORIO', 'Exámenes clínicos, hematológicos y bioquímicos.'),
('URGENCIAS', 'Atención médica inmediata por accidente o enfermedad repentina.'),
('HOSPITALIZACIÓN', 'Cuidados médicos continuos y observación de pacientes internados.'),

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

-- ÁREA ADIESTRAMIENTO Y COMPORTAMIENTO
('ADIESTRAMIENTO BÁSICO', 'Entrenamiento en obediencia y socialización.'),
('ADIESTRAMIENTO AVANZADO', 'Entrenamiento de conducta, control y refuerzo positivo.'),
('MODIFICACIÓN DE CONDUCTA', 'Tratamiento de problemas de comportamiento o ansiedad.'),

-- ÁREA DE BIENESTAR Y PREVENCIÓN
('PLAN DE SALUD ANUAL', 'Programa preventivo con controles, vacunas y beneficios especiales.'),
('CONTROL DE PESO', 'Evaluación nutricional y seguimiento de peso saludable.'),
('ASESORÍA NUTRICIONAL', 'Recomendaciones de dieta y suplementos según cada mascota.'),

-- SERVICIOS COMPLEMENTARIOS
('VENTA DE PRODUCTOS', 'Adquisición de alimentos, accesorios, medicamentos o juguetes.'),
('RECOJO A DOMICILIO', 'Transporte seguro de la mascota desde o hacia la veterinaria.'),
('SERVICIO A DOMICILIO', 'Consulta o atención médica veterinaria en el hogar.'),
('FOTOGRAFÍA DE MASCOTAS', 'Sesión profesional de fotos para mascotas.'),
('CREMACIÓN Y DESPEDIDA', 'Servicio respetuoso de cremación y ceremonia de despedida.'),
('ASESORÍA EN ADOPCIÓN', 'Orientación en adopción responsable y seguimiento post-adopción.');


-- ========================================
-- TABLA: ingresos_servicios
-- Registra los servicios realizados, su costo, responsable y observaciones.
-- Relaciona al colaborador o veterinario que prestó el servicio.
-- ========================================
CREATE TABLE IF NOT EXISTS ingresos_servicios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_servicio INT NOT NULL,
    id_colaborador BIGINT,
    id_veterinario BIGINT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_cliente BIGINT NOT NULL,
    id_mascota BIGINT NOT NULL,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda BIGINT NULL,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    id_mascota BIGINT NOT NULL,
    id_ingreso_servicio BIGINT NOT NULL,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_agenda BIGINT NOT NULL,
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