package com.mishes.pedido.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "estado")
public class Estado {

    @Id
    private Short idEstado;

    @Column(nullable = false, length = 20)
    private String nombreEstado;

}
