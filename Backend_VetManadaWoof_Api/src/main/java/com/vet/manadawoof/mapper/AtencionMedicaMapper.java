package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaRegistroEntity;
import org.springframework.stereotype.Component;

@Component
public class AtencionMedicaMapper {
    
    public AtencionMedicaResponseDTO toDto(HistoriaClinicaRegistroEntity entity) {
        if(entity == null) return null;
        
        return AtencionMedicaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idHistoriaClinica(entity.getHistoriaClinica() != null ? entity.getHistoriaClinica().getId() : null)
                .idAgenda(entity.getAgenda() != null ? entity.getAgenda().getId() : null)
                .idVeterinario(entity.getVeterinario() != null ? entity.getVeterinario().getId() : null)
                .idColaborador(entity.getColaborador() != null ? entity.getColaborador().getId() : null)
                .fechaAtencion(entity.getFechaAtencion())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .motivoConsulta(entity.getMotivoConsulta())
                .anamnesis(entity.getAnamnesis())
                .examenFisico(entity.getExamenFisico())
                .signosVitales(entity.getSignosVitales())
                .pesoKg(entity.getPesoKg())
                .temperaturaC(entity.getTemperaturaC())
                .diagnostico(entity.getDiagnostico())
                .tratamiento(entity.getTratamiento())
                .observaciones(entity.getObservaciones())
                .proximoControl(entity.getProximoControl())
                .idEstado(entity.getEstado() != null ? entity.getEstado().getId() : null)
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}
