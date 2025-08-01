-- ========================================
-- EMPRESA
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
('NATURAL'),
('JURIDICA');

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

-- ========================================
-- CANALES DE COMUNICACIÓN
-- ========================================
INSERT INTO canales_comunicacion (nombre) VALUES
('WHATSAPP'), ('EMAIL'), ('LLAMADA'), ('SMS'), ('FACEBOOK MESSENGER'), ('INSTAGRAM DM');

-- ========================================
-- MEDIOS DE PAGO
-- ========================================
INSERT INTO medios_pago (nombre) VALUES 
('EFECTIVO'), ('TARJETA DE CRÉDITO'), ('TARJETA DE DÉBITO'),
('YAPE'), ('PLIN'), ('TRANSFERENCIA BANCARIA'),
('POS QR'), ('CHEQUE'), ('CORTESÍA');

-- ========================================
-- ESTADOS DE LA AGENDA
-- ========================================
INSERT INTO estado_agenda (nombre, descripcion) VALUES
('PENDIENTE', 'Cita registrada pendiente de confirmación'),
('CONFIRMADO', 'Cita confirmada por el cliente'),
('CANCELADO', 'Cita cancelada'),
('ATENDIDO', 'Cita realizada y atendida');

-- ========================================
-- TIPOS DE RECORDATORIOS
-- ========================================
INSERT INTO tipo_recordatorio (nombre, descripcion) VALUES
('AGENDA GENERAL', 'Agenda no específica'),
('VACUNACIÓN', 'Recordatorio de próxima vacuna'),
('DESPARASITACIÓN', 'Recordatorio de desparasitación programada'),
('BAÑO', 'Recordatorio para baño programado'),
('SPA', 'Servicio de estética programado'),
('CITA CONTROL', 'Control post operatorio o revisión'),
('HOSPITALIZACIÓN', 'Alta o revisión tras hospitalización');

-- ========================================
-- MEDIOS DE SOLICITUD
-- ========================================
INSERT INTO medio_solicitud (nombre, descripcion) VALUES
('WEB', 'Solicitud realizada a través del sitio web'),
('PRESENCIAL', 'Solicitud hecha en persona en la clínica'),
('LLAMADA', 'Solicitud vía llamada telefónica'),
('WHATSAPP', 'Solicitud a través de WhatsApp');

-- ========================================
-- ESTADOS DE VISITA
-- ========================================
INSERT INTO estado_visita (nombre, descripcion) VALUES
('PENDIENTE', 'Visita registrada pero no iniciada'),
('EN CURSO', 'Visita actualmente en atención'),
('COMPLETADO', 'Visita completada'),
('CANCELADO', 'Visita cancelada');

-- ========================================
-- SERVICIOS DISPONIBLES
-- ========================================
INSERT INTO tipo_servicios (nombre, descripcion) VALUES
('BAÑO', 'Servicio de baño para mascotas'),
('PELUQUERÍA', 'Corte y peinado de pelaje'),
('SPA', 'Relajación e higiene avanzada'),
('GROOMING', 'Limpieza estética general'),
('GUARDERÍA', 'Cuidado diario por horas o días'),
('HOSPEDAJE', 'Estadía prolongada de mascotas en la veterinaria'),
('HOSPITALIZACIÓN', 'Internamiento por tratamiento médico o postoperatorio'),
('CONSULTA VETERINARIA', 'Revisión médica general'),
('VACUNACIÓN', 'Aplicación de vacunas programadas');

-- ========================================
-- ESTADOS DE HISTORIA CLÍNICA
-- ========================================
INSERT INTO estado_historia_clinica (nombre) VALUES 
('ABIERTA'), ('EN REVISIÓN'), ('CERRADA');

-- ========================================
-- TIPOS DE ARCHIVO CLÍNICO
-- ========================================
INSERT INTO tipos_archivo_clinico (nombre) VALUES 
('RADIOGRAFÍA'), ('ECOGRAFÍA'), ('INFORME'), ('RECETA'), ('PDF'), ('OTRO');

-- ========================================
-- TIPOS DE OPERACIÓN (INVENTARIO)
-- ========================================
INSERT INTO tipo_operacion (nombre, descripcion) VALUES
('ENTRADA', 'Operaciones de ingreso de productos'),
('SALIDA', 'Operaciones de salida de productos'),
('AJUSTE', 'Ajustes de stock por inventario físico');

