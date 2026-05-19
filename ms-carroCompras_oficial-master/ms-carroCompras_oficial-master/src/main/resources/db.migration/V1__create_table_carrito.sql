CREATE TABLE carrito (
    id_carrito BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL
);