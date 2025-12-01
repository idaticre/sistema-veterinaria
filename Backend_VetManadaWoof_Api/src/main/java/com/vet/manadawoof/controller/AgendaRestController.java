package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AgendaRestController {
    
    private final AgendaService service;
    
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<AgendaResponseDTO>> crear(@RequestBody AgendaRequestDTO dto) {
        AgendaResponseDTO response = service.crear(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<AgendaResponseDTO>> actualizar(@RequestBody AgendaRequestDTO dto) {
        AgendaResponseDTO response = service.actualizar(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AgendaResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendaResponseDTO> citas = service.listar(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de citas", citas));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgendaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        AgendaResponseDTO cita = service.obtenerPorId(id);
        
        if(cita == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Cita no encontrada", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Cita encontrada", cita));
    }
}