-- ========================================
-- TIPOS DE MOVIMIENTO (INVENTARIO)
-- ========================================
INSERT INTO tipo_movimiento (nombre, descripcion, id_tipo_operacion) VALUES
('INGRESO POR COMPRA', 'Ingreso de productos comprados al proveedor.', 1),
('INGRESO POR DEVOLUCIÓN', 'Ingreso por devolución de cliente o proveedor.', 1),
('SALIDA POR VENTA', 'Salida de productos por ventas al cliente.', 2),
('SALIDA POR CONSUMO INTERNO', 'Uso interno de productos en la clínica.', 2),
('AJUSTE POR INVENTARIO', 'Corrección por diferencias en inventario.', 3);

-- ========================================
-- ESTADO DE FACTURAS DE COMPRA
-- ========================================
INSERT INTO estado_factura_compra (nombre, descripcion) VALUES
('REGISTRADA', 'Factura ingresada pero no pagada.'),
('PAGADA', 'Factura cancelada en su totalidad.'),
('ANULADA', 'Factura anulada, no genera movimientos.');

-- ========================================
-- ALMACENES
-- ========================================
INSERT INTO almacenes (nombre, descripcion) VALUES
('SEDE PRINCIPAL', 'Local principal de la veterinaria'),
('ALMACÉN', 'Área de almacenamiento de productos en otro lugar');

-- ========================================
-- PRESENTACIONES DE PRODUCTOS
-- ========================================
INSERT INTO presentaciones (nombre, descripcion) VALUES
('UNIDAD', 'Unidad individual de producto.'), ('ML', 'Mililitros...'), ('L', 'Litro...'), ('MG', 'Miligramo...'),
('G', 'Gramo...'), ('KG', 'Kilogramo...'), ('TABLETA', 'Dosis sólida...'), ('CAPSULA', 'Forma sólida...'),
('JERINGA', 'Presentación lista...'), ('AMPOLLA', 'Ampolla líquida...'), ('PIPETA', 'Dosis en pipeta...'),
('FRASCO', 'Envase con contenido...'), ('BOLSA', 'Empaque flexible...'), ('CAJA', 'Empaque rígido...'), ('TIRA', 'Blister o tira...');

-- ========================================
-- CATEGORÍAS DE PRODUCTOS
-- ========================================
INSERT INTO categorias_productos (nombre, descripcion) VALUES
('MEDICAMENTO', 'Fármacos...'), ('VACUNA', 'Vacunas...'), ('ALIMENTO', 'Comida seca...'),
('ACCESORIO', 'Collares...'), ('JUGUETE', 'Juguetes...'), ('HIGIENE', 'Productos de limpieza...'),
('INSTRUMENTAL MÉDICO', 'Herramientas de consulta...'), ('SUPLEMENTO', 'Productos vitamínicos...'),
('DESPARASITANTE', 'Productos para control...'), ('OTRO', 'Otros productos...');

-- ========================================
-- TIPO DOCUMENTO DE VENTA
-- ========================================
INSERT INTO tipo_documento_venta (nombre, descripcion) VALUES
('FACTURA ELECTRONICA', 'Factura electrónica o física.'),
('BOLETA', 'Boleta de venta.'),
('NOTA DE VENTA', 'Documento no tributario.');

-- ========================================
-- ESTADO NOTA DE CRÉDITO
-- ========================================
INSERT INTO estado_nota_credito (nombre) VALUES 
('REGISTRADA'), ('APLICADA'), ('ANULADA');

-- ========================================
-- TIPO NOTA DE CRÉDITO
-- ========================================
INSERT INTO tipo_nota_credito (nombre, descripcion) VALUES 
('DEVOLUCIÓN', 'Devolución de productos o servicios'),
('DESCUENTO', 'Aplicación de descuento posterior a la venta'),
('ERROR DE FACTURACIÓN', 'Corrección de datos de la factura original');

-- ========================================
-- TIPO MOVIMIENTO CAJA
-- ========================================
INSERT INTO tipo_movimiento_caja  (nombre) VALUES 
('INGRESO'), ('EGRESO');

-- ========================================
-- ESTADO FACTURA DE VENTA
-- ========================================
INSERT INTO estado_factura_venta (nombre, descripcion) VALUES
('PENDIENTE', 'Factura emitida pero no cancelada.'),
('PARCIAL', 'Factura parcialmente pagada.'),
('PAGADO', 'Factura cancelada en su totalidad.');

-- ========================================
-- ESTADO CAJA
-- ========================================
INSERT INTO estado_caja (nombre, descripcion) VALUES
('ABIERTA', 'Caja en operación.'),
('CERRADA', 'Caja cerrada y en espera de arqueo.'),
('ANULADA', 'Movimiento de caja anulado.');

-- ========================================
-- ESTADO MENSAJE CLIENTE
-- ========================================
INSERT INTO estado_mensaje_cliente (nombre) VALUES 
('PENDIENTE'), ('RESPONDIDO'), ('ARCHIVADO');
