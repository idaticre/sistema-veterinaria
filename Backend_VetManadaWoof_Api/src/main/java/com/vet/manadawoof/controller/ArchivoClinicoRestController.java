package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.service.ArchivoClinicoService;
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
@CrossOrigin(origins = "http://localhost:5173")
public class ArchivoClinicoRestController {
    
    private final ArchivoClinicoService service;
    
    @PostMapping("/subir")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> subir(@RequestBody ArchivoClinicoRequestDTO dto) {
        ArchivoClinicoResponseDTO response = service.subir(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @DeleteMapping("/eliminar/{idArchivo}")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> eliminar(@PathVariable Long idArchivo) {
        ArchivoClinicoResponseDTO response = service.eliminar(idArchivo);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, response.getMensaje(), null));
        } return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArchivoClinicoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ArchivoClinicoResponseDTO archivo = service.obtenerPorId(id);
        
        if(archivo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Archivo no encontrado", null));
        } return ResponseEntity.ok(new ApiResponse<>(true, "Archivo encontrado", archivo));
    }
    
    @GetMapping("/registro/{idRegistroAtencion}")
    public ResponseEntity<ApiResponse<List<ArchivoClinicoResponseDTO>>> listarPorRegistro(@PathVariable Long idRegistroAtencion) {
        List<ArchivoClinicoResponseDTO> archivos = service.listarPorRegistro(idRegistroAtencion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Archivos obtenidos", archivos));
    }
    
    @GetMapping("/registro-paginado/{idRegistroAtencion}")
    public ResponseEntity<ApiResponse<Page<ArchivoClinicoResponseDTO>>> listarPorRegistroPaginado(@PathVariable Long idRegistroAtencion, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ArchivoClinicoResponseDTO> archivos = service.listarPorRegistroPaginado(idRegistroAtencion, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Archivos obtenidos", archivos));
    }
}
