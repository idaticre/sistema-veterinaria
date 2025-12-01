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
@CrossOrigin(origins = "http://localhost:5173")
public class AtencionMedicaRestController {
    
    private final AtencionMedicaService service;
    
    /**
     * NUEVO: Registrar cita como atendida (flujo principal)
     * Endpoint: POST /api/atenciones-medicas/registrar-cita-atendida
     * Body: RegistrarCitaAtendidaRequestDTO
     * <p>
     * Automáticamente:
     * - Marca cita como ATENDIDA
     * - Crea registro en historia clínica
     * - Soporta MÉDICA, ESTÉTICA, HOSPEDAJE
     */
    @PostMapping("/registrar-cita-atendida")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> registrarCitaAtendida(@Valid @RequestBody RegistrarCitaAtendidaRequestDTO dto
    ) {
        
        AtencionMedicaResponseDTO response = service.registrarCitaAtendida(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * ANTIGUO: Crear atención manual (casos especiales)
     * Endpoint: POST /api/atenciones-medicas/crear
     * Uso: Emergencias sin cita previa, datos retrospectivos
     */
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> crear(@Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {
        
        AtencionMedicaResponseDTO response = service.crear(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Actualizar atención existente
     * Endpoint: PUT /api/atenciones-medicas/actualizar
     */
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> actualizar(@Valid @RequestBody AtencionMedicaRequestDTO dto
    ) {
        
        AtencionMedicaResponseDTO response = service.actualizar(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Eliminar atención (solo si está editable)
     * Endpoint: DELETE /api/atenciones-medicas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        AtencionMedicaResponseDTO response = service.eliminar(id);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
    }
    
    /**
     * Obtener atención por ID
     * Endpoint: GET /api/atenciones-medicas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        AtencionMedicaResponseDTO atencion = service.obtenerPorId(id);
        
        if(atencion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Atención médica no encontrada", null));
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Atención encontrada", atencion));
    }
    
    /**
     * Listar atenciones de una historia
     * Endpoint: GET /api/atenciones-medicas/historia/{idHistoriaClinica}
     */
    @GetMapping("/historia/{idHistoriaClinica}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorHistoria(@PathVariable Long idHistoriaClinica
    ) {
        
        List<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoria(idHistoriaClinica);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }
    
    /**
     * Listar atenciones de una historia (paginado)
     * Endpoint: GET /api/atenciones-medicas/historia-paginado/{idHistoriaClinica}
     * Query params: page (default 0), size (default 10)
     */
    @GetMapping("/historia-paginado/{idHistoriaClinica}")
    public ResponseEntity<ApiResponse<Page<AtencionMedicaResponseDTO>>> listarPorHistoriaPaginado(@PathVariable Long idHistoriaClinica, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AtencionMedicaResponseDTO> atenciones = service.listarPorHistoriaPaginado(idHistoriaClinica, pageable);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones obtenidas", atenciones));
    }
    
    /**
     * Listar atenciones de un veterinario
     * Endpoint: GET /api/atenciones-medicas/veterinario/{idVeterinario}
     */
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<AtencionMedicaResponseDTO>>> listarPorVeterinario(@PathVariable Long idVeterinario
    ) {
        
        List<AtencionMedicaResponseDTO> atenciones = service.listarPorVeterinario(idVeterinario);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Atenciones del veterinario", atenciones));
    }
}
