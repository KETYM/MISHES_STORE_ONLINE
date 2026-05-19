package com.mishes.pedido.controller;

import com.mishes.pedido.dto.ListaPedidosClienteResponseDTO;
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

    // 1. LISTAR TODOS
    @GetMapping
    public List<PedidoResponseDTO> obtenerTodas() {
        return pedidoService.obtenerTodas();
    }

    // 2. BUSCAR POR PRODUCTO (Ruta corregida para evitar ambigüedad)
    @GetMapping("/producto")
    public List<PedidoResponseDTO> obtenerPorProductoId(@RequestParam Long productoId) {
        return pedidoService.obtenerPorProductoId(productoId);
    }

    // 3. BUSCAR POR CLIENTE (Ruta corregida para evitar ambigüedad)
    @GetMapping("/cliente")
    public List<PedidoResponseDTO> obtenerPorClienteId(@RequestParam Long clienteId) {
        return pedidoService.obtenerPorClienteId(clienteId);
    }

    @GetMapping("{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> agregar(@Valid @RequestBody PedidoRequestDTO pedido) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.guardar(pedido));
    }

    @PutMapping("{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO pedido) {
        return pedidoService.actualizarPorId(id, pedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 💡 ELIMINAR PEDIDO (Actualizado con tu firma con mensaje amigable)
    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, String>> eliminarPorId(@PathVariable Long id) {
        pedidoService.eliminarPorId(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Pedido eliminado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("listar-pedidos")
    public ListaPedidosClienteResponseDTO obtenerTotalCliente(@RequestParam Long clienteId) {
        return pedidoService.listarPedidosDeCliente(clienteId);
    }
}