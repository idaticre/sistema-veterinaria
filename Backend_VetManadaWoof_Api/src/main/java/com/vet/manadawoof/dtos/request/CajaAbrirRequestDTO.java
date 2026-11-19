package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CajaAbrirRequestDTO {

    @NotNull(message = "Indique el saldo de apertura para iniciar la caja")
    @DecimalMin(value = "0.00", message = "El saldo de apertura no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El saldo admite hasta dos decimales")
    private BigDecimal saldoAperturaEfectivo;
}
