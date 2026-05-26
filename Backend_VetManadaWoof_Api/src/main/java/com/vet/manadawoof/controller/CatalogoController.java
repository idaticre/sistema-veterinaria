package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.TipoIGVResponseDTO;
import com.vet.manadawoof.dtos.response.TipoMonedaResponseDTO;
import com.vet.manadawoof.dtos.response.TiposComprobantesResponseDTO;
import com.vet.manadawoof.dtos.response.TiposPercepcionesResponseDTO;
import com.vet.manadawoof.service.CatalogoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService cataloService;

    /** GET /api/catalogos/monedas → sp_listar_monedas */
    @GetMapping("/monedas")
    public ResponseEntity<List<TipoMonedaResponseDTO>> listarMonedas() {
        return ResponseEntity.ok(cataloService.listarMonedas());
    }

    /** GET /api/catalogos/tipos-comprobantes → sp_listar_tipos_comprobantes */
    @GetMapping("/tipos-comprobantes")
    public ResponseEntity<List<TiposComprobantesResponseDTO>> listarTiposComprobantes() {
        return ResponseEntity.ok(cataloService.listarTiposComprobantes());
    }

    /** GET /api/catalogos/tipos-percepciones → sp_listar_tipos_percepciones */
    @GetMapping("/tipos-percepciones")
    public ResponseEntity<List<TiposPercepcionesResponseDTO>> listarTiposPercepciones() {
        return ResponseEntity.ok(cataloService.listarTiposPercepciones());
    }

    /** GET /api/catalogos/tipos-igv → sp_listar_tipos_igv */
    @GetMapping("/tipos-igv")
    public ResponseEntity<List<TipoIGVResponseDTO>> listarTiposIgv() {
        return ResponseEntity.ok(cataloService.listarTiposIgv());
    }
}
