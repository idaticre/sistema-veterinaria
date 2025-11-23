package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.AsignacionHorarioDetalleResponseDTO;
import com.vet.manadawoof.dtos.response.AsignacionHorarioResponseDTO;
import com.vet.manadawoof.entity.AsignacionHorarioDetalleEntity;
import com.vet.manadawoof.entity.AsignacionHorarioEntity;
import org.springframework.stereotype.Component;

@Component
public class AsignacionHorarioMapper {
    
    public AsignacionHorarioResponseDTO toResponse(AsignacionHorarioEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return AsignacionHorarioResponseDTO.builder().id(entity.getId()).idColaborador(entity.getColaborador().getId()).nombreColaborador(entity.getColaborador().getEntidad().getNombre()).idHorarioBase(entity.getHorarioBase().getId()).nombreHorarioBase(entity.getHorarioBase().getNombre()).idDiaSemana(entity.getDia().getId()).nombreDia(entity.getDia().getNombre()).ordenDia(entity.getDia().getOrden()).fechaInicioVigencia(entity.getFechaInicioVigencia()).fechaFinVigencia(entity.getFechaFinVigencia()).motivoCambio(entity.getMotivoCambio()).fechaAsignacion(entity.getFechaAsignacion()).activo(entity.getActivo()).build();
    }
    
    public AsignacionHorarioDetalleResponseDTO toDetalleResponse(AsignacionHorarioDetalleEntity entity) {
        if(entity == null) {
            return null;
        }
        
        String tipoHorario;
        if(entity.getHoraInicio() == null && entity.getHoraFin() == null) {
            tipoHorario = "DESCANSO";
        } else if(entity.getEsExcepcion() != null && entity.getEsExcepcion()) {
            tipoHorario = "PERSONALIZADO";
        } else {
            tipoHorario = "BASE";
        }
        
        return AsignacionHorarioDetalleResponseDTO.builder()
                .idDetalle(entity.getIdDetalle())
                .idAsignacion(entity.getAsignacion().getId())
                .fecha(entity.getFecha())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .esExcepcion(entity.getEsExcepcion())
                .tipoHorario(tipoHorario)
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}
