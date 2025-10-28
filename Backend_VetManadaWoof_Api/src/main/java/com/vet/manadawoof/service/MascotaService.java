package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;

import java.util.List;

// Servicio de negocio para la entidad Mascota
public interface MascotaService {
    
    // Listar todas las mascotas
    List<MascotaResponseDTO> listarMascotas();
    
    // Crear una nueva mascota
    MascotaResponseDTO crearMascota(MascotaRequestDTO request);
    
    // Actualizar una mascota existente
    MascotaResponseDTO actualizarMascota(MascotaRequestDTO request);
    
    // Eliminar (lógica) una mascota por ID
    MascotaResponseDTO eliminarMascota(Long id);
}
