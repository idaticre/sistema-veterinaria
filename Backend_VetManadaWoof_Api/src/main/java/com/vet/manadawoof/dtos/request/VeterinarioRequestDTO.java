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
public class VeterinarioRequestDTO {
    
    // Solo para update
    private Long id;
    
    // Datos de entidad general (extendido)
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
    @NotNull(message = "UsuarioEntity es obligatorio")
    private Integer idUsuario;
    
    @NotNull(message = "Estado activo es obligatorio")
    private Boolean activo;
    
    private String foto;
    
    // Datos específicos de veterinario
    @NotNull(message = "Especialidad es obligatoria")
    private Integer idEspecialidad;
    
    @NotBlank(message = "CMP es obligatorio")
    @Size(max = 32)
    private String cmp;
}
