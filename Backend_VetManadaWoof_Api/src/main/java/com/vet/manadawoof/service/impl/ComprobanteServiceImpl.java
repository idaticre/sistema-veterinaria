package com.vet.manadawoof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.entity.AgendaEntity;
import com.vet.manadawoof.entity.ComprobanteEntity;
import com.vet.manadawoof.mapper.ComprobanteMapper;
import com.vet.manadawoof.repository.AgendaRepository;
import com.vet.manadawoof.repository.ComprobanteRepository;
import com.vet.manadawoof.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComprobanteServiceImpl implements ComprobanteService {

    private final AgendaRepository agendaRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ComprobanteResponseDTO generarComprobante(ComprobanteRequestDTO request) {

        // 1. VALIDAR AGENDA
        AgendaEntity agenda = agendaRepository.findById(request.getAgendaID())
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada"));

        // 2. SERIE SEGÚN TIPO
        String serie = (request.getTipoComprobanteID() == 1) ? "F001" : "B001";

        // 3. CONVERTIR DETALLES A JSON
        String detallesJson;
        try {
            detallesJson = objectMapper.writeValueAsString(request.getDetalles());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error convirtiendo detalles a JSON");
        }

        // 4. EJECUTAR SP Y CAPTURAR RESULTADO
        Map<String, Object> result = jdbcTemplate.queryForMap(
                """
                CALL sp_insertar_comprobante_full(
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?
                )
                """,
                request.getAgendaID(),
                request.getTipoComprobanteID(),
                serie,
                request.getFechaEmision(),
                request.getFechaVencimiento(),
                request.getTipoMonedaId(),
                request.getTotalGravada(),
                request.getTotalInafecta(),
                request.getTotalExonerada(),
                request.getTotalIGV(),
                request.getTotal(),
                agenda.getCliente().getId(),
                detallesJson
        );

        System.out.println(result);

        ComprobanteEntity comprobante = comprobanteRepository.findTopByOrderByIdDesc();
        
        if(comprobante == null){
            throw new RuntimeException("No se encontró el comprobante generado");
        }

        // 7. MAPEAR RESPONSE
        return ComprobanteMapper.toResponseDTO(comprobante);
    }
}