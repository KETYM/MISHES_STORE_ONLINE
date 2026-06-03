package com.example.ms_carroCompras.controller;

import com.example.ms_carroCompras.dto.CarritoDTO;
import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.services.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService service;

    @GetMapping
    public List<Carrito> listarTodo() {
        return service.listarTodoElCarro();
    }

    @PostMapping
    public ResponseEntity<Carrito> agregar(@Valid @RequestBody CarritoDTO dto){
        return ResponseEntity.ok(service.agregarAlCarrito(dto));
    }

    // 🌟 ACTUALIZADO: Ahora este GET responde la lista con IDs, cantidades y NOMBRES de mangas reales
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Map<String, Object>>> listarPorCliente(@PathVariable Long idCliente){
        return ResponseEntity.ok(service.verCarritoEnriquecidoPorCliente(idCliente));
    }

    // 🌟 NUEVO ENDPOINT: PUT para cambiar la cantidad (ej: /api/carrito/4)
    @PutMapping("/{idCarrito}")
    public ResponseEntity<Carrito> actualizarCantidad(
            @PathVariable Long idCarrito,
            @RequestBody Map<String, Integer> body) {

        Integer cantidadNueva = body.get("cantidad");
        Carrito actualizado = service.actualizarCantidad(idCarrito, cantidadNueva);
        return ResponseEntity.ok(actualizado);
    }

    // 🌟 NUEVO ENDPOINT: DELETE para sacar un manga del carro (ej: /api/carrito/4)
    @DeleteMapping("/{idCarrito}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long idCarrito) {
        service.eliminarDelCarrito(idCarrito);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Manga removido del carrito de compras exitosamente");
        return ResponseEntity.ok(respuesta);
    }
}