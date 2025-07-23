-- BASE DE DATOS
DROP DATABASE IF EXISTS veterinaria_woof;

CREATE DATABASE veterinaria_woof;
USE veterinaria_woof;

-- TABLA DE LA VETERINARIA PARA INICIO DEL SISTEMA WEB
CREATE TABLE IF NOT EXISTS empresa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    razon_social VARCHAR(128) NOT NULL,
    ruc CHAR(11) NOT NULL UNIQUE,
    direccion VARCHAR(256),
    ciudad VARCHAR(64),
    distrito VARCHAR(64),
    telefono VARCHAR(15),
    correo VARCHAR(64),
    representante VARCHAR(64), -- Tu nombre o de quien figura como titular
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logo_empresa BLOB
);

-- TABLAS AUXILIARES
CREATE TABLE IF NOT EXISTS tipo_documento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descripcion VARCHAR(32) NOT NULL UNIQUE
);

-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    activo TINYINT NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ROLES
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE
);

-- USUARIOS_ROLES (muchos a muchos)
CREATE TABLE IF NOT EXISTS usuarios_roles (
    id_usuario INT,
    id_rol INT,
    PRIMARY KEY (id_usuario, id_rol)
);

ALTER TABLE usuarios_roles 
    ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    ADD CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES roles(id);
    
-- TABLA ENTIDADES Aqui guardará las personas y empresas
-- cuando un colaborador o un proveedor se convierta en cliente 

CREATE TABLE IF NOT EXISTS entidades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo_entidad ENUM('natural', 'juridica') NOT NULL,
    nombre VARCHAR(128) NOT NULL,             		-- Nombre o razón social
    documento BIGINT NOT NULL UNIQUE,         		-- DNI, RUC, etc.
    id_tipo_documento INT NOT NULL,
    correo VARCHAR(64),
    telefono VARCHAR(15),
    direccion VARCHAR(256),
    ciudad VARCHAR(64),
    distrito VARCHAR(64),
    representante VARCHAR(64),                		-- Si es empresa
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE entidades
    ADD CONSTRAINT fk_entidad_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id);

-- COLABORADORES
CREATE TABLE IF NOT EXISTS colaboradores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_entidad INT NOT NULL UNIQUE,
    fecha_ingreso DATE,
    id_usuario INT,
    activo TINYINT NOT NULL DEFAULT 1,
    foto BLOB
);

ALTER TABLE colaboradores
    ADD CONSTRAINT fk_colaborador_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id),
    ADD CONSTRAINT fk_colaborador_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id);

-- TABLA DE ESPECIALIDADES - ENTORNO MEDICO
CREATE TABLE IF NOT EXISTS especialidades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE
);

-- TABLA DE VETERINARIOS
CREATE TABLE IF NOT EXISTS veterinarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador INT NOT NULL UNIQUE,
    id_especialidad INT NOT NULL,
    cmp VARCHAR(32), 					-- CERTIFICADO MEDICO DE PERU
    anios_experiencia INT,
    observaciones TEXT,
    activo TINYINT DEFAULT 1
);

ALTER TABLE veterinarios
    ADD CONSTRAINT fk_vet_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id),
    ADD CONSTRAINT fk_vet_especialidad FOREIGN KEY (id_especialidad) REFERENCES especialidades(id);

-- MEDICAMENTOS
CREATE TABLE IF NOT EXISTS medicamentos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    tipo ENUM('Antibiótico', 'Antiinflamatorio', 'Desparasitante', 'Antifúngico', 'Analgésico', 'Otro') DEFAULT 'Otro',
    descripcion TEXT
);

-- PRODUCTOS E INVENTARIO
CREATE TABLE IF NOT EXISTS categorias_productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE
);
-- Posibles categorías:
-- 'Medicamento', 'Accesorio', 'Alimento', 'Higiene', 'Juguete', 'Instrumental', 'Otro'

-- PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    descripcion TEXT,
    precio_compra_base DECIMAL(10,2),				 -- nuevo campo
    precio_sugerido_venta DECIMAL(10,2),			 -- nuevo campo
    precio DECIMAL(10,2) NOT NULL,					 -- precio actual de venta real
    id_categoria_producto INT NOT NULL,
    id_medicamento INT, 							-- opcional, si este producto es un medicamento clínico
    unidad VARCHAR(16),								-- ej: 'ml', 'tabletas', 'unidades'
    activo TINYINT DEFAULT 1
);
ALTER TABLE productos ADD CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria_producto) REFERENCES categorias_productos(id);
ALTER TABLE productos ADD CONSTRAINT fk_producto_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id);

