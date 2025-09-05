package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolRestController {

    private final RolService rolService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody RolEntity rol) {
        return ResponseEntity.ok(rolService.createRol(rol));
    }

    @GetMapping
    public ResponseEntity<List<RolEntity>> readAll() {
        return ResponseEntity.ok(rolService.readRoles(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<RolEntity>> readById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.readRoles(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody RolEntity rol) {
        rol.setId(id);
        return ResponseEntity.ok(rolService.updateRol(rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.deleteRol(id));
    }
}
