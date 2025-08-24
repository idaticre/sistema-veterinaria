-- PARTE DE INSERTS
-- BLOQUE 02 MASCOTAS
-- Estos insert son muestras, lo ideal es mediante la programación ingresar esta información,
-- es decir llamar a los respectivos procedimientos
-- ========================================
-- ESPECIES
-- ========================================
INSERT INTO especies (nombre) VALUES 
('CANINO'), ('FELINO'), ('CONEJO');

-- ========================================
-- RAZAS
-- ========================================
INSERT INTO razas (id_especie, nombre) VALUES
(1, 'LABRADOR RETRIEVER'), (1, 'BULLDOG FRANCÉS'), (1, 'PASTOR ALEMÁN'), (1, 'PUG'),
(1, 'GOLDEN RETRIEVER'), (1, 'CHIHUAHUA'), (1, 'ROTTWEILER'), (1, 'BEAGLE'),
(1, 'DOBERMAN'), (1, 'SHIH TZU');

-- ========================================
-- TAMAÑOS
-- ========================================
INSERT INTO tamanos (talla_equivalente, descripcion) VALUES
('XS','MUY PEQUEÑO'), ('S','PEQUEÑO'), ('M','MEDIANO'), ('L','GRANDE'), ('XL','MUY GRANDE');

-- ========================================
-- ETAPAS DE VIDA
-- ========================================
INSERT INTO etapas_vida (descripcion) VALUES
('CACHORRO'), ('JOVEN'), ('ADULTO'), ('SENIOR');

-- ========================================
-- VACUNAS (CANINOS)
-- ========================================
INSERT INTO vacunas (nombre, id_especie, descripcion) VALUES
('RABIA', 1, 'Vacuna anual contra la rabia.'),
('MOQUILLO', 1, 'Protección contra el virus del moquillo canino.'),
('PARVOVIRUS', 1, 'Prevención de infecciones por parvovirus.'),
('TRIPLE CANINA', 1, 'Moquillo, hepatitis y parvovirus.'),
('TOS DE LAS PERRERAS', 1, 'Vacuna contra Bordetella bronchiseptica.');

-- ========================================
-- VACUNAS (FELINOS)
-- ========================================
INSERT INTO vacunas (nombre, id_especie, descripcion) VALUES
('RABIA', 2, 'Vacuna obligatoria contra la rabia felina.'),
('TRIPLE FELINA', 2, 'Calicivirus, herpesvirus y panleucopenia felina.'),
('LEUCEMIA FELINA', 2, 'Protección contra el virus de la leucemia felina.');

-- ========================================
-- TIPOS DE MEDICAMENTOS
-- ========================================
INSERT INTO medicamento_tipo (nombre, descripcion) VALUES
('ANTIBIÓTICO', 'Medicamentos que combaten infecciones bacterianas.'),
('ANTIINFLAMATORIO', 'Disminuyen la inflamación y el dolor.'),
('DESPARASITANTE', 'Eliminan parásitos internos o externos.'),
('ANTIFÚNGICO', 'Tratamiento contra infecciones por hongos.'),
('ANALGÉSICO', 'Medicamentos para aliviar el dolor.'),
('OTRO', 'Otros tipos de medicamentos.');

-- ========================================
-- VÍAS DE APLICACIÓN
-- ========================================
INSERT INTO vias_aplicacion (nombre) VALUES 
('ORAL'), ('TÓPICA'), ('SUBCUTÁNEA'), ('INTRAMUSCULAR'), ('INTRAVENOSA'), ('OTRA');

-- ========================================
-- MEDICAMENTOS
-- ========================================
INSERT INTO medicamentos (nombre, id_tipo, descripcion) VALUES
('AMOXICILINA 500MG', 1, 'Antibiótico de amplio espectro para infecciones bacterianas.'),
('KETOPROFENO 100MG', 2, 'Antiinflamatorio no esteroideo utilizado en procesos inflamatorios y dolor.'),
('ALBENDAZOL 10%', 3, 'Desparasitante oral de amplio espectro para uso veterinario.'),
('CLOTRIMAZOL SPRAY', 4, 'Antifúngico tópico para tratamiento de micosis cutáneas.'),
('TRAMADOL 50MG', 5, 'Analgésico opiáceo utilizado en manejo del dolor moderado a severo.'),
('MULTIVITAMÍNICO PETS', 6, 'Suplemento nutricional multivitamínico para perros y gatos.');

-- ========================================
-- ESTADO DE LAS MASCOTAS
-- ========================================
INSERT INTO estado_mascota (nombre, descripcion) VALUES
('ACTIVA', 'Mascota activa con atención vigente'),
('EN TRATAMIENTO', 'Mascota con tratamiento en curso'),
('FALLECIDA', 'Mascota registrada como fallecida'),
('ADOPTADA', 'Mascota entregada en adopción');