-- PROVEEDORES
CREATE TABLE IF NOT EXISTS proveedores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_entidad INT NOT NULL UNIQUE,
    activo TINYINT DEFAULT 1
);

ALTER TABLE proveedores 
    ADD CONSTRAINT fk_proveedor_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id);

-- ORDENES DE COMPRA
CREATE TABLE IF NOT EXISTS ordenes_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha_orden DATE,
    id_proveedor INT NOT NULL,
    estado_orden ENUM('Pendiente', 'Procesada', 'Cancelada') DEFAULT 'Pendiente',
    total_orden DECIMAL(10,2)
);

ALTER TABLE ordenes_compra ADD CONSTRAINT fk_orden_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id);

-- DETALLE DE ORDENES DE COMPRA
CREATE TABLE IF NOT EXISTS detalle_orden_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_orden_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

ALTER TABLE detalle_orden_compra ADD CONSTRAINT fk_detalle_orden FOREIGN KEY (id_orden_compra) REFERENCES ordenes_compra(id),
    ADD CONSTRAINT fk_detalle_producto_compra FOREIGN KEY (id_producto) REFERENCES productos(id);
    
-- ESPECIES
CREATE TABLE IF NOT EXISTS especies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE
);

-- RAZAS
CREATE TABLE IF NOT EXISTS razas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_especie INT,
    nombre VARCHAR(32) NOT NULL
);
ALTER TABLE razas ADD CONSTRAINT fk_raza_especie FOREIGN KEY (id_especie) REFERENCES especies(id);

-- TAMAÑOS
CREATE TABLE IF NOT EXISTS tamanos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descripcion VARCHAR(16) NOT NULL UNIQUE
);

-- VACUNAS
CREATE TABLE IF NOT EXISTS vacunas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    id_especie INT NOT NULL,
    descripcion TEXT
);
ALTER TABLE vacunas ADD CONSTRAINT fk_vacuna_especie FOREIGN KEY (id_especie) REFERENCES especies(id);

-- INVENTARIO
CREATE TABLE IF NOT EXISTS inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT NOT NULL,
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL,
    fecha_actualizacion DATE,
    lote VARCHAR(32), -- opcional: para trazabilidad
    fecha_vencimiento DATE -- útil para medicamentos o alimentos
);
ALTER TABLE inventario ADD CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES productos(id);

-- CLIENTES 
CREATE TABLE IF NOT EXISTS clientes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_entidad INT NOT NULL UNIQUE,
    activo TINYINT DEFAULT 1
);

ALTER TABLE clientes 
    ADD CONSTRAINT fk_cliente_entidad FOREIGN KEY (id_entidad) REFERENCES entidades(id);

-- MASCOTAS
CREATE TABLE IF NOT EXISTS mascotas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    id_cliente INT NOT NULL,
    id_raza INT,
    id_especie INT NOT NULL,
    fecha_nacimiento DATE,
    pelaje VARCHAR(16),
    id_tamano INT,
    esterilizado TINYINT,
    alergias TEXT,
    peso DECIMAL(5,2),
    chip TINYINT,
    pedigree TINYINT, 
    factor_dea TINYINT,
    agresividad TINYINT, -- Escala de 0 a 5, por ejemplo
    foto BLOB,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo TINYINT DEFAULT 1
);

ALTER TABLE mascotas
    ADD CONSTRAINT fk_mascota_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    ADD CONSTRAINT fk_mascota_raza FOREIGN KEY (id_raza) REFERENCES razas(id),
    ADD CONSTRAINT fk_mascota_especie FOREIGN KEY (id_especie) REFERENCES especies(id),
    ADD CONSTRAINT fk_mascota_tamano FOREIGN KEY (id_tamano) REFERENCES tamanos(id);

