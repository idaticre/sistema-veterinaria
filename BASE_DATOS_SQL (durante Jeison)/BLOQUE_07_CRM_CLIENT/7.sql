-- ========================================
-- BLOQUE 07: comunicación con clientes
-- Gestiona los canales de contacto y los mensajes enviados
-- a clientes, incluyendo su estado y trazabilidad.
-- ========================================

-- ========================================
-- TABLA: estado_mensaje_cliente
-- Lista los estados posibles de los mensajes enviados a clientes.
-- ========================================
CREATE TABLE IF NOT EXISTS estado_mensaje_cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    nombre VARCHAR(32) NOT NULL UNIQUE,
    activo TINYINT NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- ========================================
-- TABLA: mensajes_cliente
-- Guarda los mensajes enviados a clientes, con información del canal utilizado,
-- estado del mensaje, contenido, respuesta del cliente y colaborador que lo gestionó.
-- ========================================
CREATE TABLE IF NOT EXISTS mensajes_cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(16) NOT NULL UNIQUE,
    id_cliente INT NOT NULL,
    id_canal INT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME DEFAULT CURRENT_TIMESTAMP,
    asunto VARCHAR(64),
    mensaje TEXT,
    respuesta TEXT,
    id_estado INT NOT NULL,
    id_colaborador INT NULL
);

ALTER TABLE mensajes_cliente 
    ADD CONSTRAINT fk_mensaje_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_estado FOREIGN KEY (id_estado) REFERENCES estado_mensaje_cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_canal FOREIGN KEY (id_canal) REFERENCES canales_comunicacion(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_mensaje_colaborador FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Índice: mejora las consultas por cliente
CREATE INDEX idx_mensajes_cliente_cliente ON mensajes_cliente(id_cliente);

-- Índice: optimiza búsquedas filtradas por canal
CREATE INDEX idx_mensajes_cliente_canal ON mensajes_cliente(id_canal);

-- Índice: permite reportes por estado de mensaje
CREATE INDEX idx_mensajes_cliente_estado ON mensajes_cliente(id_estado);

-- Índice: agiliza consultas filtradas por colaborador
CREATE INDEX idx_mensajes_cliente_colaborador ON mensajes_cliente(id_colaborador);

-- Índice: facilita listados por fecha de envío
CREATE INDEX idx_mensajes_cliente_fecha ON mensajes_cliente(fecha_envio);
