
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
-- Enumera los medios de pago aceptados en ventas.
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
    adicionales VARCHAR(64),
    observaciones VARCHAR(64),
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

