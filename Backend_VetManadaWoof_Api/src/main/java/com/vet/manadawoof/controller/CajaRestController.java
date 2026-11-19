package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.CajaAbrirRequestDTO;
import com.vet.manadawoof.dtos.request.CajaCierreRequestDTO;
import com.vet.manadawoof.dtos.request.CajaMovimientoRequestDTO;
import com.vet.manadawoof.dtos.response.CajaEstadoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaArchivoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaMovimientoResponseDTO;
import com.vet.manadawoof.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
public class CajaRestController {

    private final CajaService cajaService;

    @GetMapping("/hoy")
    public CajaEstadoResponseDTO obtenerEstadoHoy() {
        return cajaService.obtenerEstadoHoy();
    }

    @PostMapping("/abrir")
    public CajaEstadoResponseDTO abrirCaja(@Valid @RequestBody CajaAbrirRequestDTO request) {
        return cajaService.abrirCaja(request);
    }

    @PostMapping(value = "/movimientos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CajaMovimientoResponseDTO> registrarMovimiento(
            @Valid @RequestPart("movimiento") CajaMovimientoRequestDTO request,
            @RequestPart(value = "comprobante", required = false) MultipartFile comprobante) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cajaService.registrarMovimiento(request, comprobante));
    }

    @GetMapping("/movimientos")
    public List<CajaMovimientoResponseDTO> listarMovimientos(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Integer medioPagoId,
            @RequestParam(required = false) String tipo) {
        return cajaService.listarMovimientos(desde, hasta, medioPagoId, tipo);
    }

    @PostMapping("/cerrar")
    public CajaEstadoResponseDTO cerrarCaja(@Valid @RequestBody CajaCierreRequestDTO request) {
        return cajaService.cerrarCaja(request);
    }

    @GetMapping("/movimientos/{id}/comprobante")
    public ResponseEntity<byte[]> obtenerComprobante(@PathVariable Long id) {
        CajaArchivoResponseDTO archivo = cajaService.obtenerArchivoComprobante(id);
        String nombre = archivo.nombre() == null ? "comprobante-caja" : archivo.nombre().replaceAll("[\\r\\n\\\"]", "_");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                .contentType(archivo.tipo() == null ? MediaType.APPLICATION_OCTET_STREAM
                        : MediaType.parseMediaType(archivo.tipo()))
                .body(archivo.contenido());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorSolicitud(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "mensaje", error.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException error) {
        FieldError campo = error.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String mensaje = campo == null ? "Revise los datos enviados." : campo.getDefaultMessage();
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("success", false);
        respuesta.put("mensaje", mensaje);
        return ResponseEntity.badRequest().body(respuesta);
    }
}
