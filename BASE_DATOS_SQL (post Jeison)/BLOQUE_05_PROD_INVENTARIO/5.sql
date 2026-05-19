-- ========================================
-- BLOQUE 05: productos e inventario
-- gestiona las marcas, categorías, productos,
-- control de stock, movimientos e integración
-- con facturas de compra.
-- ========================================

-- ========================================
-- TABLA: tipo_operacion
-- Clasifica el sentido lógico de un movimiento de inventario.
-- Se usa para diferenciar si un movimiento incrementa,
-- reduce o ajusta el stock, independientemente del motivo específico.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_operacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(16) NOT NULL UNIQUE, 					-- ENTRADA, SALIDA, AJUSTE
    descripcion VARCHAR(128),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: tipo_movimiento
-- Lista los tipos posibles de movimiento de productos en inventario.
-- Ejemplo: INGRESO POR COMPRA, SALIDA POR VENTA.
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_movimiento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
    id_tipo_operacion INT NOT NULL,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE tipo_movimiento
ADD CONSTRAINT fk_tipo_operacion FOREIGN KEY (id_tipo_operacion) REFERENCES tipo_operacion(id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- ========================================
-- TABLA: estado_factura_compra
-- Enumera los distintos estados de una factura de compra.
-- Ejemplo: PENDIENTE, PAGADA, ANULADA.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_factura_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: almacenes
-- Lista las ubicaciones físicas disponibles para almacenamiento de productos.
-- Ejemplo: BODEGA CENTRAL, SUCURSAL MAGDALENA.
-- ========================================
CREATE TABLE IF NOT EXISTS almacenes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(128),
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: marcas
-- Registra las marcas de los productos disponibles.
-- ========================================
CREATE TABLE IF NOT EXISTS marcas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: presentaciones
-- Define las formas físicas en que se presentan los productos.
-- Ejemplo: TABLETAS, SOLUCIÓN ORAL, SPRAY.
-- ========================================
CREATE TABLE IF NOT EXISTS presentaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: categorias_productos
-- Agrupa los productos según su tipo o función.
-- Ejemplo: MEDICAMENTOS, ACCESORIOS, ALIMENTOS.
-- ========================================
CREATE TABLE IF NOT EXISTS categorias_productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    descripcion TEXT,
	activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: productos
-- Contiene los datos completos de cada producto, incluyendo precio, presentación, marca y categoría.
-- ========================================
CREATE TABLE IF NOT EXISTS productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL,
    descripcion VARCHAR(64),
    referencia VARCHAR(64),
    id_presentacion INT NOT NULL,
    id_marca INT NOT NULL,
    precio_compra_base DECIMAL(10,2) CHECK (precio_compra_base >= 0),
    precio_sugerido_venta DECIMAL(10,2) CHECK (precio_sugerido_venta >= 0),
    precio_venta DECIMAL(10,2) CHECK (precio_venta >= 0) NOT NULL,
    id_categoria_producto INT NOT NULL,
    id_medicamento INT,
    fecha_vencimiento DATE NULL,
    controla_vencimiento TINYINT NOT NULL DEFAULT 0 CHECK (controla_vencimiento IN (0,1)),
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
ALTER TABLE productos 
	ADD CONSTRAINT uq_prod_nombre_marca_pres UNIQUE(nombre, id_marca, id_presentacion),
    ADD CONSTRAINT fk_producto_presentacion FOREIGN KEY (id_presentacion) REFERENCES presentaciones(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_marca FOREIGN KEY (id_marca) REFERENCES marcas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria_producto) REFERENCES categorias_productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_producto_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de productos por categoría
-- (útil al listar productos filtrados por tipo).
CREATE INDEX idx_productos_categoria ON productos(id_categoria_producto);

-- Índice para búsquedas rápidas de productos por nombre
-- (útil para filtros, autocompletado o búsqueda parcial de productos).
CREATE INDEX idx_productos_nombre ON productos(nombre);

-- Índice para búsquedas rápidas de productos por marca
-- (permite filtrar inventarios por fabricante).
CREATE INDEX idx_productos_marca ON productos(id_marca);

-- Índice para búsquedas rápidas de productos que son medicamentos
-- (permite separar insumos médicos de otros productos).
CREATE INDEX idx_productos_medicamento ON productos(id_medicamento);



-- ========================================
-- TABLA: facturas_compras
-- Registra todas las facturas de compra ingresadas al sistema, con proveedor, usuario y estado.
-- ========================================
CREATE TABLE IF NOT EXISTS facturas_compras (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    numero_factura VARCHAR(64) NOT NULL,
    fecha_factura DATE NOT NULL,
    total DECIMAL(12,2) CHECK (total >= 0) NOT NULL ,
    observaciones VARCHAR(64),
    id_estado INT DEFAULT 1
);
ALTER TABLE facturas_compras
    ADD CONSTRAINT fk_factura_compra_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_compra_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_factura_compra_estado FOREIGN KEY (id_estado) REFERENCES estado_factura_compra(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para consultas rápidas de facturas por proveedor
-- (muy usado en reportes y conciliaciones).
CREATE INDEX idx_facturas_compras_proveedor ON facturas_compras(id_proveedor);

-- Índice para búsquedas de facturas por usuario (quién registró)
CREATE INDEX idx_facturas_compras_usuario ON facturas_compras(id_usuario);

-- Índice para consultas eficientes por fecha de emisión de factura
-- (muy útil en reportes de compras por rango de fechas).
CREATE INDEX idx_facturas_compras_fec_fac ON facturas_compras(fecha_factura);

-- ========================================
-- TABLA: detalle_factura_compra
-- Detalla los productos y cantidades contenidos en una factura de compra.
-- Ejemplo: 3 unidades de "Ivermectina 10ml" a S/ 15.00 cada una, subtotal: S/ 45.00.
-- ========================================
CREATE TABLE IF NOT EXISTS detalle_factura_compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_factura INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED
);
ALTER TABLE detalle_factura_compra
    ADD CONSTRAINT fk_detalle_factura_factura FOREIGN KEY (id_factura) REFERENCES facturas_compras(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_detalle_factura_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para obtener rápidamente los detalles de una factura
CREATE INDEX idx_detalle_fact_compra_factura ON detalle_factura_compra(id_factura);

-- Índice para consultas de facturas por producto
-- (permite conocer en qué facturas se ha comprado un producto específico).
CREATE INDEX idx_detalle_fact_compra_producto ON detalle_factura_compra(id_producto);

-- ========================================
-- TABLA: inventarios
-- Gestiona el stock actual por producto y almacén, incluyendo lote y vencimiento.
-- ========================================
CREATE TABLE IF NOT EXISTS inventarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_producto INT NOT NULL,
    id_almacen INT NOT NULL,
    stock_actual INT CHECK (stock_actual >= 0) NOT NULL,
    stock_minimo INT CHECK (stock_minimo >= 0) NOT NULL,
    fecha_actualizacion DATE NOT NULL,
    lote VARCHAR(32),
    fecha_vencimiento DATE DEFAULT NULL
);
ALTER TABLE inventarios 
    ADD CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_inventario_almacen FOREIGN KEY (id_almacen) REFERENCES almacenes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Índice para búsquedas rápidas de inventario por producto
CREATE INDEX idx_inventarios_producto ON inventarios(id_producto);

-- Índice para búsquedas rápidas de inventario por almacén
CREATE INDEX idx_inventarios_almacen ON inventarios(id_almacen);

-- Índice compuesto para consultas frecuentes de inventarios por producto y almacén
CREATE INDEX idx_inventarios_producto_almacen ON inventarios(id_producto, id_almacen);

-- ========================================
-- TABLA: movimientos_inventario
-- Registra todos los movimientos de inventario realizados (entradas, salidas, ajustes).
-- ========================================
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_producto INT NOT NULL,
    id_usuario INT NOT NULL,
    id_tipo_movimiento INT NOT NULL,
    cantidad INT CHECK (cantidad >= 0) NOT NULL ,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NULL,
    observaciones VARCHAR(64),
    id_factura_compra INT DEFAULT NULL
);
ALTER TABLE movimientos_inventario
    ADD CONSTRAINT fk_movimiento_inventario_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_tipo FOREIGN KEY (id_tipo_movimiento) REFERENCES tipo_movimiento(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_movimiento_factura FOREIGN KEY (id_factura_compra) REFERENCES facturas_compras(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice para consultas rápidas de movimientos de inventario por producto
CREATE INDEX idx_movimientos_producto ON movimientos_inventario(id_producto);

-- Índice para búsquedas rápidas de movimientos por usuario
CREATE INDEX idx_movimientos_usuario ON movimientos_inventario(id_usuario);

-- Índice para búsquedas rápidas de movimientos por tipo de movimiento
CREATE INDEX idx_movimientos_tipo ON movimientos_inventario(id_tipo_movimiento);

-- Índice compuesto para consultas de movimientos asociados a facturas de compra
CREATE INDEX idx_movimientos_factura ON movimientos_inventario(id_factura_compra);

-- Índice para consultas cronológicas de movimientos de inventario
-- (útil en reportes históricos o análisis por fechas).
CREATE INDEX idx_movimientos_fecha ON movimientos_inventario(fecha_movimiento);

-- Índice compuesto para auditorías por usuario y fecha de movimiento
-- (acelera consultas para saber qué hizo un usuario en un periodo determinado).
CREATE INDEX idx_movimientos_usuario_fecha ON movimientos_inventario(id_usuario, fecha_movimiento);