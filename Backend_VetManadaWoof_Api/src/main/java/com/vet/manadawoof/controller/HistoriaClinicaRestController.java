package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.service.HistoriaClinicaService;
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
@CrossOrigin(origins = "http://localhost:5173")
public class HistoriaClinicaRestController {
    
    private final HistoriaClinicaService service;
    
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> crear(@RequestBody HistoriaClinicaRequestDTO dto) {
        HistoriaClinicaResponseDTO response = service.crear(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        HistoriaClinicaResponseDTO historia = service.obtenerPorId(id);
        
        if(historia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Historia clínica no encontrada", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Historia clínica encontrada", historia));
    }
    
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponseDTO>> obtenerPorMascota(@PathVariable Long idMascota) {
        HistoriaClinicaResponseDTO historia = service.obtenerPorMascota(idMascota);
        
        if(historia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Historia clínica no encontrada para esta mascota", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Historia clínica encontrada", historia));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<HistoriaClinicaResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HistoriaClinicaResponseDTO> historias = service.listar(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Historias clínicas obtenidas", historias));
    }
    
    @GetMapping("/historial-mascota/{idMascota}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarHistorialMascota(@PathVariable Long idMascota) {
        Map<String, Object> resultado = service.consultarHistorialMascota(idMascota);
        String mensaje = (String) resultado.get("mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, mensaje, null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Historial obtenido correctamente", resultado));
    }
}
