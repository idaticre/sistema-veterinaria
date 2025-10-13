package com.vet.manadawoof.controller;
//algunas cosas pude ponerlas en otras clases, como en el servicio, 
//pero las dejé en el controlador para no tocar el resto del código y evitar errores. 
//Lo hice así para que todo funcione sin romper nada y mantenerlo más simple por ahora.
//agregue nuevos import
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.entity.RolEntity;

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
    public ResponseEntity<UsuarioRolEntity> crear(
            @RequestParam Integer usuarioId,
            @RequestParam Integer rolId) {

        // Cree la entidad manualmente
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(usuarioId);

        RolEntity rol = new RolEntity();
        rol.setId(rolId);

        UsuarioRolEntity entity = new UsuarioRolEntity();
        entity.setUsuario(usuario);
        entity.setRol(rol);

        UsuarioRolEntity creado = service.crearUsuarioRol(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRolEntity>> listar() {
        List<UsuarioRolEntity> list = service.listar();
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<UsuarioRolEntity>> listar(@PathVariable Integer usuarioId) {
        List<UsuarioRolEntity> list = service.listarRolesPorUsuario(usuarioId);
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/detallado")
    public ResponseEntity<List<Map<String, Object>>> listarDetallado() {
        List<UsuarioRolEntity> list = service.listar();
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (UsuarioRolEntity ur : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ur.getId());
            map.put("usuarioId", ur.getUsuario().getId());
            map.put("usuarioUsername", ur.getUsuario().getUsername());
            map.put("rolId", ur.getRol().getId());
            map.put("rolNombre", ur.getRol().getNombre());
            map.put("fechaAsignacion", ur.getFechaAsignacion());
            resultado.add(map);
        }

        return ResponseEntity.ok(resultado);
    }

    // agrege este nuevo método para eliminar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPorId(@PathVariable Integer id) {
        service.eliminarUsuarioRol(id, null, null);
        return ResponseEntity.ok("UsuarioRol eliminado correctamente");
    }

    // Esto podriamos eliminarlo tal vez...?
    @DeleteMapping
    public ResponseEntity<String> eliminar(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer rolId) {
        service.eliminarUsuarioRol(id, usuarioId, rolId);
        return ResponseEntity.ok("UsuarioRol eliminado correctamente");
    }
}
