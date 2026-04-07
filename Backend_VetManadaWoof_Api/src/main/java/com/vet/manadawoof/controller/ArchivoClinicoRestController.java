package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.service.ArchivoClinicoService;
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
@RequestMapping("/api/archivos-clinicos")
@RequiredArgsConstructor
public class ArchivoClinicoRestController {

    private final ArchivoClinicoService service;

    /**
     * Subir archivo clínico asociado a un registro de atención
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> subir(
            @Valid @RequestBody ArchivoClinicoRequestDTO dto
    ) {
        ArchivoClinicoResponseDTO response = service.subir(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    /**
     * Eliminar archivo clínico por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        ArchivoClinicoResponseDTO response = service.eliminar(id);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
    }

    /**
     * Obtener archivo clínico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> obtenerPorId(
            @PathVariable Long id
    ) {
        ArchivoClinicoResponseDTO archivo = service.obtenerPorId(id);

        if (archivo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Archivo no encontrado", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Archivo encontrado", archivo));
    }

    /**
     * Listar archivos de un registro de atención
     */
    @GetMapping("/registro/{idRegistroAtencion}")
    public ResponseEntity<ApiResponse<List<ArchivoClinicoResponseDTO>>> listarPorRegistro(
            @PathVariable Long idRegistroAtencion
    ) {
        List<ArchivoClinicoResponseDTO> archivos = service.listarPorRegistro(idRegistroAtencion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Archivos obtenidos", archivos));
    }

    /**
     * Listar archivos de un registro de atención (paginado)
     */
    @GetMapping("/registro/{idRegistroAtencion}/paginado")
    public ResponseEntity<ApiResponse<Page<ArchivoClinicoResponseDTO>>> listarPorRegistroPaginado(
            @PathVariable Long idRegistroAtencion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ArchivoClinicoResponseDTO> archivos = service.listarPorRegistroPaginado(idRegistroAtencion, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Archivos obtenidos", archivos));
    }
}