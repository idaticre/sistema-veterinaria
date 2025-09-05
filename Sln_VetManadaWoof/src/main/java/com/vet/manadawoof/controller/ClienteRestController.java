package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.ClienteEntity;
import com.vet.manadawoof.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {

    private final ClienteService service;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody ClienteEntity cliente) {
        String mensaje = service.registrarCliente(cliente);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody ClienteEntity cliente) {
        String mensaje = service.actualizarCliente(cliente);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> obtenerPorId(@PathVariable Long id) {
        ClienteEntity cliente = service.findById(id);
        return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ClienteEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}
