-- ========================================
-- BLOQUE 06: ventas y caja
-- gestiona facturación, medios de pago, control de caja,
-- arqueos, pagos de clientes y notas de crédito.
-- ========================================

-- ========================================
-- TABLA: tipo_documento_venta
-- Define el tipo de documento emitido a clientes.
-- Ejemplo: FACTURA, BOLETA, NOTA DE VENTA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_documento_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_item_factura
-- Clasifica ítems en la factura como productos o servicios.
-- Ejemplo: PRODUCTO, SERVICIO, OTRO.
-- ========================================
CREATE TABLE tipo_item_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) UNIQUE NOT NULL,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_nota_credito
-- Indica el estado actual de una nota de crédito.
-- Ejemplo: ACTIVA, ANULADA.
-- ========================================
CREATE TABLE estado_nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_nota_credito
-- Describe el tipo o motivo general de una nota de crédito.
-- Ejemplo: DEVOLUCIÓN, ERROR DE FACTURACIÓN.
-- ========================================
CREATE TABLE tipo_nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_movimiento_caja
-- Lista los tipos de movimientos registrados en la caja.
-- Ejemplo: INGRESO, EGRESO, AJUSTE.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_movimiento_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_factura_venta
-- Representa los posibles estados de una factura emitida.
-- Ejemplo: EMITIDA, PAGADA, ANULADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_factura_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: estado_caja
-- Indica el estado general de la caja diaria.
-- Ejemplo: ABIERTA, CERRADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: caja_general
-- Controla ingresos y egresos registrados en la caja diaria.
-- ========================================
CREATE TABLE IF NOT EXISTS caja_general (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    tipo_movimiento INT NOT NULL,
    descripcion VARCHAR(128),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    saldo_inicial DECIMAL(10,2),
    saldo_final DECIMAL(10,2) DEFAULT 0,
    id_usuario INT NOT NULL,
    observaciones TEXT
);

