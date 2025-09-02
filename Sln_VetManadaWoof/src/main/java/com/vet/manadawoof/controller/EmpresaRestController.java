package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.service.EmpresaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(service.listarEmpresas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaEntity> obtenerEmpresa(@PathVariable Integer id)
    {
        EmpresaEntity empresa = service.obtenerEmpresa(id);
        return empresa != null ? ResponseEntity.ok(empresa) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEmpresa(
            @PathVariable Integer id,
            @RequestBody EmpresaEntity empresa) {
        empresa.setId(id);
        return ResponseEntity.ok(service.actualizarEmpresa(empresa));
    }
}
