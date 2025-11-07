package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.FiltroAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class RegistroAsistenciaRestController {
    
    private final RegistroAsistenciaService service;
    
    /**
     * Registrar o actualizar la asistencia de un colaborador
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<String>> registrarAsistencia(@Valid @RequestBody RegistrarAsistenciaRequestDTO request
    ) {
        
        String mensaje = service.registrar(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, mensaje));
    }
    
    /**
     * Consultar asistencias por rango de fechas
     * Ejemplo: /api/asistencias?fechaInicio=2025-10-01&fechaFin=2025-10-07
     */
    @PostMapping("/rango")
    public ResponseEntity<ApiResponse<List<RegistroAsistenciaResponseDTO>>> verAsistenciaPorRango(@RequestBody FiltroAsistenciaRequestDTO filtro
    ) {
        
        List<RegistroAsistenciaResponseDTO> lista = service.verAsistenciaPorRango(filtro.getFechaInicio(), filtro.getFechaFin(),
                
                // puede ser null
                filtro.getIdEstado());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de asistencias obtenida correctamente", lista));
    }
    
    
}
