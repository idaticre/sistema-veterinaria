package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    // Para update, no se expone al frontend
    private Long idEntidad;

    // --- Datos de clasificación ---
    @NotNull(message = "El tipo de persona jurídica es obligatorio")
    private Integer idTipoPersonaJuridica;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer idTipoDocumento;

    // --- Identificación personal ---
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 128, message = "El nombre no debe superar 128 caracteres")
    private String nombre;

    @Pattern(regexp = "^[MF]$", message = "El sexo debe ser M o F")
    private String sexo;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no debe superar 20 caracteres")
    private String documento;

    // --- Contacto ---
    @Email(message = "Correo inválido")
    private String correo;

    @Size(max = 15, message = "El teléfono no debe superar 15 caracteres")
    private String telefono;

    // --- Ubicación ---
    private String direccion;
    private String ciudad;
    private String distrito;

    // --- Estado ---
    private Boolean activo;
}
