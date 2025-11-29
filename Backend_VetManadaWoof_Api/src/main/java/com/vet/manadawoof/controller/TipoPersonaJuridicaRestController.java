package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import com.vet.manadawoof.service.TipoPersonaJuridicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipo-persona-juridica")
@RequiredArgsConstructor
public class TipoPersonaJuridicaRestController {
    
    private final TipoPersonaJuridicaService service;
    
    @PostMapping
    public ResponseEntity<TipoPersonaJuridicaEntity> crear(@RequestBody TipoPersonaJuridicaEntity request) {
        TipoPersonaJuridicaEntity creado = service.crearTipoPersonaJuridica(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TipoPersonaJuridicaEntity> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoPersonaJuridicaEntity request
    ) {
        TipoPersonaJuridicaEntity actualizado = service.actualizarTipoPersonaJuridica(id, request);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminarTipoPersonaJuridica(id);
        return ResponseEntity.ok("Tipo persona jurídica eliminado correctamente");
    }
    
    @GetMapping
    public ResponseEntity<List<TipoPersonaJuridicaEntity>> listar() {
        List<TipoPersonaJuridicaEntity> lista = service.listarTiposPersonaJuridica();
        if(lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoPersonaJuridicaEntity> obtener(@PathVariable Integer id) {
        TipoPersonaJuridicaEntity tipo = service.obtenerTipoPersonaJuridicaPorId(id);
        return ResponseEntity.ok(tipo);
    }
}