-- MEDICAMENTOS DE MASCOTA
CREATE TABLE IF NOT EXISTS medicamentos_mascota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_mascota INT NOT NULL,
    id_medicamento INT NOT NULL,
    dosis VARCHAR(32),
    via ENUM('oral', 'tópica', 'subcutánea', 'intramuscular', 'intravenosa', 'otra'),
    fecha_aplicacion DATE,
    id_colaborador INT,  -- Quién aplicó el medicamento
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE medicamentos_mascota
    ADD CONSTRAINT fk_med_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id),
    ADD CONSTRAINT fk_med_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id),
    ADD CONSTRAINT fk_med_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);

-- VACUNA DE MASCOTA
CREATE TABLE IF NOT EXISTS vacunas_mascota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_vacuna INT NOT NULL,
    id_mascota INT NOT NULL,
    dosis VARCHAR(32),
    via ENUM('intramuscular', 'subcutánea', 'intranasal', 'oral', 'otra'),
    fecha_aplicacion DATE,
    durabilidad INT,  -- en meses
    proxima_dosis DATE,
    id_colaborador INT,
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vacunas_mascota
    ADD CONSTRAINT fk_vacuna FOREIGN KEY (id_vacuna) REFERENCES vacunas(id),
    ADD CONSTRAINT fk_vacuna_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id),
    ADD CONSTRAINT fk_vacuna_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);



-- TIPO DE SERVICIOS
CREATE TABLE IF NOT EXISTS tipo_servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    es_clinico TINYINT DEFAULT 0,			-- 1 para considerarlo dentro de la historia clinica
    descripcion VARCHAR(128) 
);


-- INGRESO DE SERVICIOS PARA AGENDAR
CREATE TABLE IF NOT EXISTS ingresos_servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_servicio INT NOT NULL,                         -- Tipo de servicio realizado
    id_colaborador INT,                               -- Quién lo ejecutó (puede ser técnico, asistente, etc.)
    id_veterinario INT,                               -- Si fue atendido por un veterinario colegiado
    cantidad INT,                                     -- Unidades aplicadas
    horas_estimadas INT,                              -- Para grooming, spa, etc.
    adicionales TEXT,                                 -- Servicios extra u observaciones especiales
    observaciones TEXT,                               -- Comentarios del colaborador
    precio_unitario DECIMAL(10,2),                    -- Precio por unidad
    subtotal DECIMAL(10,2),                           -- cantidad * precio_unitario
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE ingresos_servicios
    ADD CONSTRAINT fk_ingreso_servicio FOREIGN KEY (id_servicio) REFERENCES tipo_servicios(id),
    ADD CONSTRAINT fk_ingreso_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id),
    ADD CONSTRAINT fk_ingreso_vet FOREIGN KEY (id_veterinario) REFERENCES colaboradores(id);

-- AGENDAMIENTO
CREATE TABLE IF NOT EXISTS agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_cliente INT NOT NULL,                          -- Cliente que agenda
    id_mascota INT NOT NULL,                          -- Mascota que recibirá el servicio
    id_servicio INT NOT NULL,                         -- Tipo de servicio solicitado
    fecha DATE,
    hora TIME,
    duracion_estimada INT,                            -- En minutos
    estado ENUM('pendiente', 'confirmado', 'cancelado', 'atendido') NOT NULL DEFAULT 'pendiente',
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE agenda
    ADD CONSTRAINT fk_agenda_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    ADD CONSTRAINT fk_agenda_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id),
    ADD CONSTRAINT fk_agenda_servicio FOREIGN KEY (id_servicio) REFERENCES tipo_servicios(id);

-- VISITAS
CREATE TABLE IF NOT EXISTS visitas_ingresos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_agenda INT NULL,                                		-- Agenda que originó la visita (si aplica)
    fecha_ingreso DATE,                               		-- Día en que la mascota llega
    id_mascota INT NOT NULL,                           		-- Mascota atendida
    id_ingreso_servicio INT NOT NULL,                  		-- Servicio realizado con detalle
    fecha_retiro DATE,                                 		-- Día de salida o alta
    medio_solicitud ENUM('web', 'presencial', 'llamada', 'whatsapp') NOT NULL,
    estado ENUM('pendiente', 'en curso', 'completado', 'cancelado') DEFAULT 'pendiente',
    abono DECIMAL(10,2),
    total DECIMAL(10,2)
);

