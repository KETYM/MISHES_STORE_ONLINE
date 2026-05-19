package com.mishes.pago.controller;

import com.mishes.pago.dto.ListaPagosClienteDTO;
import com.mishes.pago.dto.PagoRequestDTO;
import com.mishes.pago.dto.PagoResponseDTO;
import com.mishes.pago.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    // 🔗 GET http://localhost:8083/api/pagos
    @GetMapping
    public List<PagoResponseDTO> obtenerTodas() {
        return pagoService.obtenerTodas();
    }

    // 🔗 GET http://localhost:8083/api/pagos/cliente?idCliente=X
    // 💡 Le agregamos "/cliente" a la ruta para romper la ambigüedad
    @GetMapping("/cliente")
    public List<PagoResponseDTO> obtenerPorClienteId(@RequestParam Long idCliente) {
        return pagoService.obtenerPorClienteId(idCliente);
    }

    // 🔗 GET http://localhost:8083/api/pagos/pedido?idPedido=X
    // 💡 Le agregamos "/pedido" a la ruta para que no choque con los de arriba
    @GetMapping("/pedido")
    public List<PagoResponseDTO> obtenerPorPedidoId(@RequestParam Long idPedido) {
        return pagoService.obtenerPorPedidoId(idPedido);
    }

    // 🔗 POST http://localhost:8083/api/pagos
    @PostMapping
    public ResponseEntity<PagoResponseDTO> guardar(@Valid @RequestBody PagoRequestDTO pago) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagoService.guardar(pago));
    }

    // 🔗 PUT http://localhost:8083/api/pagos/{id}
    @PutMapping("{id}")
    public ResponseEntity<PagoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PagoRequestDTO pago) {
        return pagoService.actualizarPorId(id, pago)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔗 DELETE http://localhost:8083/api/pagos/{id}
    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        pagoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // 🔗 GET http://localhost:8083/api/pagos/lista-cliente?id=X
    // 💡 Le agregamos la anotación @GetMapping y cambiamos la ruta para que sea un endpoint válido de consulta
    @GetMapping("/lista-cliente")
    public ListaPagosClienteDTO listarPagosDeCliente(@RequestParam Long id) {
        return pagoService.listarPagosDeCliente(id);
    }

}