package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.service.TipoDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins ="http://localhost:5173")
@RestController
@RequestMapping("/api/tipo-documento")
@RequiredArgsConstructor
public class TipoDocumentoRestController {

    private final TipoDocumentoService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody TipoDocumentoEntity tipoDocumento) {
        return ResponseEntity.ok(service.crearTipoDocumento(tipoDocumento));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody TipoDocumentoEntity tipoDocumento) {
        return ResponseEntity.ok(service.actualizarTipoDocumento(tipoDocumento));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.eliminarTipoDocumento(id));
    }

    @GetMapping
    public ResponseEntity<List<TipoDocumentoEntity>> listar() {
        return ResponseEntity.ok(service.listarTiposDocumento());
    }
}
