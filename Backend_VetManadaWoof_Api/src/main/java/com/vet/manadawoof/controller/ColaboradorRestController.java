package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ColaboradorRestController {

    private final ColaboradorService service;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>>
    registrar(@RequestBody ColaboradorRequestDTO dto) {
        ColaboradorResponseDTO response = service.registrar(dto);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>>
    actualizar(@RequestBody ColaboradorRequestDTO dto) {
        if (dto.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false, "ID de colaborador requerido para actualizar",
                            null));
        }
        ColaboradorResponseDTO response = service.actualizar(dto.getId(), dto);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ColaboradorResponseDTO>>> listar() {
        List<ColaboradorResponseDTO> colaboradores = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Lista de colaboradores", colaboradores));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ColaboradorResponseDTO colaborador = service.buscarPorId(id);
        if (colaborador == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false, "Colaborador no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Colaborador encontrado", colaborador));
    }
}
