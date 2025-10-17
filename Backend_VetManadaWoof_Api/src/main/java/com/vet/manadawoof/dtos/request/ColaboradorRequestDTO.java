package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorRequestDTO {
    
    // Para update, no se expone al frontend
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
    
    @Pattern(regexp = "^[MF]$", message = "El sexo debe ser M o F")
    private String sexo;
    
    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "Documento no debe superar 20 caracteres")
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
    
    // Datos específicos de colaborador
    @NotNull(message = "Usuario es obligatorio")
    private Integer idUsuario;
    
    @Size(max = 128)
    private String foto;
    
    // --- Estado ---
    private Boolean activo;
    
}
