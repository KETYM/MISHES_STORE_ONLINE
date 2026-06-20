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

// 🌟 IMPORTACIONES SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// 🌟 IMPORTACIONES HATEOAS
import com.mishes.pedido.assembler.PedidoModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/pedidos")
@RequiredArgsConstructor
@Tag(name = "4. Gestión de Pedidos", description = "Operaciones para crear, consultar, actualizar y eliminar los pedidos.")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoModelAssembler assembler; // 💡 Inyectamos el Assembler

    // 🌟 1. LISTAR TODOS (CON HATEOAS)
    @GetMapping
    @Operation(summary = "1. Listar todos los pedidos (Con HATEOAS)", description = "Obtiene un registro histórico de todos los pedidos.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public ResponseEntity<CollectionModel<EntityModel<PedidoResponseDTO>>> obtenerTodas() {
        List<EntityModel<PedidoResponseDTO>> pedidos = pedidoService.obtenerTodas().stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).obtenerTodas()).withSelfRel()));
    }

    // 🌟 2. BUSCAR POR ID (CON HATEOAS)
    @GetMapping("{id}")
    @Operation(summary = "2. Buscar pedido por ID (Con HATEOAS)", description = "Obtiene los detalles completos de un pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<EntityModel<PedidoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🌟 3. CREAR PEDIDO
    @PostMapping
    @Operation(summary = "3. Crear un nuevo pedido", description = "Registra un nuevo pedido. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado 🚀"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PedidoResponseDTO> agregar(@Valid @RequestBody PedidoRequestDTO pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.guardar(pedido));
    }

    // 🌟 4. ACTUALIZAR
    @PutMapping("{id}")
    @Operation(summary = "4. Actualizar pedido", description = "Modifica los datos de un pedido. Requiere rol ADMIN.")
    public ResponseEntity<PedidoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO pedido) {
        return pedidoService.actualizarPorId(id, pedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🌟 5. ELIMINAR
    @DeleteMapping("{id}")
    @Operation(summary = "5. Eliminar pedido", description = "Elimina un pedido del sistema. Requiere rol ADMIN.")
    public ResponseEntity<Map<String, String>> eliminarPorId(@PathVariable Long id) {
        pedidoService.eliminarPorId(id);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Pedido eliminado correctamente");
        return ResponseEntity.ok(respuesta);
    }
}