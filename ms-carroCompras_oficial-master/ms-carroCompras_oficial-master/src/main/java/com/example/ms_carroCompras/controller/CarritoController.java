package com.example.ms_carroCompras.controller;

import com.example.ms_carroCompras.dto.CarritoDTO;
import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.services.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Carrito>> listarPorCliente(@PathVariable Long idCliente){
        return ResponseEntity.ok(service.verCarritoPorCliente(idCliente));
    }
}
