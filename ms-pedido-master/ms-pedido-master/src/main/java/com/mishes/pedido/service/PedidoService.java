package com.mishes.pedido.service;

import com.mishes.pedido.client.ClienteClient;
import com.mishes.pedido.client.ProductoClient;
import com.mishes.pedido.dto.*;
import com.mishes.pedido.model.Estado;
import com.mishes.pedido.model.Pedido;
import com.mishes.pedido.repository.EstadoRepository;
import com.mishes.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoClient productoClient;
    private final ClienteClient clienteClient;

    private PedidoResponseDTO mapToDTO(Pedido pedido) {
        ProductoResponseDTO producto = productoClient.obtenerPorId(pedido.getIdProc());

        // 💡 Modificado: Ahora pasamos directamente el objeto Estado que tiene Pedido al método de validación
        return new PedidoResponseDTO(
                pedido.getIdPed(),
                pedido.getIdCli(),
                pedido.getIdProc(),
                pedido.getFechaPedido(),
                validarEstado(pedido.getIdEstado()).getNombreEstado(),
                pedido.getCantidad(),
                producto.getPrecio(),
                redondear(producto.getPrecio() * pedido.getCantidad())
        );
    }

    // 💡 Modificado: Ahora recibe el objeto Estado completo y valida que no venga nulo ni vacío en la BD
    private Estado validarEstado(Estado estado) {
        if (estado == null || estado.getIdEstado() == null) {
            throw new RuntimeException("El pedido no tiene un estado asignado");
        }
        return estadoRepository.findById(estado.getIdEstado())
                .orElseThrow(() -> new RuntimeException("No existe el estado con ID " + estado.getIdEstado()));
    }

    private double redondear(double valor) {
        return BigDecimal
                .valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public List<PedidoResponseDTO> obtenerTodas() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO guardar(PedidoRequestDTO pedido) {
        // 💡 Modificado: Buscamos el objeto Estado en la BD usando el Short que viene del DTO antes de crear el Pedido
        Estado estadoExistente = estadoRepository.findById(pedido.getIdEstado())
                .orElseThrow(() -> new RuntimeException("No existe el estado con ID " + pedido.getIdEstado()));

        Pedido nuevo = new Pedido(
                null,
                pedido.getIdCli(),
                pedido.getIdProc(),
                pedido.getFechaPedido(),
                estadoExistente, // 💡 Ahora le pasamos el objeto Estado completo al constructor
                pedido.getCantidad()
        );
        log.info("Pedido agregado: {}", pedido);
        return mapToDTO(pedidoRepository.save(nuevo));
    }

    public Optional<PedidoResponseDTO> obtenerPorId(Long idPedido) {
        try {
            return pedidoRepository.findById(idPedido).map(this::mapToDTO);
        } catch (NullPointerException e) {
            throw new RuntimeException("No existe el pedido " + idPedido);
        }
    }

    public List<PedidoResponseDTO> obtenerPorClienteId(Long idPedido) {
        List<PedidoResponseDTO> pedidos = new ArrayList<>();
        for (PedidoResponseDTO p : obtenerTodas())
            if (idPedido.equals(p.getIdCli()))
                pedidos.add(p);
        return pedidos;
    }

    public List<PedidoResponseDTO> obtenerPorProductoId(Long idProducto) {
        List<PedidoResponseDTO> pedidos = new ArrayList<>();
        for (PedidoResponseDTO p : obtenerTodas())
            if (idProducto.equals(p.getIdProc()))
                pedidos.add(p);
        return pedidos;
    }

    public ListaPedidosClienteResponseDTO listarPedidosDeCliente(Long idCliente) {
        ClienteResponseDTO cliente = clienteClient.obtenerPorId(idCliente);
        List<ListaProductosClienteDTO> pedidos = new ArrayList<>();
        List<ProductoResponseDTO> productos = new ArrayList<>();
        ProductoResponseDTO producto;
        PedidoResponseDTO ultimoPedido = null;
        int cantidadTotal = 0;

        List<PedidoResponseDTO> listaPedidos = obtenerTodas();
        listaPedidos.sort(Comparator.comparing(PedidoResponseDTO::getFechaPedido));

        for (PedidoResponseDTO p : listaPedidos.reversed()) {
            if (!idCliente.equals(p.getIdCli()))
                continue;
            else if (ultimoPedido == null)
                ultimoPedido = p;
            else if (!ultimoPedido.getFechaPedido().equals(p.getFechaPedido())) {
                pedidos.add(new ListaProductosClienteDTO(
                        cantidadTotal,
                        calcularPrecioTotal(productos),
                        ultimoPedido.getFechaPedido(),
                        ultimoPedido.getNombreEstado(),
                        productos
                ));
                ultimoPedido = p;
                cantidadTotal = 0;
                productos.clear();
            }
            producto = productoClient.obtenerPorId(p.getIdProc());
            producto.setCantidad(p.getCantidad());
            productos.add(producto);
            cantidadTotal += p.getCantidad();
        }
        if (!productos.isEmpty())
            pedidos.add(new ListaProductosClienteDTO(
                    cantidadTotal,
                    calcularPrecioTotal(productos),
                    ultimoPedido.getFechaPedido(),
                    ultimoPedido.getNombreEstado(),
                    productos
            ));
        return new ListaPedidosClienteResponseDTO(
                idCliente,
                cliente.getRutCli(),
                cliente.getNombreCompleto(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                (ultimoPedido != null) ? ultimoPedido.getFechaPedido() : null,
                pedidos
        );
    }

    public Optional<PedidoResponseDTO> actualizarPorId(Long idPedido, PedidoRequestDTO dto) {
        try {
            Optional<PedidoResponseDTO> actualizado = pedidoRepository.findById(idPedido).map(pedido -> {
                pedido.setIdCli(dto.getIdCli());
                pedido.setIdProc(dto.getIdProc());
                pedido.setFechaPedido(dto.getFechaPedido());
                pedido.setCantidad(dto.getCantidad());

                // 💡 Modificado: Si también quieren actualizar el estado, buscamos el nuevo objeto Estado
                Estado nuevoEstado = estadoRepository.findById(dto.getIdEstado())
                        .orElseThrow(() -> new RuntimeException("No existe el estado con ID " + dto.getIdEstado()));
                pedido.setIdEstado(nuevoEstado);

                return mapToDTO(pedidoRepository.save(pedido));
            });
            log.info("Pedido {} actualizado: {}", idPedido, actualizado);
            return actualizado;
        } catch (NullPointerException e) {
            throw new RuntimeException("No existe el pedido " + idPedido);
        }
    }

    public void eliminarPorId(Long idPedido) {
        pedidoRepository.deleteById(idPedido);
        log.info("Pedido {} eliminado", idPedido);
    }

    public double calcularPrecioTotal(List<ProductoResponseDTO> productos) {
        double total = 0.0;
        for (ProductoResponseDTO p : productos)
            total += p.getPrecio() * p.getCantidad();
        return redondear(total);
    }
}