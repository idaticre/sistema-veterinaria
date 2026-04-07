package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.RegistrarCitaAtendidaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.service.AtencionMedicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atenciones-medicas")
@RequiredArgsConstructor
public class AtencionMedicaRestController {

    private final AtencionMedicaService service;

    /**
     * Registrar cita como atendida (flujo principal)
     * Automáticamente marca cita como ATENDIDA y crea registro en historia clínica
     * Soporta: MEDICA, ESTETICA, HOSPEDAJE, GENERAL
     */
    @PostMapping("/cita/{idAgenda}")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> registrarCitaAtendida(
            @PathVariable Long idAgenda,
            @Valid @RequestBody RegistrarCitaAtendidaRequestDTO dto
    ) {
        dto.setIdAgenda(idAgenda);
        AtencionMedicaResponseDTO response = service.registrarCitaAtendida(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    /**
     * Crear atención manual (casos especiales)
     * Uso: Emergencias sin cita previa, datos retrospectivos
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> crear(
            @Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {
        AtencionMedicaResponseDTO response = service.crear(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    /**
     * Actualizar atención existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {
        dto.setId(id);
        AtencionMedicaResponseDTO response = service.actualizar(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }

    /**
     * Eliminar atención (solo si está en estado editable)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        AtencionMedicaResponseDTO response = service.eliminar(id);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
    }

    /**
     * Obtener atención por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        AtencionMedicaResponseDTO atencion = service.obtenerPorId(id);

        if (atencion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Atención no encontrada", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Atención encontrada", atencion));
    }

    /**
     * Listar atenciones de una historia clínica
     */
    @GetMapping("/historia/{idHistoriaClinica}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorHistoria(
            @PathVariable Long idHistoriaClinica
    ) {
        List<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoria(idHistoriaClinica);
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }

    /**
     * Listar atenciones de una historia clínica (paginado)
     */
    @GetMapping("/historia/{idHistoriaClinica}/paginado")
    public ResponseEntity<ApiResponse<Page<AtencionMedicaResponseDTO>>> listarPorHistoriaPaginado(
            @PathVariable Long idHistoriaClinica,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoriaPaginado(idHistoriaClinica, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }

    /**
     * Listar atenciones de un veterinario
     */
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorVeterinario(
            @PathVariable Long idVeterinario
    ) {
        List<AtencionMedicaResponseDTO> atenciones = service.listarPorVeterinario(idVeterinario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones del veterinario", atenciones));
    }
}