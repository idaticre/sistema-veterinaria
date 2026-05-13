package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ComprobanteResponseDTO>
    generarComprobante( 
            @RequestBody
            ComprobanteRequestDTO request
    ) {
        ComprobanteResponseDTO response = comprobanteService.generarComprobante(request);
        return ResponseEntity.ok(response);
    }
}
