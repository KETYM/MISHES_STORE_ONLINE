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

// 🌟 IMPORTACIONES SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// 🌟 IMPORTACIONES HATEOAS
import com.example.ms_carroCompras.assembler.CarritoModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "6. Gestión del Carrito de Compras", description = "Operaciones para agregar, ver, modificar y sacar mangas del carrito de compras.")
public class CarritoController {

    @Autowired
    private CarritoService service;

    @Autowired
    private CarritoModelAssembler assembler; // 💡 Inyectamos tu nuevo Assembler

    // 🌟 1. LISTAR TODOS (CON HATEOAS)
    @GetMapping
    @Operation(summary = "1. Listar todos los carritos (Con HATEOAS)", description = "Obtiene todos los registros de los carritos.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> listarTodo() {
        List<EntityModel<Carrito>> carritos = service.listarTodoElCarro().stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).listarTodo()).withSelfRel()));
    }

    // 🌟 2. AGREGAR PRODUCTO
    @PostMapping
    @Operation(summary = "2. Agregar producto al carrito", description = "Añade un nuevo libro/manga al carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente 🛒"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Carrito> agregar(@Valid @RequestBody CarritoDTO dto){
        return ResponseEntity.ok(service.agregarAlCarrito(dto));
    }

    // 🌟 3. VER CARRITO POR CLIENTE
    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "3. Ver el carrito de un cliente", description = "Obtiene la lista de productos enriquecida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<Map<String, Object>>> listarPorCliente(@PathVariable Long idCliente){
        return ResponseEntity.ok(service.verCarritoEnriquecidoPorCliente(idCliente));
    }

    // 🌟 4. ACTUALIZAR CANTIDAD
    @PutMapping("/{idCarrito}")
    @Operation(summary = "4. Actualizar cantidad", description = "Modifica la cantidad de ejemplares en el carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Carrito> actualizarCantidad(
            @PathVariable Long idCarrito,
            @RequestBody Map<String, Integer> body) {
        Integer cantidadNueva = body.get("cantidad");
        return ResponseEntity.ok(service.actualizarCantidad(idCarrito, cantidadNueva));
    }

    // 🌟 5. ELIMINAR PRODUCTO
    @DeleteMapping("/{idCarrito}")
    @Operation(summary = "5. Sacar producto del carrito", description = "Elimina un manga del carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga removido exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long idCarrito) {
        service.eliminarDelCarrito(idCarrito);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Manga removido exitosamente");
        return ResponseEntity.ok(respuesta);
    }
}