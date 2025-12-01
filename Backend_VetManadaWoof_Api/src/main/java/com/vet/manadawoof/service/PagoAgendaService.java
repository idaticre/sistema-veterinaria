package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.PagoAgendaRequestDTO;
import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;

import java.util.List;

public interface PagoAgendaService {
    
    // Registra un nuevo pago para una cita
    PagoAgendaResponseDTO crear(PagoAgendaRequestDTO dto);
    
    // Elimina un pago
    PagoAgendaResponseDTO eliminar(Long idPago, Long idAgenda);
    
    PagoAgendaResponseDTO obtenerPorId(Long id);
    
    // Lista pagos de una cita
    List<PagoAgendaResponseDTO> listarPorAgenda(Long idAgenda);
}
