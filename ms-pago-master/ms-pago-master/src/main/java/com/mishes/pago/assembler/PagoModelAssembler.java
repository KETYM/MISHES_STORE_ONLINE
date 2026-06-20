
package com.mishes.pago.assembler;

import com.mishes.pago.controller.PagoController;
import com.mishes.pago.dto.PagoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoResponseDTO, EntityModel<PagoResponseDTO>> {

    @Override
    public EntityModel<PagoResponseDTO> toModel(PagoResponseDTO pago) {
        return EntityModel.of(pago,
                linkTo(methodOn(PagoController.class).obtenerPorId(pago.getIdPag())).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodas()).withRel("historial-pagos")
        );
    }
}