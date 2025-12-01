package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.PagoAgendaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;
import com.vet.manadawoof.service.PagoAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos-agenda")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PagoAgendaRestController {
    
    private final PagoAgendaService service;
    
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<PagoAgendaResponseDTO>> crear(@RequestBody PagoAgendaRequestDTO dto) {
        PagoAgendaResponseDTO response = service.crear(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.ok(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @DeleteMapping("/eliminar/{idPago}/{idAgenda}")
    public ResponseEntity<ApiResponse<PagoAgendaResponseDTO>> eliminar(
            @PathVariable Long idPago,
            @PathVariable Long idAgenda
    ) {
        PagoAgendaResponseDTO response = service.eliminar(idPago, idAgenda);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoAgendaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        PagoAgendaResponseDTO pago = service.obtenerPorId(id);
        
        if(pago == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Pago no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Pago encontrado", pago));
    }
    
    @GetMapping("/agenda/{idAgenda}")
    public ResponseEntity<ApiResponse<List<PagoAgendaResponseDTO>>> listarPorAgenda(@PathVariable Long idAgenda) {
        List<PagoAgendaResponseDTO> pagos = service.listarPorAgenda(idAgenda);
        return ResponseEntity.ok(new ApiResponse<>(true, "Pagos obtenidos", pagos));
    }
}
