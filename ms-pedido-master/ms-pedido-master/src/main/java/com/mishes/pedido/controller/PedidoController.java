package com.mishes.pedido.controller;

import com.mishes.pedido.dto.PedidoRequestDTO;
import com.mishes.pedido.dto.PedidoResponseDTO;
import com.mishes.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // 1. LISTAR TODOS (GET http://localhost:8084/api/pedidos)
    @GetMapping
    public List<PedidoResponseDTO> obtenerTodas() {
        return pedidoService.obtenerTodas();
    }

    // 2. BUSCAR POR ID (GET http://localhost:8084/api/pedidos/1)
    @GetMapping("{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREAR PEDIDO (POST http://localhost:8084/api/pedidos)
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> agregar(@Valid @RequestBody PedidoRequestDTO pedido) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.guardar(pedido));
    }

    // 4. ACTUALIZAR ESTADO O PEDIDO (PUT http://localhost:8084/api/pedidos/1)
    @PutMapping("{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO pedido) {
        return pedidoService.actualizarPorId(id, pedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. ELIMINAR PEDIDO (DELETE http://localhost:8084/api/pedidos/1)
    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, String>> eliminarPorId(@PathVariable Long id) {
        pedidoService.eliminarPorId(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Pedido eliminado correctamente");

        return ResponseEntity.ok(respuesta);
    }
}