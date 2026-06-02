package com.vet.manadawoof.mapper;


import com.vet.manadawoof.dtos.response.ComprobanteDetalleResponseDTO;
import com.vet.manadawoof.dtos.response.ComprobanteResponseDTO;
import com.vet.manadawoof.dtos.response.TipoIGVResponseDTO;
import com.vet.manadawoof.dtos.response.TipoMonedaResponseDTO;
import com.vet.manadawoof.dtos.response.TiposComprobantesResponseDTO;
import com.vet.manadawoof.dtos.response.TiposPercepcionesResponseDTO;
import com.vet.manadawoof.entity.ComprobanteDetalleEntity;
import com.vet.manadawoof.entity.ComprobanteEntity;
import com.vet.manadawoof.entity.TipoIGVEntity;
import com.vet.manadawoof.entity.MonedasEntity;
import com.vet.manadawoof.entity.TiposComprobantesEntity;
import com.vet.manadawoof.entity.TiposPercepcionesEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ComprobanteMapper {
    
    public static ComprobanteResponseDTO
    toResponseDTO(
            ComprobanteEntity comprobante
    ) {
        List<ComprobanteDetalleResponseDTO> detalles =
        comprobante.getDetalles()!= null
                ? comprobante.getDetalles()
                    .stream()
                    .map(ComprobanteMapper::toDetalleResponseDTO)
                    .collect(Collectors.toList())
                : List.of();


        return ComprobanteResponseDTO
                .builder()

                .id(
                        comprobante.getId()
                )

                .serie(
                        comprobante.getSerie()
                )

                .numero(
                        comprobante.getNumero()
                )

                .fechaEmision(
                        comprobante.getFechaEmision()
                                != null
                                ? comprobante.getFechaEmision().toString()
                                : null
                )

                .fechaVencimiento(
                        comprobante.getFechaVencimiento()
                                != null
                                ? comprobante.getFechaVencimiento().toString()
                                : null
                )

                .clienteId(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getId()
                                : null
                )

                .nombreCliente(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad().getNombre()
                                : null
                )

                .clienteNumeroDoc(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente()
                                .getEntidad().getDocumento()
                                : null
                )

                .clienteTipoDoc(
                        comprobante.getCliente() != null
                                && comprobante.getCliente()
                                .getEntidad().getTipoDocumento() != null

                                ? comprobante.getCliente()
                                .getEntidad().getTipoDocumento()
                                .getDescripcion()

                                : null
                )

                .clienteDireccion(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad()
                                .getDireccion()
                                : null
                )

                .clienteCorreo(
                        comprobante.getCliente() != null
                                ? comprobante.getCliente().getEntidad()
                                .getCorreo()
                                : null
                )

                /*
                 * COMPROBANTE
                 */
                .tipoComprobante(
                        comprobante.getTipoComprobante() != null
                                ? comprobante.getTipoComprobante()
                                .getId()
                                : null
                )

                .moneda(
                        comprobante.getTipoMoneda()!= null
                                ? comprobante.getTipoMoneda()
                                .getId()
                                : null
                )

                .medioPago(
                        comprobante.getMedioPago()!= null
                                ? comprobante.getMedioPago()
                                .getId()
                                : null
                )

                .totalGravada(
                        comprobante.getTotalGravada()
                )

                .totalInafecta(
                        comprobante.getTotalInafecta()
                )

                .totalExonerada(
                        comprobante.getTotalExonerada()
                )

                .totalIGV(
                        comprobante.getTotalIGV()
                )

                .total(
                        comprobante.getTotal()
                )

                .observaciones(
                        comprobante.getObservaciones()
                )

                .detalles(
                        detalles
                )

                .build();
    }

    private static ComprobanteDetalleResponseDTO
    toDetalleResponseDTO(
            ComprobanteDetalleEntity detalle
    ) {

        return ComprobanteDetalleResponseDTO
                .builder()

                .Id(
                        detalle.getId()
                )

                .itemId(
                        detalle.getItemId()
                )

                .descripcion(
                        detalle.getDescripcion()
                )

                .cantidad(
                        detalle.getCantidad()
                )

                .valorUnitario(
                        detalle.getValorUnitario()
                )

                .precioUnitario(
                        detalle.getPrecioUnitario()
                )

                .descuento(
                        detalle.getDescuento()
                )

                .subtotal(
                        detalle.getSubtotal()
                )

                .igv(
                        detalle.getIgv()                )

                .total(
                        detalle.getTotal()
                )

                .unidadMedida(
                        detalle.getTipoUnidadMedida()!= null

                                ? detalle.getTipoUnidadMedida()
                                .getUnidad_medida()

                                : null
                )

                .tipoIGV(
                        detalle.getTipoIGV()!= null

                                ? detalle.getTipoIGV()
                                .getDescripcion()

                                : null
                )

                .build();
    }
    
    public static TipoMonedaResponseDTO
    toTipoMonedaResponseDTO(
            MonedasEntity moneda
    ) {
        if (moneda == null) return null;

        return TipoMonedaResponseDTO
                .builder()

                .id(
                        moneda.getId()
                )

                .moneda(
                        moneda.getMoneda()
                )

                .build();
    }

    public static TiposComprobantesResponseDTO
    toTiposComprobantesResponseDTO(
            TiposComprobantesEntity tipoComprobante
    ) {
        if (tipoComprobante == null) return null;

        return TiposComprobantesResponseDTO
                .builder()

                .id(
                        tipoComprobante.getId()
                )

                .comprobante(
                        tipoComprobante.getComprobante()
                )

                .build();
    }

    public static TiposPercepcionesResponseDTO
    toTiposPercepcionesResponseDTO(
            TiposPercepcionesEntity tipoPercepcion
    ) {
        if (tipoPercepcion == null) return null;

        return TiposPercepcionesResponseDTO
                .builder()

                .id(
                        tipoPercepcion.getId()
                )

                .percepcion(
                        tipoPercepcion.getPercepcion()
                )

                .tasaPorcentaje(
                        tipoPercepcion.getTasaPorcentaje()
                )

                .build();
    }

    public static TipoIGVResponseDTO
    toTipoIGVResponseDTO(
            TipoIGVEntity tipoIGV
    ) {
        if (tipoIGV == null) return null;

        return TipoIGVResponseDTO
                .builder()

                .id(
                        tipoIGV.getId()
                )

                .descripcion(
                        tipoIGV.getDescripcion()
                )

                .build();
    }

}
