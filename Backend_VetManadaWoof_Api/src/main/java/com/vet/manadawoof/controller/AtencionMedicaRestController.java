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


    @PostMapping("/registrar-cita-atendida")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> registrarCitaAtendida(@Valid @RequestBody RegistrarCitaAtendidaRequestDTO dto
    ) {

        AtencionMedicaResponseDTO response = service.registrarCitaAtendida(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> crear(@Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {

        AtencionMedicaResponseDTO response = service.crear(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }


    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> actualizar(@Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {

        AtencionMedicaResponseDTO response = service.actualizar(dto);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        AtencionMedicaResponseDTO response = service.eliminar(id);

        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        AtencionMedicaResponseDTO atencion = service.obtenerPorId(id);

        if (atencion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Atención médica no encontrada", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Atención encontrada", atencion));
    }


    @GetMapping("/historia/{idHistoriaClinica}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorHistoria(@PathVariable Long idHistoriaClinica
    ) {

        List<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoria(idHistoriaClinica);

        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }


    @GetMapping("/historia-paginado/{idHistoriaClinica}")
    public ResponseEntity<ApiResponse<Page<AtencionMedicaResponseDTO>>> listarPorHistoriaPaginado(@PathVariable Long idHistoriaClinica, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoriaPaginado(idHistoriaClinica, pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }

   
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorVeterinario(@PathVariable Long idVeterinario
    ) {

        List<AtencionMedicaResponseDTO> atenciones = service.listarPorVeterinario(idVeterinario);

        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones del veterinario", atenciones));
    }
}
