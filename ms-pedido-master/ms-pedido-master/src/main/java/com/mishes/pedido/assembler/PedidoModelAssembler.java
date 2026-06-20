package com.mishes.pedido.assembler;

import com.mishes.pedido.controller.PedidoController;
import com.mishes.pedido.dto.PedidoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<PedidoResponseDTO, EntityModel<PedidoResponseDTO>> {

    @Override
    public EntityModel<PedidoResponseDTO> toModel(PedidoResponseDTO pedido) {
        return EntityModel.of(pedido,
                linkTo(methodOn(PedidoController.class).obtenerPorId(pedido.getIdPed())).withSelfRel(),
                linkTo(methodOn(PedidoController.class).obtenerTodas()).withRel("volver-al-historial")
        );
    }
}