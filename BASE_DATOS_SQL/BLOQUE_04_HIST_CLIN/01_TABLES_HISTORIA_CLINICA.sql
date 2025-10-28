-- ========================================
-- BLOQUE 04: HISTORIA CLÍNICA Y ARCHIVOS ASOCIADOS
-- Gestiona el registro de atenciones médicas de cada mascota,
-- vinculando la información clínica con los ingresos/visitas
-- y permitiendo almacenar archivos relacionados.
-- ========================================
USE vet_manada_woof;
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
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
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
    id_visita BIGINT NULL,
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
