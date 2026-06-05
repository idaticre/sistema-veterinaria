package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;
import com.vet.manadawoof.entity.IngresoServicioEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class IngresoServicioMapper {

    public IngresoServicioResponseDTO toDto(IngresoServicioEntity entity) {

        if (entity == null) return null;

        BigDecimal subtotal =
                entity.getValorServicio()
                        .multiply(
                                BigDecimal.valueOf(
                                        entity.getCantidad()
                                )
                        );

        return IngresoServicioResponseDTO.builder()

                .id(entity.getId())

                .codigo(entity.getCodigo())

                .idAgenda(
                        entity.getAgenda() != null
                                ? entity.getAgenda().getId()
                                : null
                )

                .idServicio(
                        entity.getServicio() != null
                                ? entity.getServicio().getId()
                                : null
                )

                .idColaborador(
                        entity.getColaborador() != null
                                ? entity.getColaborador().getId()
                                : null
                )

                .idVeterinario(
                        entity.getVeterinario() != null
                                ? entity.getVeterinario().getId()
                                : null
                )

                .cantidad(entity.getCantidad())

                .duracionMin(entity.getDuracionMin())

                .valorServicio(entity.getValorServicio())

                .observaciones(entity.getObservaciones())

                .fechaRegistro(entity.getFechaRegistro())

                // 🔥 NOMBRE DEL SERVICIO
                .descripcion(
                        entity.getServicio() != null
                                ? entity.getServicio().getNombre()
                                : "Servicio"
                )

                // 🔥 NOMBRE DEL VETERINARIO
                .nombreVeterinario(
    entity.getColaborador() != null &&
    entity.getColaborador().getEntidad() != null
        ? entity.getColaborador()
                .getEntidad()
                .getNombre()
        : "No asignado"
)

                // 🔥 SUBTOTAL
                .subtotal(subtotal)

                .build();
    }
}