package com.example.ms_carroCompras.assembler;

import com.example.ms_carroCompras.controller.CarritoController;
import com.example.ms_carroCompras.model.Carrito;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarritoModelAssembler implements RepresentationModelAssembler<Carrito, EntityModel<Carrito>> {

    @Override
    public EntityModel<Carrito> toModel(Carrito carrito) {
        return EntityModel.of(carrito,
                // Genera el enlace al carrito de un cliente específico
                linkTo(methodOn(CarritoController.class).listarPorCliente(carrito.getIdCliente())).withSelfRel(),
                // Genera el enlace para ver el listado completo (si es que tienes esa función)
                linkTo(methodOn(CarritoController.class).listarTodo()).withRel("ver-todos-los-carritos")
        );
    }
}