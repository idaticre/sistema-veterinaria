package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.VeterinarioEntity;
import com.vet.manadawoof.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioRestController {

    private final VeterinarioService service;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody VeterinarioEntity veterinario) {
        String mensaje = service.registrarVeterinario(veterinario);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody VeterinarioEntity veterinario) {
        String mensaje = service.actualizarVeterinario(veterinario);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioEntity> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<VeterinarioEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}