ALTER TABLE caja_general
    ADD CONSTRAINT fk_caja_tipo FOREIGN KEY (tipo_movimiento) REFERENCES tipo_movimiento_caja(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_caja_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: mejora reportes y listados por fecha
CREATE INDEX idx_caja_fecha ON caja_general(fecha);

-- Índice: facilita consultas filtradas por usuario
CREATE INDEX idx_caja_usuario ON caja_general(id_usuario);

-- ========================================
-- TABLA: pagos_clientes
-- Registra pagos realizados por clientes, asociados o no a facturas.
-- ========================================
CREATE TABLE IF NOT EXISTS pagos_clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_usuario INT NOT NULL,
    id_cliente INT NOT NULL,
    id_visita_ingreso INT NULL,
    id_caja_general INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    id_medio_pago INT,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(64)
);
ALTER TABLE pagos_clientes
    ADD CONSTRAINT fk_pago_clientes_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_clientes_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_clientes_caja FOREIGN KEY (id_caja_general) REFERENCES caja_general(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_pago_visita FOREIGN KEY (id_visita_ingreso) REFERENCES visitas_ingresos(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_pago_cliente_medio FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: agiliza búsquedas por cliente
CREATE INDEX idx_pagos_cliente ON pagos_clientes(id_cliente);

-- Índice: permite filtrar por usuario que registró el pago
CREATE INDEX idx_pagos_usuario ON pagos_clientes(id_usuario);

-- Índice: optimiza reportes de pagos por fecha
CREATE INDEX idx_pagos_fecha ON pagos_clientes(fecha_pago);

-- ========================================
-- TABLA: arqueo_caja
-- Realiza el cierre diario de caja, validando saldos y registrando diferencias.
-- ========================================
CREATE TABLE IF NOT EXISTS arqueo_caja (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    id_usuario INT NOT NULL,
    saldo_apertura DECIMAL(10,2),
    total_ingresos DECIMAL(10,2) CHECK (total_ingresos >= 0),
    total_egresos DECIMAL(10,2) CHECK (total_egresos >= 0),
    saldo_calculado DECIMAL(10,2),
    saldo_declarado DECIMAL(10,2),
    diferencia DECIMAL(10,2),
    referencia_pago_cliente INT,
    id_estado_caja INT NOT NULL DEFAULT 1,
    observaciones TEXT
);

ALTER TABLE arqueo_caja
    ADD CONSTRAINT fk_estado_caja FOREIGN KEY (id_estado_caja) REFERENCES estado_caja(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_arqueo_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_referencia_pago_cliente FOREIGN KEY (referencia_pago_cliente) REFERENCES pagos_clientes(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: mejora consultas por fecha de arqueo
CREATE INDEX idx_arqueo_fecha ON arqueo_caja(fecha);

-- Índice: optimiza búsquedas filtradas por usuario
CREATE INDEX idx_arqueo_usuario ON arqueo_caja(id_usuario);

-- Índice: agiliza consultas por estado de caja (ej. abiertos o cerrados)
CREATE INDEX idx_arqueo_estado ON arqueo_caja(id_estado_caja);

-- ========================================
-- TABLA: facturas_venta
-- Registra facturas emitidas a clientes por productos o servicios.
-- ========================================
CREATE TABLE IF NOT EXISTS facturas_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_tipo_documento INT NOT NULL,
    fecha_factura DATE NOT NULL,
    id_usuario INT NOT NULL,
    id_cliente INT NOT NULL,
    id_medio_pago INT NOT NULL,
    total_pagado DECIMAL(10,2) DEFAULT 0 CHECK (total_pagado >= 0)  DEFAULT 0,
    subtotal DECIMAL(10,2) CHECK (subtotal >= 0) DEFAULT 0,
    impuestos DECIMAL(10,2) CHECK (impuestos >= 0) DEFAULT 0,
    descuentos DECIMAL(10,2) CHECK (descuentos >= 0) DEFAULT 0,
    total_factura DECIMAL(10,2) CHECK (total_factura >= 0) DEFAULT 0,
    saldo_pendiente DECIMAL(10,2) CHECK (saldo_pendiente >= 0) DEFAULT 0 ,
    id_estado INT NOT NULL DEFAULT 1,
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE facturas_venta
    ADD CONSTRAINT fk_factura_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_pago FOREIGN KEY (id_medio_pago) REFERENCES medios_pago(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_estado FOREIGN KEY (id_estado) REFERENCES estado_factura_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: mejora las consultas filtradas por cliente
CREATE INDEX idx_facturas_cliente ON facturas_venta(id_cliente);

-- Índice: permite filtrar por el usuario que emitió la factura
CREATE INDEX idx_facturas_usuario ON facturas_venta(id_usuario);

-- Índice: optimiza las búsquedas por estado de la factura
CREATE INDEX idx_facturas_estado ON facturas_venta(id_estado);

-- Índice: facilita listados y ordenación por fecha
CREATE INDEX idx_facturas_fecha ON facturas_venta(fecha_factura);

-- ========================================
-- TABLA: detalle_factura
-- Detalla los productos o servicios incluidos en cada factura.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_tipo_item INT NOT NULL,
    id_producto INT,
    id_visita_ingreso INT,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) CHECK (precio_unitario >= 0) NOT NULL,
    total_item DECIMAL(10,2) CHECK (total_item >= 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE detalle_factura
    ADD CONSTRAINT fk_detalle_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_tipo_item_factura FOREIGN KEY (id_tipo_item) REFERENCES tipo_item_factura(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detfac_visita FOREIGN KEY (id_visita_ingreso) REFERENCES visitas_ingresos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: recupera rápidamente todos los ítems de una factura
CREATE INDEX idx_detalle_fact_vta_factura ON detalle_factura(id_factura);

-- Índice: permite consultar en qué facturas se vendió un producto específico
CREATE INDEX idx_detalle_fact_vta_producto ON detalle_factura(id_producto);


-- ========================================
-- TABLA: detalle_pagos_factura
-- Asocia montos pagados por clientes a facturas específicas.
-- Ejemplo: Pago de S/ 120.00 aplicado a factura FV-0341.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_pagos_factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_pago INT NOT NULL,
    monto_asignado DECIMAL(10,2) NOT NULL CHECK (monto_asignado > 0)
);

ALTER TABLE detalle_pagos_factura
    ADD CONSTRAINT fk_det_pago_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_det_pago_clientes FOREIGN KEY (id_pago) REFERENCES pagos_clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: permite obtener pagos asignados a una factura
CREATE INDEX idx_detalle_pago_factura ON detalle_pagos_factura(id_factura);

-- Índice: agiliza consultas de facturas asociadas a un pago
CREATE INDEX idx_detalle_pago_pago ON detalle_pagos_factura(id_pago);

-- ========================================
-- TABLA: nota_credito
-- Emite notas de crédito relacionadas a facturas de venta.
-- ========================================
CREATE TABLE IF NOT EXISTS nota_credito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_usuario INT NOT NULL,
    id_factura INT NOT NULL,
    id_estado INT NOT NULL DEFAULT 1,
    id_tipo INT NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(128),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE nota_credito
    ADD CONSTRAINT fk_nota_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_nota_factura FOREIGN KEY (id_factura) REFERENCES facturas_venta(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
	ADD CONSTRAINT fk_estado_nota_credito FOREIGN KEY (id_estado) REFERENCES estado_nota_credito(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_tipo_nota_credito FOREIGN KEY (id_tipo) REFERENCES tipo_nota_credito(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice: optimiza consultas de notas por factura
CREATE INDEX idx_nota_factura ON nota_credito(id_factura);

-- Índice: permite listar notas emitidas por usuario
CREATE INDEX idx_nota_usuario ON nota_credito(id_usuario);

-- Índice: mejora reportes y listados por fecha de emisión de la nota de crédito
CREATE INDEX idx_nota_fecha ON nota_credito(fecha);