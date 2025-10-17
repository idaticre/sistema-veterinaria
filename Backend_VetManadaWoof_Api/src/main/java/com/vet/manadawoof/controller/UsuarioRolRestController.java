package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import com.vet.manadawoof.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios-roles")
@RequiredArgsConstructor
public class UsuarioRolRestController {

    private final UsuarioRolService service;

    @PostMapping
    public ResponseEntity<UsuarioRolEntity> crear(@RequestBody UsuarioRolEntity entity) {
        UsuarioRolEntity creado = service.crearUsuarioRol(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping
    public ResponseEntity<String> eliminar(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer rolId) {
        service.eliminarUsuarioRol(id, usuarioId, rolId);
        return ResponseEntity.ok("UsuarioRol eliminado correctamente");
    }


    @GetMapping
    public ResponseEntity<List<UsuarioRolEntity>> listar() {
        List<UsuarioRolEntity> list = service.listar();
        if (list.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<UsuarioRolEntity>> listarPorUsuario(@PathVariable Integer usuarioId) {
        List<UsuarioRolEntity> list = service.listarRolesPorUsuario(usuarioId);
        if (list.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(list);
    }
}
