package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.UsuarioResponseDTO;
import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioService service;

    @PostMapping("/crear")
    public ResponseEntity<UsuarioResponseDTO> create(@RequestBody UsuarioEntity user) {
        return ResponseEntity.ok(service.createUser(user));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(
            @PathVariable Integer id, @RequestBody UsuarioEntity user) {
        user.setId(id);
        return ResponseEntity.ok(service.updateUser(user));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<UsuarioResponseDTO> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(service.deleteUser(id));
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<UsuarioResponseDTO> readById(@PathVariable Integer id) {
        UsuarioResponseDTO user = service.findUserById(id);
        if (user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> readAll() {
        return ResponseEntity.ok(service.findAllUsers());
    }
}
