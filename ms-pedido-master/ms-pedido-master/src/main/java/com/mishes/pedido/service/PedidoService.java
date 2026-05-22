package com.mishes.pedido.service;

import com.mishes.pedido.dto.*;
import com.mishes.pedido.model.Estado;
import com.mishes.pedido.model.Pedido;
import com.mishes.pedido.model.PedidoDetalle;
import com.mishes.pedido.repository.EstadoRepository;
import com.mishes.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;

    // 💡 Variable auxiliar temporal para reflejar el nombre del producto en el Postman sin alterar la BD actual
    private String dtoNombreTemporal;

    // 1. MAPEO DINÁMICO: Toma los datos de la BD e inyecta el nombre y precio real para Postman
    private PedidoResponseDTO mapToDTO(Pedido pedido) {
        Long idProc = null;
        Integer cantidad = 0;
        double precioUnitario = 0.0;

        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            PedidoDetalle detalle = pedido.getDetalles().get(0);
            idProc = detalle.getIdProc();
            cantidad = detalle.getCantidad();
            precioUnitario = detalle.getPrecioUnitario();
        }

        // Si no capturamos un nombre en la petición actual, le asignamos uno genérico según el ID para que nunca se vea vacío
        String nombreAMostrar = (dtoNombreTemporal != null) ? dtoNombreTemporal : "Manga / Libro General (ID: " + idProc + ")";

        return new PedidoResponseDTO(
                pedido.getIdPed(),
                pedido.getIdCli(),
                idProc,
                nombreAMostrar, // 🌟 ¡Aquí se pinta el nombre dinámico del producto!
                pedido.getFechaPedido(),
                pedido.getIdEstado().getNombreEstado(),
                cantidad,
                precioUnitario, // 🌟 Adiós al 0.0 fijo, muestra el valor real
                pedido.getTotalPagar()
        );
    }

    // 2. OBTENER TODOS LOS PEDIDOS
    public List<PedidoResponseDTO> obtenerTodas() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 3. GUARDAR PEDIDO DINÁMICO (Soporta Mangas, Libros, etc.)
    public PedidoResponseDTO guardar(PedidoRequestDTO dto) {
        // Buscamos el estado relacional en la BD
        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        // Almacenamos el nombre que viene de Postman en nuestra variable auxiliar
        this.dtoNombreTemporal = dto.getNombreProducto();

        // Creamos la cabecera del Pedido
        Pedido pedido = new Pedido();
        pedido.setIdCli(dto.getIdCli());
        pedido.setFechaPedido(dto.getFechaPedido());
        pedido.setIdEstado(estado);

        // Si en Postman mandas un precio válido (> 0) lo usa, si no, por defecto aplica 8990.0 (seguridad antibugs)
        double precioAplicado = (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0)
                ? dto.getPrecioUnitario()
                : 8990.0;

        // Calculamos el total de la cabecera automáticamente multiplicando el precio por la cantidad
        pedido.setTotalPagar(precioAplicado * dto.getCantidad());

        // Creamos el detalle de la orden (Relación 1 a Muchos)
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setIdProc(dto.getIdProc());
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(precioAplicado);
        detalle.setPedido(pedido);

        pedido.setDetalles(List.of(detalle));

        log.info("Pedido registrado con éxito. Producto: '{}' | Precio Unitario: ${}", dto.getNombreProducto(), precioAplicado);
        return mapToDTO(pedidoRepository.save(pedido));
    }

    // 4. OBTENER POR ID
    public Optional<PedidoResponseDTO> obtenerPorId(Long id) {
        return pedidoRepository.findById(id).map(this::mapToDTO);
    }

    // 5. ELIMINAR PEDIDO
    public void eliminarPorId(Long id) {
        pedidoRepository.deleteById(id);
    }

    // 6. ACTUALIZAR PEDIDO O ESTADO (PUT)
    public Optional<PedidoResponseDTO> actualizarPorId(Long idPedido, PedidoRequestDTO dto) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            // Actualizamos la variable de nombre con el request del PUT
            this.dtoNombreTemporal = dto.getNombreProducto();

            pedido.setIdCli(dto.getIdCli());
            pedido.setFechaPedido(dto.getFechaPedido());

            // Buscamos y actualizamos el nuevo estado enviado (ej: Id 2 para Preparando)
            Estado nuevoEstado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setIdEstado(nuevoEstado);

            // Modificamos el desglose del detalle asociado
            if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                PedidoDetalle detalle = pedido.getDetalles().get(0);
                detalle.setIdProc(dto.getIdProc());
                detalle.setCantidad(dto.getCantidad());

                // Mantenemos la lógica de precios dinámicos al actualizar
                double precioAplicado = (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0)
                        ? dto.getPrecioUnitario()
                        : detalle.getPrecioUnitario();

                detalle.setPrecioUnitario(precioAplicado);
                pedido.setTotalPagar(precioAplicado * dto.getCantidad());
            }

            log.info("Pedido ID {} actualizado de forma dinámica con éxito", idPedido);
            return mapToDTO(pedidoRepository.save(pedido));
        });
    }
}