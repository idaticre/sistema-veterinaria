package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorRequestDTO {
    // Para actualizar
    private Long id;

    @NotNull(message = "Tipo de persona jurídica es obligatorio")
    private Integer idTipoPersonaJuridica;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 128, message = "Nombre no debe superar 128 caracteres")
    private String nombre;

    @Pattern(regexp = "M|F", message = "Sexo debe ser 'M' o 'F'")
    private String sexo;

    @NotBlank(message = "Documento es obligatorio")
    @Size(max = 20, message = "Documento no debe superar 20 caracteres")
    private String documento;

    @NotNull(message = "Tipo de documento es obligatorio")
    private Integer idTipoDocumento;

    @Email(message = "Correo debe ser válido")
    @Size(max = 64, message = "Correo no debe superar 64 caracteres")
    private String correo;

    @Size(max = 15, message = "Teléfono no debe superar 15 caracteres")
    private String telefono;

    @Size(max = 128, message = "Dirección no debe superar 128 caracteres")
    private String direccion;

    @Size(max = 64, message = "Ciudad no debe superar 64 caracteres")
    private String ciudad;

    @Size(max = 64, message = "Distrito no debe superar 64 caracteres")
    private String distrito;

    @Size(max = 64, message = "Representante no debe superar 64 caracteres")
    private String representante;

    @NotNull(message = "Activo es obligatorio")
    private Boolean activo;
}
