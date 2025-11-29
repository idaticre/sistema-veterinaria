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
    
    // --- Identificador del proveedor (solo para update) --
    private Long id;
    // no se expone al frontend
    private Long idEntidad;
    
    // --- Datos de clasificación ---
    @NotNull(message = "Tipo de persona jurídica es obligatorio")
    private Integer idTipoPersonaJuridica;
    
    @NotNull(message = "Tipo de documento es obligatorio")
    private Integer idTipoDocumento;
    
    // --- Identificación personal ---
    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 128, message = "Nombre no debe superar 128 caracteres")
    private String nombre;
    
    @Pattern(regexp = "^[MF]$", message = "Sexo debe ser 'M' o 'F'")
    private String sexo;
    
    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "Documento no debe superar 20 caracteres")
    private String documento;
    
    // --- Contacto ---
    @Email(message = "Correo inválido")
    private String correo;
    private String representante;
    
    @Size(max = 15, message = "Teléfono no debe superar 15 caracteres")
    private String telefono;
    
    // --- Ubicación ---
    private String direccion;
    private String ciudad;
    private String distrito;
    
    // --- Estado ---
    private Boolean activo;
}
