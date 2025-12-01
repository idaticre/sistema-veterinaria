package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArchivoClinicoService {
    
    ArchivoClinicoResponseDTO subir(ArchivoClinicoRequestDTO dto);
    
    ArchivoClinicoResponseDTO eliminar(Long idArchivo);
    
    ArchivoClinicoResponseDTO obtenerPorId(Long id);
    
    List<ArchivoClinicoResponseDTO> listarPorRegistro(Long idRegistroAtencion);
    
    Page<ArchivoClinicoResponseDTO> listarPorRegistroPaginado(Long idRegistroAtencion, Pageable pageable);
}
