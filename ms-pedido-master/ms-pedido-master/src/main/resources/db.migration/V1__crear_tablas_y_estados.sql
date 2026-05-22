-- 1. CREAR LA TABLA MAESTRA DE ESTADOS
CREATE TABLE estado (
    id_estado SMALLINT PRIMARY KEY,
    nombre_estado VARCHAR(20) NOT NULL
);

-- 2. CREAR LA TABLA DE PEDIDO (LA CABECERA GLOBAL)
CREATE TABLE pedido (
    id_ped BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cli BIGINT NOT NULL,
    fecha_pedido DATETIME NOT NULL,
    total_pagar DOUBLE NOT NULL,
    id_estado SMALLINT NOT NULL,
    CONSTRAINT fk_pedido_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);

-- 3. CREAR LA TABLA DE DETALLES (LA RELACIÓN 1 A MUCHOS)
CREATE TABLE pedido_detalle (
    id_ped_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_proc BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    id_pedido BIGINT NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_ped) ON DELETE CASCADE
);

-- 4. INSERTAR LOS ESTADOS FIJOS OBLIGATORIOS
INSERT INTO estado (id_estado, nombre_estado) VALUES (1, 'PENDIENTE_PAGO');
INSERT INTO estado (id_estado, nombre_estado) VALUES (2, 'PAGADO');
INSERT INTO estado (id_estado, nombre_estado) VALUES (3, 'ENVIADO');