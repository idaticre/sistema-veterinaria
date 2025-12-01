package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.IngresoServicioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;
import com.vet.manadawoof.service.IngresoServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingresos-servicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class IngresoServicioRestController {
    
    private final IngresoServicioService service;
    
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<IngresoServicioResponseDTO>> crear(@RequestBody IngresoServicioRequestDTO dto) {
        IngresoServicioResponseDTO response = service.crear(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<IngresoServicioResponseDTO>> actualizar(@RequestBody IngresoServicioRequestDTO dto) {
        IngresoServicioResponseDTO response = service.actualizar(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        } return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @DeleteMapping("/eliminar/{idIngreso}/{idAgenda}")
    public ResponseEntity<ApiResponse<IngresoServicioResponseDTO>> eliminar(@PathVariable Long idIngreso, @PathVariable Long idAgenda) {
        IngresoServicioResponseDTO response = service.eliminar(idIngreso, idAgenda);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, response.getMensaje(), null));
        } return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngresoServicioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        IngresoServicioResponseDTO servicio = service.obtenerPorId(id);
        
        if(servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Servicio no encontrado", null));
        } return ResponseEntity.ok(new ApiResponse<>(true, "Servicio encontrado", servicio));
    }
    
    @GetMapping("/agenda/{idAgenda}")
    public ResponseEntity<ApiResponse<List<IngresoServicioResponseDTO>>> listarPorAgenda(@PathVariable Long idAgenda) {
        List<IngresoServicioResponseDTO> servicios = service.listarPorAgenda(idAgenda);
        return ResponseEntity.ok(new ApiResponse<>(true, "Servicios obtenidos", servicios));
    }
}
