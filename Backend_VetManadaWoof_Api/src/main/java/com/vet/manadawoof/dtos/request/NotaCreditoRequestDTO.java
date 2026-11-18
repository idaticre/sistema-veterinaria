package com.vet.manadawoof.dtos.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotaCreditoRequestDTO {

    // Tipo de nota de credito (1-13) segun catalogo NUBEFACT:
    // 1=Anulacion de la operacion, 2=Anulacion por error en RUC,
    // 3=Correccion por descripcion, 4=Descuento global,
    // 5=Descuento por item, 6=Devolucion total, 7=Devolucion por item,
    // 8=Bonificacion, 9=Disminucion en valor, 10=Otros conceptos,
    // 11-13=Ajustes
    private Integer ncTipo;

    // Motivo o sustento de la nota (obligatorio para SUNAT)
    private String motivo;

    // Monto a descontar (con IGV); solo se usa para tipos 4 y 5.
    // En los demas tipos la NC usa los montos del comprobante origen.
    private BigDecimal montoDescuento;

}
