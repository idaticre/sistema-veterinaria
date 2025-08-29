package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins ="http://localhost:5173")
@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorRestController {

    private final ColaboradorService service;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody ColaboradorEntity colaborador) {
        return ResponseEntity.ok(service.registrarColaborador(colaborador));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody ColaboradorEntity colaborador) {
        return ResponseEntity.ok(service.actualizarColaborador(colaborador));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorEntity> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ColaboradorEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}
