package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.service.TipoDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-documento")
@RequiredArgsConstructor
public class TipoDocumentoRestController {

    private final TipoDocumentoService tipoDocumentoService;

    @GetMapping
    public ResponseEntity<List<TipoDocumentoEntity>> listarTiposDocumento() {
        return ResponseEntity.ok(tipoDocumentoService.listarTiposDocumento());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoEntity> obtenerTipoDocumento(@PathVariable Long id) {
        TipoDocumentoEntity tipoDocumento = tipoDocumentoService.obtenerTipoDocumento(id);
        if (tipoDocumento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tipoDocumento);
    }

    @PostMapping
    public ResponseEntity<String> guardarTipoDocumento(@RequestBody TipoDocumentoEntity tipoDocumento) {
        String resultado = tipoDocumentoService.guardarTipoDocumento(tipoDocumento);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTipoDocumento(@PathVariable Long id) {
        String resultado = tipoDocumentoService.eliminarTipoDocumento(id);
        return ResponseEntity.ok(resultado);
    }
}
