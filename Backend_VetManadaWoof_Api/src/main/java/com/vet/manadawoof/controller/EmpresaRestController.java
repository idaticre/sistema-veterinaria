package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaRestController {
    
    private final EmpresaService service;
    
    @GetMapping
    public ResponseEntity<List<EmpresaEntity>> listarEmpresas() {
        List<EmpresaEntity> empresas = service.listarEmpresas(); if(empresas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } return ResponseEntity.ok(empresas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaEntity> obtenerEmpresa(@PathVariable Integer id) {
        EmpresaEntity empresa = service.obtenerEmpresa(id); if(empresa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } return ResponseEntity.ok(empresa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEmpresa(@PathVariable Integer id, @RequestBody EmpresaEntity empresa) {
        EmpresaEntity existente = service.obtenerEmpresa(id); if(existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La empresa con id " + id + " no existe");
        }
        
        empresa.setId(id); service.actualizarEmpresa(empresa);
        
        return ResponseEntity.status(HttpStatus.OK).body("Empresa actualizada correctamente");
    }
}
