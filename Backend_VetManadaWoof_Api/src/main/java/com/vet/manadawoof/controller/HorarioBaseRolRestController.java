package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.HorarioBaseRolRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HorarioBaseRolResponseDTO;
import com.vet.manadawoof.service.HorarioBaseRolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/horarios-base-roles")
@RequiredArgsConstructor
public class HorarioBaseRolRestController {
    private final HorarioBaseRolService service;
    
    @PostMapping
    public ResponseEntity<ApiResponse<HorarioBaseRolResponseDTO>> asignar(
            @Valid @RequestBody HorarioBaseRolRequestDTO request
    ) {
        try {
            HorarioBaseRolResponseDTO creado = service.asignarHorarioARol(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Horario asignado correctamente al rol", creado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            service.eliminarAsignacion(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Asignación eliminada correctamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<HorarioBaseRolResponseDTO>>> listar() {
        List<HorarioBaseRolResponseDTO> lista = service.listarTodos();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lista de asignaciones obtenida correctamente", lista));
    }
    
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<ApiResponse<List<HorarioBaseRolResponseDTO>>> listarPorRol(
            @PathVariable Integer idRol
    ) {
        try {
            List<HorarioBaseRolResponseDTO> lista = service.listarPorRol(idRol);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Horarios del rol obtenidos correctamente", lista));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/horario-base/{idHorarioBase}")
    public ResponseEntity<ApiResponse<List<HorarioBaseRolResponseDTO>>> listarPorHorarioBase(
            @PathVariable Integer idHorarioBase
    ) {
        try {
            List<HorarioBaseRolResponseDTO> lista = service.listarPorHorarioBase(idHorarioBase);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Roles del horario obtenidos correctamente", lista));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/dia/{idDiaSemana}")
    public ResponseEntity<ApiResponse<List<HorarioBaseRolResponseDTO>>> listarPorDia(
            @PathVariable Integer idDiaSemana
    ) {
        try {
            List<HorarioBaseRolResponseDTO> lista = service.listarPorDia(idDiaSemana);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Horarios del día obtenidos correctamente", lista));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HorarioBaseRolResponseDTO>> obtener(@PathVariable Long id) {
        try {
            HorarioBaseRolResponseDTO encontrado = service.obtenerPorId(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Asignación encontrada", encontrado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
