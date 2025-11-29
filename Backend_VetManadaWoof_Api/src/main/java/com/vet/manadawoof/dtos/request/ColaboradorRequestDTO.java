package com.vet.manadawoof.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorRequestDTO {
    // --- Identificador del colaborador (solo para actualización) ---
    private Long id;
    
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
    @NotNull(message = "UsuarioEntity es obligatorio")
    private Integer idUsuario;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;
    
    
    @Size(max = 128)
    private String foto;
    
    // --- Estado ---
    private Boolean activo;
    
}
