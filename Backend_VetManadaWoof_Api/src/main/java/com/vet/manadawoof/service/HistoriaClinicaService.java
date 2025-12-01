package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface HistoriaClinicaService {
    
    HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO dto);
    
    HistoriaClinicaResponseDTO obtenerPorId(Long id);
    
    HistoriaClinicaResponseDTO obtenerPorMascota(Long idMascota);
    
    Map<String, Object> consultarHistorialMascota(Long idMascota);
    
    Page<HistoriaClinicaResponseDTO> listar(Pageable pageable);
    
}
