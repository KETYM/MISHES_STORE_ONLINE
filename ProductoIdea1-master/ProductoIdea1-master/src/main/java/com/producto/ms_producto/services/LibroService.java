package com.producto.ms_producto.services;

import com.producto.ms_producto.dto.LibroDTO;
import com.producto.ms_producto.exception.RecursoDuplicadoException;
import com.producto.ms_producto.model.Libro;
import com.producto.ms_producto.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<Libro> listarTodos(){
        return libroRepository.findAll();
    }

    public Libro guardar(LibroDTO dto) {

        if (libroRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            throw new RecursoDuplicadoException("El libro con ISBN " + dto.getIsbn() + " ya existe.");
        }

        Libro libro = new Libro();
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setPrecio(dto.getPrecio());
        libro.setStock(dto.getStock());
        libro.setIsbn(dto.getIsbn());

        return libroRepository.save(libro);
    }

    public Libro buscarPorIsbn(String isbn){
        return libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RecursoDuplicadoException("No encontramos el libro con ISBN: " + isbn));
    }
}