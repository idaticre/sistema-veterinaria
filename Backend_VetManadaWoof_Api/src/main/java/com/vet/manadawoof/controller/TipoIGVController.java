package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.entity.TipoIGVEntity;
import com.vet.manadawoof.service.TipoDocumentoService;
import com.vet.manadawoof.service.TipoIGVService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipoIGV")
@RequiredArgsConstructor
public class TipoIGVController {
    
    private final TipoIGVService service;
    
    @GetMapping
    public ResponseEntity<List<TipoIGVEntity>> Listar() {
        List<TipoIGVEntity> lista = service.Listar();
        if(lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lista);
    }
    
}
