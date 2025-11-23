package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.request.GestionDiaEspecialRequestDTO;
import com.vet.manadawoof.dtos.request.GestionRangoRequestDTO;
import com.vet.manadawoof.dtos.response.*;

import java.util.List;
import java.util.Map;

public interface AsignacionHorarioService {
    // CRUD básico
    AsignacionHorarioResponseDTO crearAsignacion(AsignacionHorarioRequestDTO request);
    
    AsignacionHorarioResponseDTO actualizarAsignacion(Long id, AsignacionHorarioRequestDTO request);
    
    void eliminarAsignacion(Long id);
    
    GestionDiaEspecialResponseDTO gestionarDiaEspecial(GestionDiaEspecialRequestDTO request);
    
    GestionRangoResponseDTO gestionarRangoFechas(GestionRangoRequestDTO request);
    
    List<HistorialHorarioResponseDTO> consultarHistorialHorarios(Long idColaborador, Integer idDiaSemana);
    
    List<HorarioVigenteResponseDTO> verHorariosVigentes(Long idColaborador);
    
    Map<String, Object> resumenHorariosColaborador(Long idColaborador);
    
}