-- Relaciones foráneas
ALTER TABLE visitas_ingresos
    ADD CONSTRAINT fk_visita_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id),
    ADD CONSTRAINT fk_visita_ingreso FOREIGN KEY (id_ingreso_servicio) REFERENCES ingresos_servicios(id),
    ADD CONSTRAINT fk_visita_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id);


-- VENTAS Y FACTURACIÓN
CREATE TABLE IF NOT EXISTS medios_pago (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) NOT NULL
);

-- TABLA GENERAL DE LA HISTORIA CLINICA
CREATE TABLE IF NOT EXISTS historia_clinica (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_mascota INT NOT NULL,
    id_colaborador INT,         			-- Opcional: quien atendió (puede ser asistente, peluquero, etc.)
    id_veterinario INT,         			-- Opcional: si fue un veterinario colegiado
    id_visita INT,              			-- Opcional: si proviene de una visita estructurada
    id_ingreso_servicio INT,    			-- Opcional: si viene de un servicio agendado
    fecha DATE NOT NULL,
    tipo_servicio VARCHAR(64) NOT NULL,  	-- Libre o relacionado
    descripcion TEXT,                     	-- Descripción general del evento
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE historia_clinica
    ADD CONSTRAINT fk_hist_mascota FOREIGN KEY (id_mascota) REFERENCES mascotas(id),
    ADD CONSTRAINT fk_hist_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id),
    ADD CONSTRAINT fk_hist_vet FOREIGN KEY (id_veterinario) REFERENCES colaboradores(id),
    ADD CONSTRAINT fk_hist_visita FOREIGN KEY (id_visita) REFERENCES visitas_ingresos(id),
    ADD CONSTRAINT fk_hist_ingreso FOREIGN KEY (id_ingreso_servicio) REFERENCES ingresos_servicios(id);

-- TABLA PARA ARCHIVOS DE LA HISTORIA CLINICA GRAL
CREATE TABLE IF NOT EXISTS historia_clinica_archivos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_historia_clinica INT NOT NULL,
    tipo_archivo ENUM('Radiografía', 'Ecografía', 'Informe', 'Receta', 'PDF', 'Otro') DEFAULT 'Otro',
    nombre_archivo VARCHAR(128) NOT NULL,
    archivo LONGBLOB NOT NULL,
    descripcion TEXT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE historia_clinica_archivos
    ADD CONSTRAINT fk_archivo_historia FOREIGN KEY (id_historia_clinica) REFERENCES historia_clinica(id);

-- FACTURA
CREATE TABLE IF NOT EXISTS factura_cabecera (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha_factura DATE,
    id_cliente INT NOT NULL,
    id_medio_pago INT NOT NULL,
    total_pagado DECIMAL(10,2) DEFAULT 0,
    subtotal DECIMAL(10,2),
    impuestos DECIMAL(10,2),
    descuentos DECIMAL(10,2),
    saldo_pendiente DECIMAL(10,2) GENERATED ALWAYS AS (total_factura - total_pagado) STORED,
    estado_pago ENUM('PENDIENTE', 'PARCIAL', 'PAGADO') DEFAULT 'PENDIENTE',
    total_factura DECIMAL(10,2),
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE factura_cabecera
    ADD CONSTRAINT fk_factura_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    ADD CONSTRAINT fk_factura_pago FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id);


-- DETALLE FACTURA
CREATE TABLE IF NOT EXISTS detalle_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_factura INT NOT NULL,
    id_producto INT,
    id_ingreso INT,
    cantidad INT,
    precio_unitario DECIMAL(10,2),
    total_item DECIMAL(10,2),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE detalle_factura
    ADD CONSTRAINT fk_detalle_factura FOREIGN KEY (id_factura) REFERENCES factura_cabecera(id),
    ADD CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos(id),
    ADD CONSTRAINT fk_detalle_ingreso FOREIGN KEY (id_ingreso) REFERENCES ingresos_servicios(id);

-- CAJA GENERAL
CREATE TABLE IF NOT EXISTS caja_general (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    tipo_movimiento ENUM('INGRESO', 'EGRESO') NOT NULL,
    descripcion VARCHAR(128),
    monto DECIMAL(10,2) NOT NULL,
    saldo_inicial DECIMAL(10,2),
    saldo_final DECIMAL(10,2),
    id_colaborador INT,
    observaciones TEXT
);

