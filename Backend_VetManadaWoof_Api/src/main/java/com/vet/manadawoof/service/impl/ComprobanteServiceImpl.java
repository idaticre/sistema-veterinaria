package com.vet.manadawoof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vet.manadawoof.dtos.request.ComprobanteDetalleRequestDTO;
import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.entity.AgendaEntity;
import com.vet.manadawoof.entity.ComprobanteEntity;
import com.vet.manadawoof.mapper.ComprobanteMapper;
import com.vet.manadawoof.repository.AgendaRepository;
import com.vet.manadawoof.repository.ComprobanteRepository;
import com.vet.manadawoof.service.ComprobanteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComprobanteServiceImpl implements ComprobanteService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AgendaRepository agendaRepository;
    private final ComprobanteRepository comprobanteRepository;
    
    @Override
    @Transactional
    public Map<String, String> generarComprobante(ComprobanteRequestDTO request) { //ComprobanteResponseDTO

        // VALIDAR AGENDA

        AgendaEntity agenda = agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada"));

        // DEFINIR SERIE

        String serie = request.getTipoComprobanteId() == 1
                ? "F001"
                : "B001";

        // PROCEDURE CABECERA

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("sp_insertar_comprobante");

        sp.registerStoredProcedureParameter("p_agenda_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo_comprobante_id", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_serie", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_fecha_emision", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_fecha_vencimiento", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo_moneda_id", Integer.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_total_gravada", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_inafecta", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_exonerada", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_igv", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total", java.math.BigDecimal.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_cliente_id", Long.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_id_comprobante", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_numero", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        // SET PARAMS

        sp.setParameter("p_agenda_id", request.getAgendaId());
        sp.setParameter("p_tipo_comprobante_id", request.getTipoComprobanteId());
        sp.setParameter("p_serie", serie);

        sp.setParameter(
                "p_fecha_emision",
                java.sql.Date.valueOf(request.getFechaEmision())
        );

        sp.setParameter(
                "p_fecha_vencimiento",
                java.sql.Date.valueOf(request.getFechaVencimiento())
        );

        sp.setParameter("p_tipo_moneda_id", request.getTipoMonedaId());

        sp.setParameter("p_total_gravada", request.getTotalGravada());
        sp.setParameter("p_total_inafecta", request.getTotalInafecta());
        sp.setParameter("p_total_exonerada", request.getTotalExonerada());
        sp.setParameter("p_total_igv", request.getTotalIGV());
        sp.setParameter("p_total", request.getTotal());

        sp.setParameter("p_cliente_id", agenda.getCliente().getId());

        // EJECUTAR

        sp.execute();

        Long idComprobante = ((Number)
                sp.getOutputParameterValue("p_id_comprobante"))
                .longValue();

        // INSERTAR DETALLES

        for (ComprobanteDetalleRequestDTO detalle : request.getDetalles()) {

            StoredProcedureQuery spDetalle = entityManager
                    .createStoredProcedureQuery("sp_insertar_comprobante_detalle");

            spDetalle.registerStoredProcedureParameter(
                    "p_comprobante_id",
                    Long.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_tipo_unidad_medida_id",
                    Integer.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_item_id",
                    Integer.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_descripcion",
                    String.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_cantidad",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_valor_unitario",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_precio_unitario",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_descuento",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_subtotal",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_tipo_igv_id",
                    Integer.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_igv",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_impuestos_bolsas",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.registerStoredProcedureParameter(
                    "p_total",
                    java.math.BigDecimal.class,
                    ParameterMode.IN
            );

            spDetalle.setParameter("p_comprobante_id", idComprobante);
            spDetalle.setParameter("p_tipo_unidad_medida_id", detalle.getTipoUnidadMedidaId());
            spDetalle.setParameter("p_item_id", detalle.getItemId());
            spDetalle.setParameter("p_descripcion", detalle.getDescripcion());
            spDetalle.setParameter("p_cantidad", detalle.getCantidad());
            spDetalle.setParameter("p_valor_unitario", detalle.getValorUnitario());
            spDetalle.setParameter("p_precio_unitario", detalle.getPrecioUnitario());
            spDetalle.setParameter("p_descuento", detalle.getDescuento());
            spDetalle.setParameter("p_subtotal", detalle.getSubtotal());
            spDetalle.setParameter("p_tipo_igv_id", detalle.getTipoIgvId());
            spDetalle.setParameter("p_igv", detalle.getIgv());
            spDetalle.setParameter("p_impuestos_bolsas", detalle.getImpuestosBolsas());
            spDetalle.setParameter("p_total", detalle.getTotal());

            spDetalle.execute();
        }

        // OBTENER COMPROBANTE

        /*ComprobanteEntity comprobante = comprobanteRepository
                .findById(idComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

        return ComprobanteMapper.toResponseDTO(comprobante);*/
        
        return Map.of("mensaje", "Comprobante guardado exitosamente");
        
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ComprobanteResponseDTO> listarComprobantes() {

        return comprobanteRepository.findAll()
                .stream()
                .map(ComprobanteMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComprobanteResponseDTO obtenerComprobante(Long id) {

        ComprobanteEntity comprobante = comprobanteRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

        return ComprobanteMapper.toResponseDTO(comprobante);
    }
}
