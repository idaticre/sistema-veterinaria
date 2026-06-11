package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.HorarioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HorarioResponseDTO;
import com.vet.manadawoof.service.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HorarioRestController {

    private final HorarioService service;

    // GET /api/horarios                    → todos los horarios
    // GET /api/horarios?dia={diaId}        → colaboradores que trabajan ese día (1-7)
    // GET /api/horarios?dia=hoy            → colaboradores que trabajan hoy
    // GET /api/horarios?colaborador={id}   → horarios de un colaborador específico
    @GetMapping("/api/horarios")
    public ResponseEntity<ApiResponse<List<HorarioResponseDTO>>> listar(
            @RequestParam(required = false) String dia,
            @RequestParam(required = false) Long colaborador
    ) {
        // ?colaborador=N
        if (colaborador != null) {
            try {
                Map<String, Object> resultado = service.listarPorColaborador(colaborador);
                String nombreColaborador = (String) resultado.get("nombreColaborador");
                @SuppressWarnings("unchecked")
                List<HorarioResponseDTO> horarios = (List<HorarioResponseDTO>) resultado.get("horarios");

                return ResponseEntity.ok(new ApiResponse<>(true,
                    "Horarios de " + nombreColaborador + " obtenidos correctamente",
                    horarios));
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, e.getMessage(), null));
            }
        }

        // sin parámetros → todos
        if (dia == null) {
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Horarios obtenidos correctamente", service.listarTodos()));
        }

        // ?dia=N o ?dia=hoy
        int diaId;
        if ("hoy".equalsIgnoreCase(dia)) {
            diaId = LocalDate.now().getDayOfWeek().getValue();
        } else {
            try {
                diaId = Integer.parseInt(dia);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false,
                            "El parámetro 'dia' debe ser un número del 1 al 7 o la palabra 'hoy'", null));
            }

            if (diaId < 1 || diaId > 7) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false,
                            "El parámetro 'dia' debe estar entre 1 (lunes) y 7 (domingo)", null));
            }
        }

        try {
            Map<String, Object> resultado = service.listarPorDia(diaId);
            String nombreDia = (String) resultado.get("nombreDia");
            @SuppressWarnings("unchecked")
            List<HorarioResponseDTO> horarios = (List<HorarioResponseDTO>) resultado.get("horarios");

            return ResponseEntity.ok(new ApiResponse<>(true,
                "Colaboradores que trabajan los días " + nombreDia + " obtenidos correctamente",
                horarios));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // POST /api/asignar-horario
    @PostMapping("/api/asignar-horario")
    public ResponseEntity<ApiResponse<HorarioResponseDTO>> asignar(
            @Valid @RequestBody HorarioRequestDTO request
    ) {
        try {
            HorarioResponseDTO creado = service.asignarHorario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Horario asignado correctamente", creado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // PUT /api/asignar-horario/{id}
    @PutMapping("/api/asignar-horario/{id}")
    public ResponseEntity<ApiResponse<HorarioResponseDTO>> editar(
            @PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO request
    ) {
        try {
            HorarioResponseDTO actualizado = service.editarHorario(id, request);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Horario actualizado correctamente", actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // DELETE /api/eliminar-horario/{trabajadorId}
    @DeleteMapping("/api/eliminar-horario/{trabajadorId}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long trabajadorId) {
        try {
            service.eliminarHorariosPorColaborador(trabajadorId);
            return ResponseEntity.ok(
                new ApiResponse<>(true,
                    "Todos los horarios del colaborador eliminados correctamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}