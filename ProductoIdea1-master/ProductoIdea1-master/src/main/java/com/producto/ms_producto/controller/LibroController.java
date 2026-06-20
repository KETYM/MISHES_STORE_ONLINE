package com.producto.ms_producto.controller;

import com.producto.ms_producto.dto.LibroDTO;
import com.producto.ms_producto.model.Libro;
import com.producto.ms_producto.repository.LibroRepository;
import com.producto.ms_producto.services.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 🌟 IMPORTACIONES DE SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// 🌟 IMPORTACIONES DE HATEOAS
import com.producto.ms_producto.assembler.LibroModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
// TÍTULO PRINCIPAL EN SWAGGER
@Tag(name = "3. Gestión de Productos (Catálogo)", description = "Operaciones para ver, agregar, modificar y eliminar los libros o mangas de Mishes Store")
public class LibroController {

    private final LibroService libroService;
    private final LibroRepository libroRepository;
    private final LibroModelAssembler assembler; // 💡 Inyectamos tu nuevo Assembler

    // Inyección limpia por constructor comercial
    public LibroController(LibroService libroService, LibroRepository libroRepository, LibroModelAssembler assembler) {
        this.libroService = libroService;
        this.libroRepository = libroRepository;
        this.assembler = assembler;
    }

    // 🌟 1. LISTAR TODOS LOS PRODUCTOS (CON HATEOAS)
    @GetMapping
    @Operation(summary = "1. Listar todos los productos (Con HATEOAS)", description = "Obtiene el catálogo completo de libros/mangas disponibles en la tienda. Incluye enlaces hipermedia de navegación. Acceso público.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido exitosamente")
    public ResponseEntity<CollectionModel<EntityModel<Libro>>> listar(){
        List<EntityModel<Libro>> libros = libroService.listarTodos().stream()
                .map(assembler::toModel) // La magia del Assembler
                .toList();

        return ResponseEntity.ok(CollectionModel.of(libros,
                linkTo(methodOn(LibroController.class).listar()).withSelfRel()));
    }

    // 🌟 2. OBTENER PRODUCTO POR ID (CON HATEOAS)
    @GetMapping("/{id}")
    @Operation(summary = "2. Buscar producto por ID (Con HATEOAS)", description = "Obtiene los detalles de un libro específico buscando por su ID numérico interno. Incluye enlaces para volver al catálogo o eliminarlo. Acceso público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<EntityModel<Libro>> obtenerPorId(@PathVariable Long id){
        Libro libro = libroRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(assembler.toModel(libro)); // Transformación HATEOAS
    }

    // 🌟 3. OBTENER PRODUCTO POR ISBN
    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "3. Buscar producto por ISBN", description = "Obtiene los detalles de un libro específico buscando por su código ISBN. Acceso público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado (ISBN no registrado)")
    })
    public ResponseEntity<Libro> obtenerPorIsbn(@PathVariable String isbn){
        Libro libro = libroService.buscarPorIsbn(isbn);
        return ResponseEntity.ok(libro);
    }

    // 🌟 4. CREAR UN NUEVO PRODUCTO
    @PostMapping
    @Operation(summary = "4. Registrar un nuevo producto", description = "Agrega un nuevo libro o manga al catálogo. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente 🚀"),
            @ApiResponse(responseCode = "400", description = "Error en los datos (ej. ISBN duplicado o campos vacíos)"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para hacer esto (Forbidden)")
    })
    public ResponseEntity<Libro> crear(@Valid @RequestBody LibroDTO libroDTO){
        return ResponseEntity.ok(libroService.guardar(libroDTO));
    }

    // 🌟 5. ACTUALIZAR PRODUCTO EXISTENTE
    @PutMapping("/{id}")
    @Operation(summary = "5. Actualizar producto existente", description = "Modifica los datos de un libro/manga según su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El producto a actualizar no existe"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para hacer esto (Forbidden)")
    })
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Libro dto) {
        return libroService.actualizarPorId(id, dto)
                .map(libroActualizado -> ResponseEntity.ok(libroActualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // 🌟 6. ELIMINAR PRODUCTO POR ID
    @DeleteMapping("/{id}")
    @Operation(summary = "6. Eliminar producto", description = "Elimina un libro/manga del catálogo a través de su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "El producto a eliminar no existe"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para hacer esto (Forbidden)")
    })
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> eliminar(@PathVariable Long id) {
        libroService.eliminarPorId(id);

        java.util.HashMap<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Producto eliminado correctamente de Mishes Store");

        return org.springframework.http.ResponseEntity.ok(respuesta);
    }
}