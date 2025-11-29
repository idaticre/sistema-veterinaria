package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;

import java.util.Map;

public interface HistoriaClinicaService {
    
    // Crea historia clínica (una vez por mascota)
    HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO dto);
    
    // Registra una atención médica
    AtencionMedicaResponseDTO registrarAtencion(AtencionMedicaRequestDTO dto);
    
    // Actualiza una atención médica
    AtencionMedicaResponseDTO actualizarAtencion(AtencionMedicaRequestDTO dto);
    
    // Sube archivo clínico
    ArchivoClinicoResponseDTO subirArchivo(ArchivoClinicoRequestDTO dto);
    
    // Elimina archivo clínico
    ArchivoClinicoResponseDTO eliminarArchivo(Long idArchivo);
    
    // Consulta historial completo de mascota
    Map<String, Object> consultarHistorialMascota(Long idMascota);
    
    // Consulta registro específico de atención
    Map<String, Object> consultarRegistroAtencion(Long idRegistro);
}
