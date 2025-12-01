package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.RegistrarCitaAtendidaRequestDTO;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AtencionMedicaService {
    
    AtencionMedicaResponseDTO registrarCitaAtendida(RegistrarCitaAtendidaRequestDTO dto);
    
    AtencionMedicaResponseDTO crear(AtencionMedicaRequestDTO dto);
    
    AtencionMedicaResponseDTO actualizar(AtencionMedicaRequestDTO dto);
    
    AtencionMedicaResponseDTO eliminar(Long id);
    
    AtencionMedicaResponseDTO obtenerPorId(Long id);
    
    List<AtencionMedicaResponseDTO> listarPorHistoria(Long idHistoriaClinica);
    
    Page<AtencionMedicaResponseDTO> listarPorHistoriaPaginado(Long idHistoriaClinica, Pageable pageable);
    
    List<AtencionMedicaResponseDTO> listarPorVeterinario(Long idVeterinario);
}
