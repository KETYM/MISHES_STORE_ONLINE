package com.mishes.pago.service;

import com.mishes.pago.client.ClienteClient;
import com.mishes.pago.client.PedidoClient;
import com.mishes.pago.dto.ClienteResponseDTO;
import com.mishes.pago.dto.ListaPagosClienteDTO;
import com.mishes.pago.dto.PagoRequestDTO;
import com.mishes.pago.dto.PagoResponseDTO;
import com.mishes.pago.model.MetodoPago;
import com.mishes.pago.model.Pago;
import com.mishes.pago.repository.MetodoPagoRepository;
import com.mishes.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ClienteClient clienteClient;
    private final PedidoClient pedidoClient;

    private PagoResponseDTO mapToDTO(Pago pago) {
        return new PagoResponseDTO(
            pago.getIdPag(),
            pago.getIdPed(),
            pago.getIdCli(),
            pago.getFechaPago(),
            pago.getMontoTotal(),
            validarIdMetodoPago(pago.getIdMetodoPago()).getNombreMetodoPago()
        );
    }

    private MetodoPago validarIdMetodoPago(Short idMetodoPago) {
        Optional<MetodoPago> metodoPago = metodoPagoRepository.findById(idMetodoPago);
        if (metodoPago.isEmpty())
            throw new RuntimeException("No existe el metodo de pago " + idMetodoPago);
        return metodoPago.get();
    }

    public List<PagoResponseDTO> obtenerTodas() {
        return pagoRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public PagoResponseDTO guardar(PagoRequestDTO pago) {
        Pago nuevo = new Pago(
            null,
            pago.getIdPed(),
            pago.getIdCli(),
            pago.getFechaPago(),
            pago.getMontoTotal(),
            validarIdMetodoPago(pago.getIdMetodoPago()).getIdMetodoPago()
        );
        log.info("Pago agregado: {}", nuevo);
        return mapToDTO(pagoRepository.save(nuevo));
    }

    public Optional<PagoResponseDTO> obtenerPorId(Long idPago) {
        try {
            return pagoRepository.findById(idPago).map(this::mapToDTO);
        } catch (NullPointerException e) {
            throw new RuntimeException("No existe el pago " + idPago);
        }
    }

    public List<PagoResponseDTO> obtenerPorPedidoId(Long idPedido) {
        return obtenerTodas().stream().filter(pago -> idPedido.equals(pago.getIdPed())).toList();
    }

    public List<PagoResponseDTO> obtenerPorClienteId(Long idCliente) {
        return obtenerTodas().stream().filter(pago -> idCliente.equals(pago.getIdCli())).toList();
    }

    public ListaPagosClienteDTO listarPagosDeCliente(Long idCliente) {
        ClienteResponseDTO cliente = clienteClient.obtenerPorId(idCliente);
        List<PagoResponseDTO> pagos = new ArrayList<>();
        PagoResponseDTO ultimoPago = null;

        List<PagoResponseDTO> listaPagos = obtenerTodas();
        listaPagos.sort(Comparator.comparing(PagoResponseDTO::getFechaPago));

        for (PagoResponseDTO p : listaPagos.reversed()) {
            if (!idCliente.equals(p.getIdCli()))
                continue;
            else if (ultimoPago == null)
                ultimoPago = p;
            else if (!ultimoPago.getFechaPago().equals(p.getFechaPago()))
                ultimoPago = p;
            pagos.add(p);
        }
        return new ListaPagosClienteDTO(
            idCliente,
            cliente.getRutCli(),
            cliente.getNombreCompleto(),
            cliente.getCorreo(),
            cliente.getTelefono(),
            (ultimoPago != null) ? ultimoPago.getFechaPago() : null,
            pagos
        );
    }

    public Optional<PagoResponseDTO> actualizarPorId(Long idPago, PagoRequestDTO dto) {
        try {
            Optional<PagoResponseDTO> actualizado = pagoRepository.findById(idPago).map(pago -> {
                pago.setIdPed(dto.getIdPed());
                pago.setIdCli(dto.getIdCli());
                pago.setFechaPago(dto.getFechaPago());
                pago.setMontoTotal(dto.getMontoTotal());
                pago.setIdMetodoPago(dto.getIdMetodoPago());
                return mapToDTO(pagoRepository.save(pago));
            });
            log.info("Pedido {} actualizado: {}", idPago, actualizado);
            return actualizado;
        } catch (NullPointerException e) {
            throw new RuntimeException("No existe el pago " + idPago);
        }
    }

    public void eliminarPorId(Long idPago) {
        pagoRepository.deleteById(idPago);
        log.info("Pago {} eliminado", idPago);
    }

}
