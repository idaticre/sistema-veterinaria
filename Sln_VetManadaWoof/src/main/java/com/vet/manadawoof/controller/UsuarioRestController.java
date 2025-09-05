package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody UsuarioEntity user) {
        return ResponseEntity.ok(usuarioService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody UsuarioEntity user) {
        user.setId(id);
        return ResponseEntity.ok(usuarioService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.deleteUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> readById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> readAll() {
        return ResponseEntity.ok(usuarioService.findAllUsers());
    }
}
