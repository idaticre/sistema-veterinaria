package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorRequestDTO {

    // solo para update
    private Long id;

    // Datos de entidad general
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 128)
    private String nombre;

    @Pattern(regexp = "^[MF]$", message = "El sexo debe ser M o F")
    private String sexo;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20)
    private String documento;

    @NotNull(message = "Tipo de persona jurídica es obligatorio")
    private Integer idTipoPersonaJuridica;

    @NotNull(message = "Tipo de documento es obligatorio")
    private Integer idTipoDocumento;

    @Email(message = "Correo inválido")
    private String correo;

    @Size(max = 15, message = "El teléfono no debe superar 15 caracteres")
    private String telefono;

    private String direccion;
    private String ciudad;
    private String distrito;

    // Datos específicos de colaborador
    @NotNull(message = "Usuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "Estado activo es obligatorio")
    private Boolean activo;

    private LocalDate fechaIngreso;

    @Size(max = 128)
    private String foto;
}
