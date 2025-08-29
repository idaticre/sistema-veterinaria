package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.service.DiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins ="http://localhost:5173")
@RestController
@RequestMapping("/api/dias")
@RequiredArgsConstructor
public class DiaRestController {

    private final DiaService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody DiaEntity dia) {
        String mensaje = service.crearDia(dia);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody DiaEntity dia) {
        String mensaje = service.actualizarDia(dia);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        String mensaje = service.eliminarDia(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<DiaEntity>> listar() {
        List<DiaEntity> lista = service.listarDias();
        return ResponseEntity.ok(lista);
    }
}
