package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca;
    private BigDecimal precio;
    private Integer stock;
    private Long proveedorId;
    private String nombreProveedor;
    private String foto;
    private Boolean activo;
    private String mensaje;
}
