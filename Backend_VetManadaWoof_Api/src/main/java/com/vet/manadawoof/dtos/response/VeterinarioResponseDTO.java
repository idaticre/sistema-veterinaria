package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VeterinarioResponseDTO {
    
    // Datos específicos de veterinario
    private Long id;
    private String codigo;
    private String cmp;
    private String especialidad;
    
    // Datos específicos de colaborador
    private String usuario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDate fechaIngreso;
    private String foto;
    
    // Datos de colaborador / entidad
    private Long idColaborador;
    private String codigoColaborador;
    private Long idEntidad;
    private String nombre;
    private String sexo;
    private String documento;
    private Integer idTipoPersonaJuridica;
    private Integer idTipoDocumento;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;
    
    // fallback de SP
    private String mensaje;
}
