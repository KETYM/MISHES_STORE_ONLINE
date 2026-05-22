package com.producto.ms_producto.exception;

import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException; // 🌟 Importación clave para capturar el error de BD
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Mantiene el manejo de errores de validación de DTOs de tu grupo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errores);
    }

    // 2. Mantiene la excepción manual de recurso duplicado de tu grupo
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> handleDuplicado(RecursoDuplicadoException ex) {
        Map<String, String> error = new java.util.HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 3. Mantiene la excepción de recurso no encontrado de tu grupo
    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ConfigDataResourceNotFoundException ex) {
        Map<String, String> error = new java.util.HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 🌟 4. NUESTRO APORTE MAESTRO: Intercepta los choques reales de ISBN duplicado en MySQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();

        // Si el mensaje de la BD menciona la columna del ISBN, damos la respuesta bonita en español
        if (ex.getMessage() != null && ex.getMessage().contains("productos.isbn")) {
            error.put("error", "Conflict");
            error.put("mensaje", "El ISBN ingresado ya se encuentra registrado en el sistema.");
        } else {
            error.put("error", "Conflict");
            error.put("mensaje", "Error de integridad: Se intentó duplicar un dato único en la base de datos.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // Devuelve un hermoso 409 Conflict
    }
}