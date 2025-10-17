package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HorarioTrabajoResponseDTO;
import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/horarios-trabajo")
@RequiredArgsConstructor
public class HorarioTrabajoRestController {
    
    private final HorarioTrabajoService service;
    
    /**
     * Convierte una entidad HorarioTrabajoEntity a un DTO de respuesta.
     */
    private HorarioTrabajoResponseDTO toDTO(HorarioTrabajoEntity entity) {
        return HorarioTrabajoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .activo(entity.getActivo())
                .build();
    }
    
    /**
     * Crea un nuevo horario de trabajo.
     *
     * @param entity Datos del horario a crear
     * @return Horario creado con mensaje de confirmación
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HorarioTrabajoResponseDTO>> crear(@RequestBody HorarioTrabajoEntity entity) {
        try {
            HorarioTrabajoEntity creado = service.crearHorario(entity);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Horario creado correctamente", toDTO(creado)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    /**
     * Actualiza los datos de un horario existente.
     *
     * @param entity Datos del horario a actualizar
     * @return Horario actualizado con mensaje de éxito
     */
    @PutMapping
    public ResponseEntity<ApiResponse<HorarioTrabajoResponseDTO>> actualizar(@RequestBody HorarioTrabajoEntity entity) {
        try {
            HorarioTrabajoEntity actualizado = service.actualizarHorario(entity);
            return ResponseEntity.ok(new ApiResponse<>(true, "Horario actualizado correctamente", toDTO(actualizado)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    /**
     * Elimina un horario de trabajo por su ID.
     *
     * @param id Identificador del horario
     * @return Mensaje de confirmación o error si no se encuentra
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            String mensaje = service.eliminarHorario(id);
            return ResponseEntity.ok(new ApiResponse<>(true, mensaje, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    /**
     * Lista todos los horarios de trabajo registrados.
     *
     * @return Lista de horarios y mensaje de éxito
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HorarioTrabajoResponseDTO>>> listar() {
        List<HorarioTrabajoResponseDTO> lista = service.listarHorarios().stream()
                .map(this :: toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de horarios obtenida correctamente", lista));
    }
    
    /**
     * Obtiene los datos de un horario específico por su ID.
     *
     * @param id Identificador del horario
     * @return Horario encontrado o mensaje de error si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HorarioTrabajoResponseDTO>> obtener(@PathVariable Integer id) {
        try {
            HorarioTrabajoEntity encontrado = service.obtenerPorId(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Horario encontrado", toDTO(encontrado)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
