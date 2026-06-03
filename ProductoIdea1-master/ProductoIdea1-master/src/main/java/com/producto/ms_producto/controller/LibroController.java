package com.producto.ms_producto.controller;

import com.producto.ms_producto.dto.LibroDTO;
import com.producto.ms_producto.model.Libro;
import com.producto.ms_producto.repository.LibroRepository;
import com.producto.ms_producto.services.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class LibroController {

    private final LibroService libroService;
    private final LibroRepository libroRepository;

    // 💡 Inyección limpia por constructor comercial
    public LibroController(LibroService libroService, LibroRepository libroRepository) {
        this.libroService = libroService;
        this.libroRepository = libroRepository;
    }

    @GetMapping
    public List<Libro> listar(){
        return libroService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody LibroDTO libroDTO){
        return ResponseEntity.ok(libroService.guardar(libroDTO));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Libro> obtenerPorIsbn(@PathVariable String isbn){
        Libro libro = libroService.buscarPorIsbn(isbn);
        return ResponseEntity.ok(libro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(libroRepository.findById(id).orElseThrow());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Libro dto) {
        return libroService.actualizarPorId(id, dto)
                .map(libroActualizado -> ResponseEntity.ok(libroActualizado))
                .orElse(ResponseEntity.notFound().build());
    }
    // 🌟 5. ELIMINAR PRODUCTO POR ID (CRUD COMPLETO)
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> eliminar(@PathVariable Long id) {
        libroService.eliminarPorId(id); // O como se llame tu método en el servicio (ej: productoService.delete(id))

        java.util.HashMap<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Producto eliminado correctamente de Mishes Store");

        return org.springframework.http.ResponseEntity.ok(respuesta);
    }
}