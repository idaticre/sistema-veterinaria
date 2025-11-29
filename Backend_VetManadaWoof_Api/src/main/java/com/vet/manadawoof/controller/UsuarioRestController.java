package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {
    
    private final UsuarioService service;
    
    @PostMapping
    public ResponseEntity<UsuarioEntity> crear(@RequestBody UsuarioEntity request) {
        UsuarioEntity creado = service.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioEntity request
    ) {
        UsuarioEntity actualizado = service.actualizarUsuario(id, request);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminarUsuario(id);
        return ResponseEntity.ok("UsuarioEntity eliminado correctamente");
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> listar() {
        List<UsuarioEntity> lista = service.listarUsuarios();
        if(lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> obtener(@PathVariable Integer id) {
        UsuarioEntity usuario = service.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }
}
