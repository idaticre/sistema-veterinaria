package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CajaMovimientoRequestDTO {

    @NotBlank(message = "Seleccione el tipo de movimiento")
    private String tipo;

    @NotBlank(message = "Ingrese el concepto")
    @Size(max = 180, message = "El concepto admite hasta 180 caracteres")
    private String concepto;

    @NotNull(message = "Ingrese el monto")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El monto admite hasta dos decimales")
    private BigDecimal monto;

    @NotNull(message = "Seleccione el medio de pago")
    private Integer medioPagoId;

    private Long clienteId;
    @Size(max = 160)
    private String clienteReferencia;
    private Long productoId;
    @Positive(message = "La cantidad comprada debe ser mayor que cero")
    private Integer cantidadProducto;
    private Integer servicioId;
    @Size(max = 160)
    private String servicioReferencia;
    private Long agendaId;
}
