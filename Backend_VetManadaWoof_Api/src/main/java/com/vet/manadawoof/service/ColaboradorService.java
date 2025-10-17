package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;

import java.util.List;

// Servicio para gestionar colaboradores
public interface ColaboradorService {
    
    // Registra un nuevo colaborador
    ColaboradorResponseDTO registrar(ColaboradorRequestDTO dto);
    
    // Actualiza los datos de un colaborador existente
    ColaboradorResponseDTO actualizar(ColaboradorRequestDTO dto);
    
    // Lista todos los colaboradores
    List<ColaboradorResponseDTO> listar();
    
    // Busca un colaborador por su ID
    ColaboradorResponseDTO obtenerPorId(Long idColaborador);
    
    // Elimina (lógicamente o físicamente) un colaborador
    ColaboradorResponseDTO eliminar(Long idColaborador);
}
