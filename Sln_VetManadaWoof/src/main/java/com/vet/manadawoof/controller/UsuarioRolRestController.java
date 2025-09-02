package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;
import com.vet.manadawoof.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios-roles")
@RequiredArgsConstructor
public class UsuarioRolRestController {

    private final UsuarioRolService usuarioRolService;

    @PostMapping("/gestionar")
    public ResponseEntity<UsuarioRolResponseDTO> gestionarUsuarioRol(@RequestBody UsuarioRolRequestDTO requestDTO) {
        UsuarioRolResponseDTO response = usuarioRolService.gestionarUsuarioRol(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<UsuarioRolResponseDTO>> listarRolesPorUsuario(@PathVariable Integer usuarioId) {
        List<UsuarioRolResponseDTO> response = usuarioRolService.listarRolesPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
}