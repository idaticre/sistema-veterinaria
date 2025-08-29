package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.service.EntidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/entidades")
@RequiredArgsConstructor
public class EntidadRestController {

    private final EntidadService service;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody EntidadEntity entidad) {
        String mensaje = service.registrarEntidad(entidad);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody EntidadEntity entidad) {
        String mensaje = service.actualizarEntidad(entidad);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadEntity> obtenerPorId(@PathVariable Long id) {
        EntidadEntity entidad = service.findById(id);
        return entidad != null ? ResponseEntity.ok(entidad) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<EntidadEntity>> listarTodas() {
        return ResponseEntity.ok(service.findAll());
    }
}
