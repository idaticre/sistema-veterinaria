package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.request.GestionDiaEspecialRequestDTO;
import com.vet.manadawoof.dtos.request.GestionRangoRequestDTO;
import com.vet.manadawoof.dtos.response.*;
import com.vet.manadawoof.service.AsignacionHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/asignaciones-horarios")
@RequiredArgsConstructor
public class AsignacionHorarioRestController {
    private final AsignacionHorarioService service;
    
    @PostMapping
    public ResponseEntity<ApiResponseWithMetadata<AsignacionHorarioResponseDTO>> crear(@Valid @RequestBody AsignacionHorarioRequestDTO request
    ) {
        try {
            AsignacionHorarioResponseDTO creado = service.crearAsignacion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseWithMetadata<>(true, "Asignación creada correctamente", creado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseWithMetadata<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWithMetadata<AsignacionHorarioResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody AsignacionHorarioRequestDTO request) {
        try {
            AsignacionHorarioResponseDTO actualizado = service.actualizarAsignacion(id, request);
            return ResponseEntity.ok(new ApiResponseWithMetadata<>(true, "Asignación actualizada correctamente", actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseWithMetadata<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWithMetadata<Void>> eliminar(@PathVariable Long id) {
        try {
            service.eliminarAsignacion(id);
            return ResponseEntity.ok(new ApiResponseWithMetadata<>(true, "Asignación eliminada correctamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseWithMetadata<>(false, e.getMessage(), null));
        }
    }
    
    
    @PostMapping("/gestionar-dia-especial")
    public ResponseEntity<ApiResponseWithMetadata<GestionDiaEspecialResponseDTO>> gestionarDiaEspecial(@Valid @RequestBody GestionDiaEspecialRequestDTO request
    ) {
        try {
            GestionDiaEspecialResponseDTO resultado = service.gestionarDiaEspecial(request);
            
            var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("gestionar_dia_especial").totalRecords(1).build();
            
            var response = ApiResponseWithMetadata.<GestionDiaEspecialResponseDTO> builder().success(true).message(resultado.getMensaje()).data(resultado).metadata(metadata).build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("gestionar_dia_especial").totalRecords(0).build();
            
            var response = ApiResponseWithMetadata.<GestionDiaEspecialResponseDTO> builder().success(false).message(e.getMessage()).data(null).metadata(metadata).build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/gestionar-rango")
    public ResponseEntity<ApiResponseWithMetadata<GestionRangoResponseDTO>> gestionarRango(@Valid @RequestBody GestionRangoRequestDTO request
    ) {
        try {
            GestionRangoResponseDTO resultado = service.gestionarRangoFechas(request);
            
            var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("gestionar_asignar_rango").totalRecords(1).build();
            
            boolean success = "OK".equalsIgnoreCase(resultado.getStatus());
            
            var response = ApiResponseWithMetadata.<GestionRangoResponseDTO> builder().success(success).message(resultado.getMensaje()).data(resultado).metadata(metadata).build();
            
            return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("gestionar_asignar_rango").totalRecords(0).build();
            
            var errorResponse = GestionRangoResponseDTO.builder().status("ERROR").mensaje(e.getMessage()).build();
            
            var response = ApiResponseWithMetadata.<GestionRangoResponseDTO> builder().success(false).message(e.getMessage()).data(errorResponse).metadata(metadata).build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/historial/{idColaborador}")
    public ResponseEntity<ApiResponseWithMetadata<List<HistorialHorarioResponseDTO>>> consultarHistorial(@PathVariable Long idColaborador, @RequestParam(required = false) Integer idDiaSemana) {
        List<HistorialHorarioResponseDTO> historial = service.consultarHistorialHorarios(idColaborador, idDiaSemana);
        
        var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("consultar_historial_horarios").totalRecords(historial.size()).build();
        
        var response = ApiResponseWithMetadata.<List<HistorialHorarioResponseDTO>> builder().success(true).message(String.format("Se encontraron %d registros en el historial", historial.size())).data(historial).metadata(metadata).build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vigentes")
    public ResponseEntity<ApiResponseWithMetadata<List<HorarioVigenteResponseDTO>>> verHorariosVigentes(@RequestParam(required = false) Long idColaborador
    ) {
        List<HorarioVigenteResponseDTO> vigentes = service.verHorariosVigentes(idColaborador);
        
        // Contar alertas de vencimiento próximo
        long proximosACambiar = vigentes.stream().filter(HorarioVigenteResponseDTO :: getProximoACambiar).count();
        
        var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("ver_horarios_vigentes").totalRecords(vigentes.size()).build();
        
        String mensaje = proximosACambiar > 0 ? String.format("Horarios vigentes obtenidos. %d horario(s) próximo(s) a vencer", proximosACambiar) : "Horarios vigentes obtenidos correctamente";
        
        var response = ApiResponseWithMetadata.<List<HorarioVigenteResponseDTO>> builder().success(true).message(mensaje).data(vigentes).metadata(metadata).build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/resumen/{idColaborador}")
    public ResponseEntity<ApiResponseWithMetadata<Map<String, Object>>> resumenSemanal(@PathVariable Long idColaborador
    ) {
        Map<String, Object> resumen = service.resumenHorariosColaborador(idColaborador);
        
        var metadata = ApiResponseWithMetadata.Metadata.builder().timestamp(Instant.now().toEpochMilli()).version("1.0").operation("resumen_horarios_colaborador").totalRecords(1).build();
        
        var response = ApiResponseWithMetadata.<Map<String, Object>> builder().success(true).message("Resumen semanal obtenido correctamente").data(resumen).metadata(metadata).build();
        
        return ResponseEntity.ok(response);
    }
    
}
