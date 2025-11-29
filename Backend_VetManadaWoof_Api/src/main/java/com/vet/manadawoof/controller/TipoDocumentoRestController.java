package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.service.TipoDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipo-documento")
@RequiredArgsConstructor
public class TipoDocumentoRestController {
    
    private final TipoDocumentoService service;
    
    @PostMapping
    public ResponseEntity<TipoDocumentoEntity> crear(@RequestBody TipoDocumentoEntity request) {
        TipoDocumentoEntity creado = service.crearTdoc(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoEntity> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoDocumentoEntity request
    ) {
        TipoDocumentoEntity actualizado = service.actualizarTdoc(id, request);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminarTdoc(id);
        return ResponseEntity.ok("Tipo de documento eliminado correctamente");
    }
    
    @GetMapping
    public ResponseEntity<List<TipoDocumentoEntity>> listar() {
        List<TipoDocumentoEntity> lista = service.listarTdoc();
        if(lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoEntity> obtener(@PathVariable Integer id) {
        TipoDocumentoEntity tipoDocumento = service.obtenerTdocPorId(id);
        return ResponseEntity.ok(tipoDocumento);
    }
}
