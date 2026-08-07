package com.vet.manadawoof.controller;
import com.vet.manadawoof.entity.SalaEntity;
import com.vet.manadawoof.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalaController {
    private final SalaService salaService;
    @GetMapping
    public ResponseEntity<List<SalaEntity>> listar() {
        return ResponseEntity.ok(salaService.listar());
    }
    @GetMapping("/disponibles")
    public ResponseEntity<List<SalaEntity>> disponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime hora) {
        return ResponseEntity.ok(
                salaService.buscarSalasDisponibles(fecha, hora)
        );
    }
}