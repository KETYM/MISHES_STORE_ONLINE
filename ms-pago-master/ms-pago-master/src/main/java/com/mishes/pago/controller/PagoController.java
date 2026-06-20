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

import java.util.Map;
import java.util.List;
import java.util.HashMap;

// 🌟 IMPORTACIONES SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// 🌟 IMPORTACIONES HATEOAS
import com.mishes.pago.assembler.PagoModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/pagos")
@RequiredArgsConstructor
@Tag(name = "5. Gestión de Pagos", description = "Pasarela interna de pagos.")
public class PagoController {

    private final PagoService pagoService;
    private final PagoModelAssembler assembler;

    // 🌟 1. LISTAR TODOS
    @GetMapping
    @Operation(summary = "1. Listar todos los pagos")
    @ApiResponse(responseCode = "200", description = "Historial obtenido")
    public ResponseEntity<CollectionModel<EntityModel<PagoResponseDTO>>> obtenerTodas() {
        List<EntityModel<PagoResponseDTO>> pagos = pagoService.obtenerTodas().stream()
                .map(assembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(pagos, linkTo(methodOn(PagoController.class).obtenerTodas()).withSelfRel()));
    }

    // 🌟 2. BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "2. Buscar pago por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<EntityModel<PagoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return pagoService.obtenerPorId(id)
                .map(assembler::toModel) // Si existe, lo convierte a HATEOAS
                .map(ResponseEntity::ok) // Si existe, devuelve 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si no existe, devuelve 404
    }

    // 🌟 3. BUSCAR POR CLIENTE
    @GetMapping("/cliente")
    @Operation(summary = "3. Buscar pagos por Cliente")
    @ApiResponse(responseCode = "200", description = "Pagos encontrados")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorClienteId(@RequestParam Long idCliente) {
        return ResponseEntity.ok(pagoService.obtenerPorClienteId(idCliente));
    }

    // 🌟 4. BUSCAR POR PEDIDO
    @GetMapping("/pedido")
    @Operation(summary = "4. Buscar pago por Pedido")
    @ApiResponse(responseCode = "200", description = "Pago encontrado")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorPedidoId(@RequestParam Long idPedido) {
        return ResponseEntity.ok(pagoService.obtenerPorPedidoId(idPedido));
    }

    // 🌟 5. PROCESAR PAGO
    @PostMapping
    @Operation(summary = "5. Procesar un nuevo pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado 🚀"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody PagoRequestDTO pago) {
        PagoResponseDTO pagoProcesado = pagoService.guardar(pago);
        Map<String, Object> respuesta = new java.util.LinkedHashMap<>();
        respuesta.put("mensaje", "¡Compra realizada con éxito! 🚀🌸");
        respuesta.put("detallePago", pagoProcesado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // 🌟 6. ACTUALIZAR PAGO
    @PutMapping("{id}")
    @Operation(summary = "6. Actualizar comprobante de pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<PagoResponseDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody PagoRequestDTO pago) {
        return pagoService.actualizarPorId(id, pago)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🌟 7. ELIMINAR PAGO
    @DeleteMapping("/{id}")
    @Operation(summary = "7. Eliminar / Anular pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, String>> eliminarPorId(@PathVariable Long id) {
        pagoService.eliminarPorId(id);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "El registro fue eliminado correctamente");
        return ResponseEntity.ok(respuesta);
    }

    // 🌟 8. HISTORIAL ENRIQUECIDO
    @GetMapping("/lista-cliente")
    @Operation(summary = "8. Ver historial enriquecido de cliente")
    @ApiResponse(responseCode = "200", description = "Historial obtenido")
    public ListaPagosClienteDTO listarPagosDeCliente(@RequestParam Long id) {
        return pagoService.listarPagosDeCliente(id);
    }
}