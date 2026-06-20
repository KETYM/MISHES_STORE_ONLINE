package com.producto.ms_producto.assembler;

import com.producto.ms_producto.controller.LibroController;
import com.producto.ms_producto.model.Libro;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component // 💡 Muy importante para poder inyectarlo en el controlador
public class LibroModelAssembler implements RepresentationModelAssembler<Libro, EntityModel<Libro>> {

    @Override
    public EntityModel<Libro> toModel(Libro libro) {
        // Aquí centralizamos la creación de los links.
        // Cada vez que un libro pase por aquí, saldrá con estos 3 enlaces pegados.
        return EntityModel.of(libro,
                linkTo(methodOn(LibroController.class).obtenerPorId(libro.getIdLibro())).withSelfRel(),
                linkTo(methodOn(LibroController.class).listar()).withRel("volver-al-catalogo"),
                linkTo(methodOn(LibroController.class).eliminar(libro.getIdLibro())).withRel("eliminar-producto")
        );
    }
}