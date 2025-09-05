package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaRestController {

    private final EmpresaService empresaService;

    // GET: Listar todas las empresas
    @GetMapping
    public ResponseEntity<List<EmpresaEntity>> listarEmpresas() {
        List<EmpresaEntity> empresas = empresaService.listarEmpresas();
        return ResponseEntity.ok(empresas);
    }

    // GET: Obtener empresa por ID
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaEntity> obtenerEmpresa(@PathVariable Long id) {
        EmpresaEntity empresa = empresaService.obtenerEmpresa(id);
        return (empresa == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(empresa);
    }

    // PUT: Actualizar empresa existente
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEmpresa(@PathVariable Long id,
                                                    @RequestBody EmpresaEntity empresa) {
        empresa.setId(id);
        String resultado = empresaService.actualizarEmpresa(empresa);
        return ResponseEntity.ok(resultado);
    }
}
