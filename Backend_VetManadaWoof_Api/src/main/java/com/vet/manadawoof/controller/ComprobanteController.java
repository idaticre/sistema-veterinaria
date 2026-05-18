package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.service.ComprobanteService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {
    private final ComprobanteService
            comprobanteService;

    @PostMapping("/generar")
    public ResponseEntity<Map<String, String>> generarComprobante( //<ComprobanteResponseDTO>
            @RequestBody
            ComprobanteRequestDTO request
    ) {
        //ComprobanteResponseDTO response = comprobanteService.generarComprobante(request);
        return ResponseEntity.ok(comprobanteService.generarComprobante(request));//response
    }
    
    @GetMapping
    public List<ComprobanteResponseDTO> listarComprobantes() {
        return comprobanteService.listarComprobantes();
    }

    @GetMapping("/{id}")
    public ComprobanteResponseDTO obtenerComprobante(
            @PathVariable Long id
    ) {
        return comprobanteService.obtenerComprobante(id);
    }
}
