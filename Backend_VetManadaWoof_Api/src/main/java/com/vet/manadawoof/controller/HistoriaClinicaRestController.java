package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.service.HistoriaClinicaService;
import lombok.RequiredArgsConstructor;
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
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PostMapping("/registrar-atencion")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> registrarAtencion(@RequestBody AtencionMedicaRequestDTO dto) {
        AtencionMedicaResponseDTO response = service.registrarAtencion(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PutMapping("/actualizar-atencion")
    public ResponseEntity<ApiResponse<AtencionMedicaResponseDTO>> actualizarAtencion(@RequestBody AtencionMedicaRequestDTO dto) {
        AtencionMedicaResponseDTO response = service.actualizarAtencion(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PostMapping("/subir-archivo")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> subirArchivo(@RequestBody ArchivoClinicoRequestDTO dto) {
        ArchivoClinicoResponseDTO response = service.subirArchivo(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @DeleteMapping("/eliminar-archivo/{idArchivo}")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> eliminarArchivo(@PathVariable Long idArchivo) {
        try {
            ArchivoClinicoResponseDTO response = service.eliminarArchivo(idArchivo);
            
            if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, response.getMensaje(), null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error en la operación: " + e.getMessage(), null));
        }
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
    
    @GetMapping("/registro-atencion/{idRegistro}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarRegistroAtencion(@PathVariable Long idRegistro) {
        Map<String, Object> resultado = service.consultarRegistroAtencion(idRegistro);
        String mensaje = (String) resultado.get("mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, mensaje, null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Registro obtenido correctamente", resultado));
    }
}
