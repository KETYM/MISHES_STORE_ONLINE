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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;

    private String dtoNombreTemporal;

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

        String nombreAMostrar = (dtoNombreTemporal != null) ? dtoNombreTemporal : "Manga / Libro General (ID: " + idProc + ")";

        return new PedidoResponseDTO(
                pedido.getIdPed(),
                pedido.getIdCli(),
                idProc,
                nombreAMostrar,
                pedido.getFechaPedido(),
                pedido.getIdEstado().getNombreEstado(),
                cantidad,
                precioUnitario,
                pedido.getTotalPagar()
        );
    }

    public List<PedidoResponseDTO> obtenerTodas() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🌟 GUARDAR ACTUALIZADO Y BLINDADO: Para evitar errores de Feign si falta el cliente del carro
    public PedidoResponseDTO guardar(PedidoRequestDTO dto) {

        // Buscamos el estado relacional en la BD usando el ID que viene en el DTO o uno por defecto
        Long idEstadoABuscar = (dto.getIdEstado() != null) ? dto.getIdEstado() : 1L;

        // Buscamos sin el sufijo "L" por si las dudas si tu BD maneja otro tipo
        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado en la base de datos"));

        this.dtoNombreTemporal = dto.getNombreProducto();

        Pedido pedido = new Pedido();
        pedido.setIdCli(dto.getIdCli());

        // 🕒 Si en Postman no mandas fecha, el sistema la calcula sola automáticamente
        pedido.setFechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : LocalDateTime.now());
        pedido.setIdEstado(estado);

        double precioAplicado = (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0)
                ? dto.getPrecioUnitario()
                : 8990.0;

        // 🌟 CORREGIDO: Usamos la variable de forma idéntica en el cálculo y en el seteo
        double totalGlobalPagar = precioAplicado * (dto.getCantidad() != null ? dto.getCantidad() : 1);
        pedido.setTotalPagar(totalGlobalPagar);

        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setIdProc(dto.getIdProc());
        detalle.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 1);
        detalle.setPrecioUnitario(precioAplicado); // Ajustado al precio real
        detalle.setPedido(pedido);

        pedido.setDetalles(List.of(detalle));

        log.info("Pedido registrado con éxito. Total: ${}", totalGlobalPagar);
        return mapToDTO(pedidoRepository.save(pedido));
    }

    public Optional<PedidoResponseDTO> obtenerPorId(Long id) {
        return pedidoRepository.findById(id).map(this::mapToDTO);
    }

    public void eliminarPorId(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Optional<PedidoResponseDTO> actualizarPorId(Long idPedido, PedidoRequestDTO dto) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            this.dtoNombreTemporal = dto.getNombreProducto();
            pedido.setIdCli(dto.getIdCli());
            pedido.setFechaPedido(dto.getFechaPedido());

            Estado nuevoEstado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setIdEstado(nuevoEstado);

            if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                PedidoDetalle detalle = pedido.getDetalles().get(0);
                detalle.setIdProc(dto.getIdProc());
                detalle.setCantidad(dto.getCantidad());

                double precioAplicado = (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0)
                        ? dto.getPrecioUnitario()
                        : detalle.getPrecioUnitario();

                detalle.setPrecioUnitario(precioAplicado);
                pedido.setTotalPagar(precioAplicado * dto.getCantidad());
            }

            return mapToDTO(pedidoRepository.save(pedido));
        });
    }
}