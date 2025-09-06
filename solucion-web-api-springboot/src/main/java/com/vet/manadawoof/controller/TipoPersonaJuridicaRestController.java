package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import com.vet.manadawoof.service.TipoPersonaJuridicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipos-persona-juridica")
@RequiredArgsConstructor
public class TipoPersonaJuridicaRestController {

    private final TipoPersonaJuridicaService service;

    @GetMapping
    public ResponseEntity<List<TipoPersonaJuridicaEntity>> listar() {
        return ResponseEntity.ok(service.listarTiposPersonaJuridica());
    }

    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody TipoPersonaJuridicaEntity entity) {
        return ResponseEntity.ok(service.registrarTipoPersonaJuridica(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(
            @PathVariable Integer id, @RequestBody TipoPersonaJuridicaEntity entity) {
        entity.setId(id);
        return ResponseEntity.ok(service.actualizarTipoPersonaJuridica(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.eliminarTipoPersonaJuridica(id));
    }
}
