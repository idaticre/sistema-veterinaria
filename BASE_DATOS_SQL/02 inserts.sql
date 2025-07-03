
-- INSERTAR TIPOS DE SERVICIOS
INSERT INTO tipo_servicios (nombre, descripcion) VALUES
  ('Baño', 'Servicio de baño para mascotas'),
  ('Peluquería', 'Corte y peinado de pelaje'),
  ('Spa', 'Relajación e higiene avanzada'),
  ('Grooming', 'Limpieza estética general'),
  ('Guardería', 'Cuidado diario por horas o días'),
  ('Hospedaje', 'Estadía prolongada de mascotas en la veterinaria'),
  ('Hospitalización', 'Internamiento por tratamiento médico o postoperatorio'),
  ('Consulta veterinaria', 'Revisión médica general'),
  ('Vacunación', 'Aplicación de vacunas programadas');
  
  -- INSERTAR MEDIOS DE PAGO
INSERT INTO medios_pago (nombre) VALUES 
('Efectivo'),
('Tarjeta de Crédito'),
('Tarjeta de Débito'),
('Yape'),
('Plin'),
('Transferencia Bancaria'),
('POS'),
('QR Dinámico'),
('Cheque'),
('Cortesía');

-- INSERTAR CANALES
INSERT INTO canales_comunicacion (nombre) VALUES
('WhatsApp'),('Email'),('Llamada'),('SMS'),('Facebook Messenger'),('Instagram DM');

-- INSERTAR ESPECIALIDADES
INSERT INTO especialidades (nombre) VALUES
('Medicina General'),
('Cirugía'),
('Dermatología'),
('Odontología'),
('Oftalmología'),
('Cardiología'),
('Oncología'),
('Neurología'),
('Rehabilitación'),
('Urgencias'),
('Anestesiología');
