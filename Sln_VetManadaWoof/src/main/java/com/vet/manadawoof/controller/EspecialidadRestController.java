package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadRestController {

    private final EspecialidadService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody EspecialidadEntity especialidad) {
        return ResponseEntity.ok(service.crearEspecialidad(especialidad));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody EspecialidadEntity especialidad) {
        return ResponseEntity.ok(service.actualizarEspecialidad(especialidad));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarEspecialidad(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EspecialidadEntity>> listar() {
        return ResponseEntity.ok(service.listarEspecialidades());
    }
}