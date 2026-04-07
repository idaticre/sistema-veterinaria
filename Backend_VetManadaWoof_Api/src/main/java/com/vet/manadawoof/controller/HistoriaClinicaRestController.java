package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.service.HistoriaClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/historia-clinica")
@RequiredArgsConstructor
public class HistoriaClinicaRestController {

    private final HistoriaClinicaService service;

    /**
     * Crear historia clínica (una por mascota)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> crear(
            @Valid @RequestBody HistoriaClinicaRequestDTO dto
    ) {
        HistoriaClinicaResponseDTO response = service.crear(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    /**
     * Obtener historia clínica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> obtenerPorId(
            @PathVariable Long id
    ) {
        HistoriaClinicaResponseDTO historia = service.obtenerPorId(id);

        if (historia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Historia clinica no encontrada", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Historia clinica encontrada", historia));
    }

    /**
     * Obtener historia clínica por mascota
     */
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> obtenerPorMascota(
            @PathVariable Long idMascota
    ) {
        HistoriaClinicaResponseDTO historia = service.obtenerPorMascota(idMascota);

        if (historia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Historia clinica no encontrada para esta mascota", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Historia clinica encontrada", historia));
    }

    /**
     * Listar todas las historias clínicas activas (paginado)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<HistoriaClinicaResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HistoriaClinicaResponseDTO> historias = service.listar(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Historias clinicas obtenidas", historias));
    }

    /**
     * Consultar historial completo de una mascota
     * Devuelve: datos mascota + historia + registros de atención + archivos
     */
    @GetMapping("/mascota/{idMascota}/historial")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarHistorialMascota(
            @PathVariable Long idMascota
    ) {
        Map<String, Object> resultado = service.consultarHistorialMascota(idMascota);
        String mensaje = (String) resultado.get("mensaje");

        if (mensaje != null && mensaje.startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, mensaje, null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Historial obtenido correctamente", resultado));
    }
}