package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.FiltroAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponseWithMetadata;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class RegistroAsistenciaRestController {
    
    private final RegistroAsistenciaService service;
    
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponseWithMetadata<RegistroAsistenciaResponseDTO>> registrarAsistencia(
            @Valid @RequestBody RegistrarAsistenciaRequestDTO request
    ) {
        try {
            RegistroAsistenciaResponseDTO resultado = service.registrar(request);
            
            var metadata = ApiResponseWithMetadata.Metadata.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .version("1.0")
                    .operation("gestionar_asistencia")
                    .totalRecords(1)
                    .build();
            
            var response = ApiResponseWithMetadata.<RegistroAsistenciaResponseDTO> builder()
                    .success(true)
                    .message(resultado.getMensaje())
                    .data(resultado)
                    .metadata(metadata)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            var metadata = ApiResponseWithMetadata.Metadata.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .version("1.0")
                    .operation("gestionar_asistencia")
                    .totalRecords(0)
                    .build();
            
            var response = ApiResponseWithMetadata.<RegistroAsistenciaResponseDTO> builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .metadata(metadata)
                    .build();
            
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }
    
    
    @PostMapping("/rango")
    public ResponseEntity<ApiResponseWithMetadata<List<RegistroAsistenciaResponseDTO>>> verAsistenciaPorRango(
            @Valid @RequestBody FiltroAsistenciaRequestDTO filtro
    ) {
        List<RegistroAsistenciaResponseDTO> lista = service.verAsistenciaPorRango(
                filtro.getFechaInicio(),
                filtro.getFechaFin(),
                filtro.getIdColaborador(),
                filtro.getIdEstado()
        );
        
        // Calcular estadísticas
        long totalPresentes = lista.stream()
                .filter(r -> "PRESENTE".equals(r.getEstadoAsistencia())
                        || "COMPLETADO".equals(r.getEstadoAsistencia()))
                .count();
        
        long totalTardanzas = lista.stream()
                .filter(r -> "TARDANZA".equals(r.getEstadoAsistencia()))
                .count();
        
        long totalDescansos = lista.stream()
                .filter(r -> "DESCANSO_SEMANAL".equals(r.getEstadoAsistencia()))
                .count();
        
        var metadata = ApiResponseWithMetadata.Metadata.builder()
                .timestamp(Instant.now().toEpochMilli())
                .version("1.0")
                .operation("ver_asistencia_por_rango")
                .totalRecords(lista.size())
                .build();
        
        String mensaje = String.format(
                "Registros obtenidos: %d total | %d presentes | %d tardanzas | %d descansos",
                lista.size(), totalPresentes, totalTardanzas, totalDescansos
        );
        
        var response = ApiResponseWithMetadata.<List<RegistroAsistenciaResponseDTO>> builder()
                .success(true)
                .message(mensaje)
                .data(lista)
                .metadata(metadata)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
