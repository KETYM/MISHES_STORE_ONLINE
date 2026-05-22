package com.mishes.pedido.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPed;

    @Column(nullable = false)
    private Long idCli; // ID del Cliente/Usuario

    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado idEstado; // 💡 Mantenemos la excelente idea de tu compañero

    @Column(nullable = false)
    private Double totalPagar; // Para guardar el costo total de la compra completa

    // 💡 LA RELACIÓN 1 A MUCHOS PARA LA PAUTA:
    // Un pedido tiene muchos productos detallados en la lista.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoDetalle> detalles;
}