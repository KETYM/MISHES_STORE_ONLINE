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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 🌟 POST ACTUALIZADO: Usamos LinkedHashMap para garantizar el orden visual
    @PostMapping
    public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody PagoRequestDTO pago) {
        PagoResponseDTO pagoProcesado = pagoService.guardar(pago);

        // 🛠️ Cambiamos HashMap por LinkedHashMap:
        Map<String, Object> respuesta = new java.util.LinkedHashMap<>();

        // Al escribir este primero, se garantiza que aparezca arriba en Postman:
        respuesta.put("mensaje", "😊😊😊¡Compra realizada con éxito en Mishes Store! 🚀🌸");
        respuesta.put("detallePago", pagoProcesado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // 🔗 PUT http://localhost:8083/api/pagos/{id}
    @PutMapping("{id}")
    public ResponseEntity<PagoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PagoRequestDTO pago) {
        return pagoService.actualizarPorId(id, pago)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🌟 DELETE CORREGIDO: Ahora devuelve un mensaje explícito de éxito en Postman
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> eliminarPorId(@PathVariable Long id) {
        pagoService.eliminarPorId(id);

        java.util.HashMap<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "El registro de pago fue eliminado correctamente de Mishes Store");

        return org.springframework.http.ResponseEntity.ok(respuesta);
    }

    // 🔗 GET http://localhost:8083/api/pagos/lista-cliente?id=X
    // 💡 Le agregamos la anotación @GetMapping y cambiamos la ruta para que sea un endpoint válido de consulta
    @GetMapping("/lista-cliente")
    public ListaPagosClienteDTO listarPagosDeCliente(@RequestParam Long id) {
        return pagoService.listarPagosDeCliente(id);
    }

}