package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.AsignacionHorarioResponseDTO;
import com.vet.manadawoof.service.AsignacionHorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// Controlador REST que gestiona la asignación de horarios a colaboradores
// Interactúa con el servicio que ejecuta los SPs correspondientes
public class AsignacionHorarioRestController {
    
    private final AsignacionHorarioService service;
    
    // Asigna o actualiza un horario para un colaborador en un día específico
    @PostMapping("/dia")
    public ResponseEntity<ApiResponse<AsignacionHorarioResponseDTO>> asignarDia(
            @RequestBody AsignacionHorarioRequestDTO dto
    ) {
        ApiResponse<AsignacionHorarioResponseDTO> response = service.asignarHorarioDia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Desasigna (desactiva) un horario para un colaborador en un día específico
    @PostMapping("/dia/desasignar")
    public ResponseEntity<ApiResponse<AsignacionHorarioResponseDTO>> desasignarDia(
            @RequestBody AsignacionHorarioRequestDTO dto
    ) {
        ApiResponse<AsignacionHorarioResponseDTO> response = service.desasignarHorarioDia(dto);
        return ResponseEntity.ok(response);
    }
    
    // Asigna un mismo horario a toda la semana laboral (lunes a sábado)
    @PostMapping("/semana")
    public ResponseEntity<ApiResponse<List<AsignacionHorarioResponseDTO>>> asignarSemana(
            @RequestBody AsignacionHorarioRequestDTO dto
    ) {
        ApiResponse<List<AsignacionHorarioResponseDTO>> response = service.asignarHorarioSemana(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Desasigna todos los horarios de lunes a sábado para un colaborador
    @PostMapping("/semana/desasignar/{idColaborador}")
    public ResponseEntity<ApiResponse<String>> desasignarSemana(@PathVariable Long idColaborador) {
        ApiResponse<String> response = service.desasignarHorarioSemana(idColaborador);
        return ResponseEntity.ok(response);
    }
    
    // Lista todas las asignaciones de horarios activas
    @GetMapping
    public ResponseEntity<ApiResponse<List<AsignacionHorarioResponseDTO>>> listar() {
        ApiResponse<List<AsignacionHorarioResponseDTO>> response = service.listarAsignaciones();
        return ResponseEntity.ok(response);
    }
}
