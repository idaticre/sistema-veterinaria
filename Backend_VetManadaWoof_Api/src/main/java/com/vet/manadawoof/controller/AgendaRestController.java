package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;

import com.vet.manadawoof.service.AgendaService;
import com.vet.manadawoof.service.IngresoServicioService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaRestController {

    private final AgendaService service;
    private final IngresoServicioService ingresoServicioService;

    // 🔥 CREAR CITA
    @PostMapping
    public ResponseEntity<ApiResponse<AgendaResponseDTO>> crear(
            @RequestBody AgendaRequestDTO dto
    ) {

        AgendaResponseDTO response =
                service.crear(dto);

        if (
                response.getMensaje() != null &&
                response.getMensaje().startsWith("ERROR")
        ) {

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            false,
                            response.getMensaje(),
                            null
                    )
            );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                response.getMensaje(),
                                response
                        )
                );
    }

    // 🔥 ACTUALIZAR CITA
    @PutMapping
    public ResponseEntity<ApiResponse<AgendaResponseDTO>> actualizar(
            @RequestBody AgendaRequestDTO dto
    ) {

        AgendaResponseDTO response =
                service.actualizar(dto);

        if (
                response.getMensaje() != null &&
                response.getMensaje().startsWith("ERROR")
        ) {

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            false,
                            response.getMensaje(),
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response.getMensaje(),
                        response
                )
        );
    }

    // 🔥 LISTAR CITAS
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AgendaResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<AgendaResponseDTO> citas =
                service.listar(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lista de citas",
                        citas
                )
        );
    }

    // 🔥 LISTAR CITAS POR CLIENTE
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<AgendaResponseDTO>>>
    listarPorCliente(
            @PathVariable Long idCliente
    ) {

        List<AgendaResponseDTO> lista =
                service.listarPorCliente(idCliente);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Citas del cliente",
                        lista
                )
        );
    }

    // 🔥 OBTENER CITA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgendaResponseDTO>>
    obtenerPorId(
            @PathVariable Long id
    ) {

        AgendaResponseDTO cita =
                service.obtenerPorId(id);

        if (cita == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Cita no encontrada",
                                    null
                            )
                    );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Cita encontrada",
                        cita
                )
        );
    }

    // 🔥 NUEVO ENDPOINT
    // OBTENER SERVICIOS DE UNA CITA
    @GetMapping("/{id}/servicios")
    public ResponseEntity<?> listarServiciosAgenda(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                Map.of(
                        "data",
                        ingresoServicioService
                                .listarPorAgenda(id)
                )
        );
    }
}