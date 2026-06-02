package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.ComprobanteSunatDTO;
import com.vet.manadawoof.entity.ComprobanteDetalleEntity;
import com.vet.manadawoof.entity.ComprobanteEntity;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ComprobanteSunatMapper {

    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static ComprobanteSunatDTO toSunatDTO(ComprobanteEntity comprobante) {

        List<ComprobanteSunatDTO.ItemSunatDTO> items =
                comprobante.getDetalles() != null
                        ? comprobante.getDetalles()
                            .stream()
                            .map(ComprobanteSunatMapper::toItemSunatDTO)
                            .collect(Collectors.toList())
                        : List.of();

        return ComprobanteSunatDTO.builder()

                .operacion("generar_comprobante")
                .sunatTransaction(1)
                .porcentajeDeIgv(new BigDecimal("18.00"))
                .enviarAutomaticamenteALaSunat(true)
                .enviarAutomaticamenteAlCliente(false)
                .detraccion(false)

                .tipoDeComprobante(
                        comprobante.getTipoComprobante() != null
                                ? comprobante.getTipoComprobante().getId()
                                : null
                )
                .serie(comprobante.getSerie())
                .numero(comprobante.getNumero())
                .fechaDeEmision(
                        comprobante.getFechaEmision() != null
                                ? comprobante.getFechaEmision().format(FECHA_FORMATTER)
                                : ""
                )
                .fechaDeVencimiento(
                        comprobante.getFechaVencimiento() != null
                                ? comprobante.getFechaVencimiento().format(FECHA_FORMATTER)
                                : ""
                )
                .moneda(
                        comprobante.getTipoMoneda() != null
                                ? comprobante.getTipoMoneda().getId()
                                : null
                )

                .clienteTipoDeDocumento(
                        comprobante.getCliente() != null
                                && comprobante.getCliente().getEntidad().getTipoDocumento() != null
                                ? comprobante.getCliente().getEntidad().getTipoDocumento().getId()
                                : null
                )
                .clienteNumeroDeDocumento(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad().getDocumento()
                                : ""
                )
                .clienteDenominacion(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad().getNombre()
                                : ""
                )
                .clienteDireccion(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad().getDireccion()
                                : ""
                )
                .clienteEmail(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad().getCorreo()
                                : ""
                )

                .totalGravada(comprobante.getTotalGravada())
                .totalInafecta(comprobante.getTotalInafecta() != null && comprobante.getTotalInafecta().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalInafecta().toString() : "")
                .totalExonerada(comprobante.getTotalExonerada() != null && comprobante.getTotalExonerada().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalExonerada().toString() : "")
                .totalIgv(comprobante.getTotalIGV())
                .totalGratuita(comprobante.getTotalGratuita() != null && comprobante.getTotalGratuita().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalGratuita().toString() : "")
                .totalOtrosCargos(comprobante.getTotalOtrosCargos() != null && comprobante.getTotalOtrosCargos().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalOtrosCargos().toString() : "")
                .totalImpuestosBolsas("")
                .total(comprobante.getTotal())
                .totalAnticipo(comprobante.getTotalAnticipio() != null && comprobante.getTotalAnticipio().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalAnticipio().toString() : "")

                .percepcionTipo(comprobante.getTipoPercepcion() != null
                        ? String.valueOf(comprobante.getTipoPercepcion().getId()) : "")
                .percepcionBaseImponible(comprobante.getPercepcionBaseImponible() != null && comprobante.getPercepcionBaseImponible().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getPercepcionBaseImponible().toString() : "")
                .totalPercepcion(comprobante.getTotalPercepcion() != null && comprobante.getTotalPercepcion().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalPercepcion().toString() : "")
                .totalIncluidoPercepcion(comprobante.getTotalIncluidoPercepcion() != null && comprobante.getTotalIncluidoPercepcion().compareTo(BigDecimal.ZERO) > 0
                        ? comprobante.getTotalIncluidoPercepcion().toString() : "")

                .observaciones(comprobante.getObservaciones() != null ? comprobante.getObservaciones() : "")
                .codigoUnico(comprobante.getCodigoUnico() != null ? comprobante.getCodigoUnico() : "")
                .condicionesDePago(comprobante.getCondicionesPago() != null ? comprobante.getCondicionesPago() : "")
                .ordenCompraServicio(String.valueOf(comprobante.getId()))
                .nubecontTipoDeVentaCodigo(comprobante.getNubecontTipoVentaCodigo() != null ? comprobante.getNubecontTipoVentaCodigo() : "")
                .medioDePago(comprobante.getMedioPago() != null ? comprobante.getMedioPago().getNombre() : "")

                .items(items)
                .build();
    }

    private static ComprobanteSunatDTO.ItemSunatDTO toItemSunatDTO(ComprobanteDetalleEntity detalle) {

        return ComprobanteSunatDTO.ItemSunatDTO.builder()

                .unidadDeMedida(
                        detalle.getTipoUnidadMedida() != null
                                ? detalle.getTipoUnidadMedida().getUnidad_medida()
                                : ""
                )
                .codigo(detalle.getItemId() != null ? String.valueOf(detalle.getItemId()) : "")
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .valorUnitario(detalle.getValorUnitario())
                .precioUnitario(detalle.getPrecioUnitario())
                .descuento(detalle.getDescuento() != null && detalle.getDescuento().compareTo(BigDecimal.ZERO) > 0
                        ? detalle.getDescuento().toString() : "")
                .subtotal(detalle.getSubtotal())
                .tipoDeIgv(
                        detalle.getTipoIGV() != null
                                ? detalle.getTipoIGV().getId()
                                : null
                )
                .igv(detalle.getIgv())
                .total(detalle.getTotal())
                .anticipoRegularizacion(detalle.getAnticipoRegularizacion() != null ? detalle.getAnticipoRegularizacion() : false)
                .anticipoDocumentoSerie(detalle.getAnticipoDocSerie() != null ? detalle.getAnticipoDocSerie() : "")
                .anticipoDocumentoNumero(detalle.getAnticipoDocNumero() != null ? String.valueOf(detalle.getAnticipoDocNumero()) : "")
                .codigoProductoSunat(detalle.getCodigoProductoSunat() != null ? detalle.getCodigoProductoSunat() : "")
                .build();
    }
}