ALTER TABLE caja_general
    ADD CONSTRAINT fk_caja_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);

-- Índice para optimizar consultas por fecha
CREATE INDEX idx_fecha_caja ON caja_general(fecha);

-- ABONO POR PARTE DE CLIENTES
CREATE TABLE pagos_clientes (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_caja_general INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    medio_pago VARCHAR(50), -- o id_medio_pago si es una FK
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT,
    fue_asociado_factura BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    FOREIGN KEY (id_caja_general) REFERENCES caja_general(id)
);
-- ARQUEO DIARIO
CREATE TABLE IF NOT EXISTS arqueo_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    id_colaborador INT NOT NULL,
    saldo_apertura DECIMAL(10,2),         -- Saldo con el que empezó el día
    total_ingresos DECIMAL(10,2),         -- Suma de ingresos registrados en caja_general
    total_egresos DECIMAL(10,2),          -- Suma de egresos registrados en caja_general
    saldo_calculado DECIMAL(10,2),        -- apertura + ingresos - egresos
    saldo_declarado DECIMAL(10,2),        -- Lo contado físicamente por el colaborador
    diferencia DECIMAL(10,2),             -- declarada - calculada
    tipo_movimiento ENUM('INGRESO', 'EGRESO') DEFAULT 'INGRESO',
    referencia_pago_cliente INT,
    observaciones TEXT
);

ALTER TABLE arqueo_caja
    ADD CONSTRAINT fk_arqueo_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id),
    ADD CONSTRAINT fk_referencia_pago_cliente FOREIGN KEY (referencia_pago_cliente) REFERENCES pagos_clientes(id_pago);


-- DETALLE DE PAGO DE FACTURA (LOS ABONOS)
CREATE TABLE detalle_pagos_factura (
    id_detalle_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_factura INT NOT NULL,
    id_pago INT NOT NULL,
    monto_asignado DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_factura) REFERENCES factura_cabecera(id),
    FOREIGN KEY (id_pago) REFERENCES pagos_clientes(id_pago)
);

-- NOTA CREDITO
CREATE TABLE IF NOT EXISTS nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_factura INT NOT NULL,
    fecha DATE,
    motivo TEXT,
    monto DECIMAL(10,2),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE nota_credito
    ADD CONSTRAINT fk_nota_factura FOREIGN KEY (id_factura) REFERENCES factura_cabecera(id);



-- TABLA: canales_comunicacion
CREATE TABLE IF NOT EXISTS canales_comunicacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(32) UNIQUE NOT NULL
);

-- MENSAJE A CLIENTES
CREATE TABLE IF NOT EXISTS mensajes_cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    canal_id INT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    asunto VARCHAR(64),
    mensaje TEXT,
    respuesta TEXT,
    estado VARCHAR(16) NOT NULL CHECK (estado IN ('Pendiente', 'Respondido', 'Archivado')),
    id_colaborador INT
);

ALTER TABLE mensajes_cliente 
    ADD CONSTRAINT fk_mensaje_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    ADD CONSTRAINT fk_mensaje_canal FOREIGN KEY (canal_id) REFERENCES canales_comunicacion(id),
    ADD CONSTRAINT fk_mensaje_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);



-- HORARIO DE TRABAJO
CREATE TABLE IF NOT EXISTS horarios_trabajo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador INT NOT NULL,
    dia_semana ENUM('Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL
);

ALTER TABLE horarios_trabajo
    ADD CONSTRAINT fk_horario_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);

-- ASISTENCIA 
CREATE TABLE IF NOT EXISTS registro_asistencia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_colaborador INT NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    observaciones TEXT
);

ALTER TABLE registro_asistencia
    ADD CONSTRAINT fk_asistencia_colab FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id);


-- RECORDAR LAS CITAS
CREATE TABLE IF NOT EXISTS recordatorios_agenda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_agenda INT NOT NULL,
    fecha_recordatorio DATE NOT NULL,
    hora TIME,
    mensaje TEXT NOT NULL,
    enviado TINYINT NOT NULL DEFAULT 0
);

ALTER TABLE recordatorios_agenda
    ADD CONSTRAINT fk_recordatorio_agenda FOREIGN KEY (id_agenda) REFERENCES agenda(id);
