package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.AuditoriaEntity;
import com.vet.manadawoof.service.AuditoriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {
    
    private final AuditoriaService auditoriaService;
    
    @GetMapping
    public ResponseEntity<List<AuditoriaEntity>> listar(){
        return ResponseEntity.ok(auditoriaService.listar());
    }
    
}
