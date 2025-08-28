package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import com.vet.manadawoof.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario-rol")
@RequiredArgsConstructor
public class UsuarioRolRestController {

    private final UsuarioRolService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crearRol(@RequestParam Long usuarioId, @RequestParam Long rolId) {
        String mensaje = service.crearRolUsuario(usuarioId, rolId);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarRol(@RequestParam Long usuarioId, @RequestParam Long rolId) {
        String mensaje = service.eliminarRolUsuario(usuarioId, rolId);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioRolEntity>> listarRoles(@RequestParam(required = false) Long usuarioId) {
        List<UsuarioRolEntity> lista = service.listarRolesUsuario(usuarioId);
        return ResponseEntity.ok(lista);
    }
}
