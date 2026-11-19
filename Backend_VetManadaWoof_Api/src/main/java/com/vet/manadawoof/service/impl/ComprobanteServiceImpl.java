package com.vet.manadawoof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vet.manadawoof.dtos.request.ComprobanteDetalleRequestDTO;
import com.vet.manadawoof.dtos.request.ComprobanteRequestDTO;
import com.vet.manadawoof.dtos.request.NotaCreditoRequestDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.dtos.response.ComprobanteSunatDTO;
import com.vet.manadawoof.entity.AgendaEntity;
import com.vet.manadawoof.entity.ComprobanteDetalleEntity;
import com.vet.manadawoof.entity.ComprobanteEntity;
import java.math.BigDecimal;
import com.vet.manadawoof.mapper.ComprobanteMapper;
import com.vet.manadawoof.mapper.ComprobanteSunatMapper;
import com.vet.manadawoof.repository.AgendaRepository;
import com.vet.manadawoof.repository.ComprobanteRepository;
import com.vet.manadawoof.service.ComprobanteService;
import com.vet.manadawoof.service.CajaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobanteServiceImpl implements ComprobanteService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AgendaRepository agendaRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final CajaService cajaService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sunat.api.url}")
    private String sunatApiUrl;

    @Value("${sunat.api.token}")
    private String sunatApiToken;
    
    @Override
    @Transactional
    public Map<String, Object> generarComprobante(ComprobanteRequestDTO request) {

        // VALIDAR AGENDA
        AgendaEntity agenda = agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada"));

        // DEFINIR SERIE
        String serie = request.getTipoComprobanteId() == 1 ? "FFF1" : "BBB1";

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
        sp.registerStoredProcedureParameter("p_total_gratuita", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_otros_cargos", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo_percepcion_id", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_percepcion_base_imponible", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_percepcion", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_incluido_percepcion", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo_unico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_condiciones_pago", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nubecont_tipo_venta_codigo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cliente_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_anticipio", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_medio_pago_id", Integer.class, ParameterMode.IN);

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
        sp.setParameter("p_total_gratuita", request.getTotalGratuita());
        sp.setParameter("p_total_otros_cargos", request.getTotalOtrosCargos());
        sp.setParameter("p_total", request.getTotal());
        sp.setParameter("p_tipo_percepcion_id", request.getTipoPercepcionId());
        sp.setParameter("p_percepcion_base_imponible", request.getPercepcionBaseImponible());
        sp.setParameter("p_total_percepcion", request.getTotalPercepcion());
        sp.setParameter("p_total_incluido_percepcion", request.getTotalIncluidoPercepcion());
        sp.setParameter("p_observaciones", request.getObservaciones());
        sp.setParameter("p_codigo_unico", request.getCodigoUnico());
        sp.setParameter("p_condiciones_pago", request.getCondicionesPago());
        sp.setParameter("p_nubecont_tipo_venta_codigo", request.getNubecontTipoVentaCodigo());
        sp.setParameter("p_cliente_id", agenda.getCliente().getId());
        sp.setParameter("p_total_anticipio", request.getTotalAnticipio());
        sp.setParameter("p_medio_pago_id", request.getMedioPagoId());

        // EJECUTAR

        sp.execute();

        Long idComprobante = ((Number)
                sp.getOutputParameterValue("p_id_comprobante"))
                .longValue();

        // INSERTAR DETALLES

        for (ComprobanteDetalleRequestDTO detalle : request.getDetalles()) {
            insertarDetalleComprobante(idComprobante, detalle);
        }

        if (Boolean.TRUE.equals(request.getPagoRecibido())) {
            if (request.getMedioPagoId() == null || request.getMontoPagado() == null
                    || request.getMontoPagado().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Para registrar el pago, indique un monto y un medio de pago.");
            }
            java.math.BigDecimal maximoPendiente = request.getTotal()
                    .subtract(request.getTotalAnticipio() == null
                            ? java.math.BigDecimal.ZERO
                            : request.getTotalAnticipio());
            if (request.getMontoPagado().compareTo(maximoPendiente) > 0) {
                throw new IllegalArgumentException("El pago ingresado supera el saldo pendiente del comprobante.");
            }
            cajaService.registrarIngresoDesdeComprobante(
                    idComprobante,
                    request.getMontoPagado(),
                    request.getMedioPagoId(),
                    agenda.getCliente().getId(),
                    agenda.getId(),
                    "Pago de comprobante #" + idComprobante
            );
        }

        // OBTENER ENTIDAD Y ENVIAR A SUNAT
        ComprobanteEntity comprobante = comprobanteRepository
                .findById(idComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

        Map<String, Object> respuestaSunat = new java.util.HashMap<>(enviarASunat(comprobante));
        // Si NUBEFACT rechazo el comprobante, se revierte (borra fila y
        // devuelve el correlativo) para no descuadrar la numeracion
        revertirSiRechazado(respuestaSunat, comprobante);
        respuestaSunat.put("pago_registrado_en_caja", Boolean.TRUE.equals(request.getPagoRecibido()));
        respuestaSunat.put("id_comprobante", idComprobante);
        return respuestaSunat;
    }

    // INSERTA UN DETALLE de comprobante via sp_insertar_comprobante_detalle.
    // Lo usan tanto el flujo de boleta/factura como las notas de credito.
    private void insertarDetalleComprobante(Long idComprobante, ComprobanteDetalleRequestDTO detalle) {

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

        spDetalle.registerStoredProcedureParameter(
                "p_anticipio_regularizacion",
                Boolean.class,
                ParameterMode.IN
        );

        spDetalle.registerStoredProcedureParameter(
                "p_anticipio_documento_serie",
                String.class,
                ParameterMode.IN
        );

        spDetalle.registerStoredProcedureParameter(
                "p_anticipio_documento_numero",
                Integer.class,
                ParameterMode.IN
        );

        spDetalle.registerStoredProcedureParameter(
                "p_codigo_producto_sunat",
                String.class,
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
        spDetalle.setParameter("p_anticipio_regularizacion", detalle.getAnticipoRegularizacion());
        spDetalle.setParameter("p_anticipio_documento_serie", detalle.getAnticipoDocumentoSerie());
        spDetalle.setParameter("p_anticipio_documento_numero", detalle.getAnticipoDocumentoNumero());
        spDetalle.setParameter("p_codigo_producto_sunat", detalle.getCodigoProductoSunat());

        spDetalle.execute();
    }

    /*@Override
    @Transactional(readOnly = true)
    public Map<String, Object> enviarComprobanteSunat(Long id) {
        ComprobanteEntity comprobante = comprobanteRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
        return enviarASunat(comprobante);
    }*/

    @Override
    @Transactional
    public Map<String, Object> emitirNotaCredito(Long idComprobanteOrigen, NotaCreditoRequestDTO request) {

        // COMPROBANTE ORIGEN (debe ser factura o boleta ya emitida)
        ComprobanteEntity origen = comprobanteRepository
                .findById(idComprobanteOrigen)
                .orElseThrow(() -> new RuntimeException("Comprobante origen no encontrado"));

        Integer tipoOrigen = origen.getTipoComprobante() != null
                ? origen.getTipoComprobante().getId()
                : null;

        if (tipoOrigen == null || (tipoOrigen != 1 && tipoOrigen != 2)) {
            throw new RuntimeException(
                    "Solo se pueden emitir notas de credito sobre facturas o boletas");
        }

        // VALIDACIONES DEL REQUEST
        if (request.getNcTipo() == null || request.getNcTipo() < 1 || request.getNcTipo() > 13) {
            throw new RuntimeException("Tipo de nota de credito invalido (1-13)");
        }

        if (request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new RuntimeException("El motivo o sustento de la nota de credito es obligatorio");
        }

        // MONTOS: por defecto los del comprobante origen.
        // Para descuentos (tipo 4 = global, 5 = por item) se usa el monto
        // indicado (con IGV): base = monto / 1.18, igv = monto - base
        BigDecimal gravada = origen.getTotalGravada();
        BigDecimal inafecta = origen.getTotalInafecta();
        BigDecimal exonerada = origen.getTotalExonerada();
        BigDecimal igv = origen.getTotalIGV();
        BigDecimal total = origen.getTotal();

        boolean esDescuento = request.getNcTipo() == 4 || request.getNcTipo() == 5;

        if (esDescuento) {
            if (request.getMontoDescuento() == null
                    || request.getMontoDescuento().compareTo(BigDecimal.ZERO) <= 0
                    || request.getMontoDescuento().compareTo(origen.getTotal()) > 0) {
                throw new RuntimeException(
                        "El monto de descuento debe ser mayor a 0 y menor o igual al total del comprobante");
            }

            BigDecimal monto = request.getMontoDescuento();
            BigDecimal base = monto.divide(
                    new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);

            gravada = base;
            igv = monto.subtract(base);
            inafecta = BigDecimal.ZERO;
            exonerada = BigDecimal.ZERO;
            total = monto;
        }

        // SERIE: en NUBEFACT las notas de credito comparten la serie del
        // documento modificado (NC de factura = FFF1, NC de boleta = BBB1);
        // la numeracion es independiente por tipo de documento
        String serieNC = tipoOrigen == 1 ? "FFF1" : "BBB1";

        // PROCEDURE CABECERA
        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("sp_insertar_nota_credito");

        sp.registerStoredProcedureParameter("p_agenda_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_comprobante_origen_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_doc_modifica_tipo", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_doc_modifica_serie", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_doc_modifica_numero", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nc_tipo", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nc_motivo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_serie", String.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_fecha_emision", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_fecha_vencimiento", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo_moneda_id", Integer.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_total_gravada", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_inafecta", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_exonerada", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_igv", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_gratuita", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_otros_cargos", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total", java.math.BigDecimal.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo_unico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_condiciones_pago", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nubecont_tipo_venta_codigo", String.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_cliente_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_total_anticipio", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_medio_pago_id", Integer.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_id_comprobante", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_numero", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        // SET PARAMS (la NC hereda agenda, moneda y cliente del origen)
        java.time.LocalDate hoy = java.time.LocalDate.now();

        sp.setParameter("p_agenda_id", origen.getAgenda().getId());
        sp.setParameter("p_comprobante_origen_id", origen.getId());
        sp.setParameter("p_doc_modifica_tipo", tipoOrigen);
        sp.setParameter("p_doc_modifica_serie", origen.getSerie());
        sp.setParameter("p_doc_modifica_numero", origen.getNumero());
        sp.setParameter("p_nc_tipo", request.getNcTipo());
        sp.setParameter("p_nc_motivo", request.getMotivo());
        sp.setParameter("p_serie", serieNC);

        sp.setParameter("p_fecha_emision", java.sql.Date.valueOf(hoy));
        sp.setParameter("p_fecha_vencimiento", java.sql.Date.valueOf(hoy));
        sp.setParameter("p_tipo_moneda_id", origen.getTipoMoneda().getId());

        sp.setParameter("p_total_gravada", gravada);
        sp.setParameter("p_total_inafecta", inafecta);
        sp.setParameter("p_total_exonerada", exonerada);
        sp.setParameter("p_total_igv", igv);
        sp.setParameter("p_total_gratuita", BigDecimal.ZERO);
        sp.setParameter("p_total_otros_cargos", BigDecimal.ZERO);
        sp.setParameter("p_total", total);

        sp.setParameter("p_observaciones", request.getMotivo());
        sp.setParameter("p_codigo_unico", origen.getCodigoUnico());
        sp.setParameter("p_condiciones_pago", origen.getCondicionesPago());
        sp.setParameter("p_nubecont_tipo_venta_codigo", origen.getNubecontTipoVentaCodigo());

        sp.setParameter("p_cliente_id", origen.getCliente().getId());
        sp.setParameter("p_total_anticipio", BigDecimal.ZERO);
        sp.setParameter("p_medio_pago_id", origen.getMedioPago() != null
                ? origen.getMedioPago().getId() : null);

        // EJECUTAR
        sp.execute();

        Long idNotaCredito = ((Number)
                sp.getOutputParameterValue("p_id_comprobante"))
                .longValue();

        Integer numeroNC = ((Number)
                sp.getOutputParameterValue("p_numero"))
                .intValue();

        // DETALLES: se copian los del comprobante origen; para los descuentos,
        // un solo item con el monto descontado
        List<ComprobanteDetalleRequestDTO> detallesNC = new java.util.ArrayList<>();

        if (esDescuento) {
            detallesNC.add(ComprobanteDetalleRequestDTO.builder()
                    .tipoUnidadMedidaId(1) // NIU (unica unidad en el catalogo)
                    .itemId(1)
                    .descripcion("Descuento global: " + request.getMotivo())
                    .cantidad(BigDecimal.ONE)
                    .valorUnitario(gravada)
                    .precioUnitario(total)
                    .descuento(BigDecimal.ZERO)
                    .subtotal(gravada)
                    .tipoIgvId(1) // GRAVADO
                    .igv(igv)
                    .impuestosBolsas(BigDecimal.ZERO)
                    .total(total)
                    .anticipoRegularizacion(false)
                    .build());
        } else {
            for (ComprobanteDetalleEntity d : origen.getDetalles()) {
                detallesNC.add(ComprobanteDetalleRequestDTO.builder()
                        .tipoUnidadMedidaId(d.getTipoUnidadMedida() != null
                                ? d.getTipoUnidadMedida().getId() : null)
                        .itemId(d.getItemId())
                        .descripcion(d.getDescripcion())
                        .cantidad(d.getCantidad())
                        .valorUnitario(d.getValorUnitario())
                        .precioUnitario(d.getPrecioUnitario())
                        .descuento(d.getDescuento())
                        .subtotal(d.getSubtotal())
                        .tipoIgvId(d.getTipoIGV() != null ? d.getTipoIGV().getId() : null)
                        .igv(d.getIgv())
                        .impuestosBolsas(d.getImpuestosBolsas())
                        .total(d.getTotal())
                        .anticipoRegularizacion(false)
                        .anticipoDocumentoSerie(d.getAnticipoDocSerie())
                        .anticipoDocumentoNumero(d.getAnticipoDocNumero())
                        .codigoProductoSunat(d.getCodigoProductoSunat())
                        .build());
            }
        }

        for (ComprobanteDetalleRequestDTO detalle : detallesNC) {
            insertarDetalleComprobante(idNotaCredito, detalle);
        }

        // OBTENER LA NOTA Y ENVIAR A NUBEFACT (misma clasificacion de estados)
        ComprobanteEntity notaCredito = comprobanteRepository
                .findById(idNotaCredito)
                .orElseThrow(() -> new RuntimeException("Nota de credito no encontrada"));

        Map<String, Object> respuesta = new java.util.HashMap<>(enviarASunat(notaCredito));
        // Si NUBEFACT rechazo la nota de credito, se revierte igual que en
        // el flujo de boleta/factura
        revertirSiRechazado(respuesta, notaCredito);
        respuesta.put("id_comprobante", idNotaCredito);
        respuesta.put("serie", serieNC);
        respuesta.put("numero", numeroNC);
        return respuesta;
    }

    // Si NUBEFACT rechazo el comprobante (estado RECHAZADA, o error de la API),
    // se revierte la insercion: se borra la fila y se devuelve el correlativo,
    // para no dejar numeros huecos en la serie. Los estados ACEPTADA y
    // PENDIENTE se quedan guardados.
    private void revertirSiRechazado(Map<String, Object> respuesta, ComprobanteEntity comprobante) {

        boolean rechazado =
                "RECHAZADA".equals(respuesta.get("estado"))
                || (!respuesta.containsKey("estado") && respuesta.containsKey("error"));

        if (!rechazado) return;

        try {
            StoredProcedureQuery sp = entityManager
                    .createStoredProcedureQuery("sp_revertir_comprobante");
            sp.registerStoredProcedureParameter("p_id_comprobante", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            sp.setParameter("p_id_comprobante", comprobante.getId());
            sp.execute();

            log.info("Comprobante {} revertido por rechazo de NUBEFACT: {}",
                    comprobante.getSerie() + "-" + comprobante.getNumero(),
                    sp.getOutputParameterValue("p_mensaje"));

            respuesta.put("guardado_en_bd", false);
        } catch (Exception ex) {
            // Si la reversion falla, el comprobante queda en BD (sin PDF);
            // quedara visible en el listado para reintentar
            log.error("No se pudo revertir el comprobante {}: {}",
                    comprobante.getId(), ex.getMessage(), ex);
            respuesta.put("guardado_en_bd", true);
        }
    }

    private Map<String, Object> enviarASunat(ComprobanteEntity comprobante) {
        ComprobanteSunatDTO sunatDTO = ComprobanteSunatMapper.toSunatDTO(comprobante);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(sunatApiToken);

        HttpEntity<ComprobanteSunatDTO> entity = new HttpEntity<>(sunatDTO, headers);

        try {
            Map respuesta = restTemplate.postForObject(sunatApiUrl, entity, Map.class);
            log.info("Respuesta NUBEFACT: {}", respuesta);
            if (respuesta == null) return Map.of("error", "Sin respuesta de la API");

            // NUBEFACT responde un JSON PLANO.
            // Formato de error: {"error": true, "mensaje": "..."}
            if (isTrue(respuesta.get("error"))) {
                return Map.of(
                    "error", "Error al generar el comprobante",
                    "mensaje", String.valueOf(respuesta.getOrDefault("mensaje", ""))
                );
            }
          

            // Formato de exito: {"aceptada_por_sunat": true/false, "sunat_description": "...",
            // "sunat_responsecode": "...", "enlace_del_pdf": "...", ...}
            boolean aceptadaPorSunat = isTrue(respuesta.get("aceptada_por_sunat"));

            String enlacePdf = String.valueOf(respuesta.getOrDefault("enlace_del_pdf", ""));

            // Deteccion de estado:
            //  - aceptada_por_sunat = true                  → ACEPTADA
            //  - aceptada = false y sunat_description null  → PENDIENTE (emitida y firmada,
            //    esperando acuse de SUNAT; el PDF ya existe y sera valido al aceptarse)
            //  - aceptada = false y sunat_description texto → RECHAZADA (con codigo y motivo)
            String sunatDescripcion = respuesta.get("sunat_description") == null
                ? null : String.valueOf(respuesta.get("sunat_description"));

            if (aceptadaPorSunat) {
                comprobante.setPdfurlNubefact(enlacePdf);
                comprobanteRepository.save(comprobante);

                return Map.of(
                    "estado", "ACEPTADA",
                    "mensaje", "Comprobante aceptado por SUNAT",
                    "enlace_del_pdf", enlacePdf
                );
            }

            if (sunatDescripcion == null) {
                // PENDIENTE: el documento existe en NUBEFACT (hash, XML y PDF generados)
                comprobante.setPdfurlNubefact(enlacePdf);
                comprobanteRepository.save(comprobante);

                return Map.of(
                    "estado", "PENDIENTE",
                    "mensaje", "Comprobante emitido y enviado a SUNAT. Pendiente de acuse de SUNAT.",
                    "enlace_del_pdf", enlacePdf
                );
            }

            // RECHAZADA: no se guarda el enlace (no hay documento valido)
            return Map.of(
                "estado", "RECHAZADA",
                "error", "Comprobante rechazado por SUNAT",
                "sunat_responsecode", String.valueOf(respuesta.getOrDefault("sunat_responsecode", "")),
                "sunat_description", sunatDescripcion,
                "sunat_note", String.valueOf(respuesta.getOrDefault("sunat_note", "")),
                "sunat_soap_error", String.valueOf(respuesta.getOrDefault("sunat_soap_error", ""))
            );
        } catch (HttpClientErrorException e) {
            // NUBEFACT rechaza peticiones invalidas con HTTP 4xx y un cuerpo {"errors": "...", "codigo": N}
            String cuerpo = e.getResponseBodyAsString();
            log.error("NUBEFACT rechazo la peticion ({}): {}", e.getStatusCode(), cuerpo);

            String mensaje = cuerpo;
            String codigo = String.valueOf(e.getStatusCode().value());
            try {
                Map errorBody = objectMapper.readValue(cuerpo, Map.class);
                if (errorBody.get("errors") != null) mensaje = String.valueOf(errorBody.get("errors"));
                else if (errorBody.get("mensaje") != null) mensaje = String.valueOf(errorBody.get("mensaje"));
                if (errorBody.get("codigo") != null) codigo = String.valueOf(errorBody.get("codigo"));
            } catch (Exception ignored) {
                // cuerpo no era JSON valido; se devuelve el texto crudo
            }

            return Map.of(
                "error", "NUBEFACT rechazo el comprobante",
                "codigo", codigo,
                "mensaje", mensaje
            );
        } catch (Exception e) {
            log.error("Error al llamar NUBEFACT ({}): {}", sunatApiUrl, e.getMessage(), e);
            return Map.of(
                "error", "Error al conectar con la API de SUNAT",
                "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
            );
        }
    }
    private static boolean isTrue(Object valor) {
        if (valor instanceof Boolean b) return b;
        return "true".equalsIgnoreCase(String.valueOf(valor));
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
    
    @Override
    @Transactional(readOnly = true)
    public List<ComprobanteResponseDTO> obtenerPorTipo(Integer tipoComprobanteId) {
        return comprobanteRepository.findByTipoComprobante(tipoComprobanteId)
                .stream()
                .map(ComprobanteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComprobanteResponseDTO> obtenerPorCliente(Long clienteId) {
        return comprobanteRepository.findByCliente(clienteId)
                .stream()
                .map(ComprobanteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
