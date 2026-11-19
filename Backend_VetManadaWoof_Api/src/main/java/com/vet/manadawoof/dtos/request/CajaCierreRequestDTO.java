package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CajaCierreRequestDTO {

    @NotNull(message = "Ingrese el efectivo contado")
    @DecimalMin(value = "0.00", message = "El efectivo contado no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El efectivo contado admite hasta dos decimales")
    private BigDecimal saldoEfectivoContado;

    @Size(max = 2000, message = "La observación admite hasta 2000 caracteres")
    private String observaciones;
}
