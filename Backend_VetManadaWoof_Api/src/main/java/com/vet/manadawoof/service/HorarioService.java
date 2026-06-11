package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.HorarioRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioResponseDTO;

import java.util.List;
import java.util.Map;

public interface HorarioService {

    // POST /asignar-horario
    HorarioResponseDTO asignarHorario(HorarioRequestDTO request);

    // PUT /asignar-horario/{id}
    HorarioResponseDTO editarHorario(Long id, HorarioRequestDTO request);

    // DELETE /eliminar-horario/{trabajadorId}
    void eliminarHorariosPorColaborador(Long trabajadorId);

    // GET /horarios
    List<HorarioResponseDTO> listarTodos();

    // GET /horarios?dia={diaId}
    Map<String, Object> listarPorDia(Integer diaId);

    // GET /horarios?colaborador={colaboradorId}
    Map<String, Object> listarPorColaborador(Long colaboradorId);
}