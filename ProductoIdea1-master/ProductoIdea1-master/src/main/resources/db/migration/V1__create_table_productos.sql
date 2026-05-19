CREATE TABLE productos(
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_producto VARCHAR(100) NOT NULL,
    autor VARCHAR(100),
    precio DECIMAL(10,2),
    stock Integer,
    isbn VARCHAR(20) UNIQUE
);