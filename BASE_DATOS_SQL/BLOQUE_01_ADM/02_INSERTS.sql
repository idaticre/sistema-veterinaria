-- PARTE DE INSERTS
-- BLOQUE 01 ADM
-- Estos insert son muestras, lo ideal es mediante la programaciÓn ingresar esta informaciÓn,
-- es decir llamar a los respectivos procedimientos

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
-- TIPO DE NATURALEZA LEGAL DE LA ENTIDAD
-- Clasifica si la entidad es de tipo NATURAL (persona física) o JURÍDICA (empresa o institución con RUC propio).
-- ========================================
INSERT INTO tipo_persona_juridica (nombre, descripcion) VALUES
('NATURAL', 'Persona natural que representa una entidad individual'),
('JURIDICA', 'Entidad jurídica con existencia legal y RUC propio');

-- ========================================
-- ROLES DEL SISTEMA
-- ========================================
INSERT INTO roles (nombre, descripcion, activo) VALUES
('ADMINISTRADOR GENERAL', NULL, 1),
('ADMINISTRADOR G 2', NULL, 1),
('AUXILIAR CAJA', NULL, 1),
('AUXILIAR GROMERS', NULL, 1);

-- ========================================
-- TIPO DE ENTIDAD
-- ========================================
INSERT INTO tipo_entidad (nombre) VALUES 
('CLIENTE'),         -- Persona o empresa que recibe servicios veterinarios.
('PROVEEDOR'),       -- Entidad que suministra productos o servicios a la veterinaria.
('COLABORADOR');     -- Personal interno que trabaja en la organización (incluye veterinarios).

-- ========================================
-- ESPECIALIDADES VETERINARIAS
-- ========================================
INSERT INTO especialidades (nombre) VALUES
('MEDICINA GENERAL'), ('CIRUGÍA'), ('DERMATOLOGÍA'), ('OFTALMOLOGÍA'), ('TRAUMATOLOGÍA'),
('CARDIOLOGÍA'), ('ODONTOLOGÍA VETERINARIA'), ('ONCOLOGÍA'), ('NEUROLOGÍA'),
('ANESTESIOLOGÍA'), ('EMERGENCIAS Y CUIDADOS CRÍTICOS'), ('REHABILITACIÓN Y FISIOTERAPIA'),
('ETOLOGÍA Y COMPORTAMIENTO ANIMAL');

-- ========================================
-- DÍAS DE LA SEMANA
-- ========================================
INSERT INTO dias_semana (nombre) VALUES 
('LUNES'), ('MARTES'), ('MIÉRCOLES'), ('JUEVES'), ('VIERNES'), ('SÁBADO'), ('DOMINGO');

-- ========================================
-- TIPOS DE DÍA
-- ========================================
INSERT INTO tipos_dia (nombre) VALUES 
('FERIADO'), ('LABORAL'), ('DÍA PUENTE'), ('DÍA NO LABORABLE');
