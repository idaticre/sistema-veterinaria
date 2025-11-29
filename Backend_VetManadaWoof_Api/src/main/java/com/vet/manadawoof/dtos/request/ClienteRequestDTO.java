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
public class ClienteRequestDTO {
    
    // --- IDs para operaciones específicas ---
    private Long id;           // Solo para actualizar
    private Long idEntidad;    // Solo para actualizar
    
    // --- Clasificación ---
    @NotNull(message = "El tipo de persona jurídica es obligatorio")
    private Integer idTipoPersonaJuridica;
    
    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer idTipoDocumento;
    
    // --- Identificación ---
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 128, message = "El nombre no debe superar los 128 caracteres")
    private String nombre;
    
    /**
     * Sexo del cliente (M o F).
     * Puede venir en minúscula, el sistema tomará el primer carácter.
     */
    @Pattern(regexp = "^[MFmf]$", message = "El sexo debe ser M o F")
    private String sexo;
    
    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no debe superar los 20 caracteres")
    private String documento;
    
    // --- Contacto ---
    @Email(message = "El correo no es válido")
    @Size(max = 128, message = "El correo no debe superar los 128 caracteres")
    private String correo;
    
    @Size(max = 15, message = "El teléfono no debe superar los 15 caracteres")
    private String telefono;
    
    // --- Ubicación ---
    @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "La ciudad no debe superar los 100 caracteres")
    private String ciudad;
    
    @Size(max = 100, message = "El distrito no debe superar los 100 caracteres")
    private String distrito;
    
    // Representante Campo opcional
    // Se usará solo si el tipo de persona jurídica lo requiere.
    
    @Size(max = 128, message = "El representante no debe superar los 128 caracteres")
    private String representante;
    
    // --- Estado Por defecto activo al crear. Puede usarse en actualización.
    
    private Boolean activo = true;
}
