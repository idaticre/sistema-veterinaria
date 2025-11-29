package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MascotaRequestDTO {
    
    // Solo para UPDATE
    private Long id;
    
    // Nombre de la mascota, obligatorio
    @NotBlank
    @Size(max = 64)
    private String nombre;
    
    // Sexo: M/H/O
    @Pattern(regexp = "^(M|H|O)?$")
    private String sexo;
    
    // ID del cliente propietario, obligatorio
    @NotNull
    private Long idCliente;
    
    // ID de la raza, opcional
    private Integer idRaza;
    
    //ID de la especie, obligatorio
    @NotNull
    private Integer idEspecie;
    
    // Estado de mascota, solo para UPDATE
    private Integer idEstado;
    
    //  Fecha de nacimiento, obligatorio
    @NotNull
    private LocalDate fechaNacimiento;
    
    // Pelaje, opcional
    @Size(max = 16)
    private String pelaje;
    
    // ID de tamaño, obligatorio
    @NotNull
    private Integer idTamano;
    
    // ID de etapa de vida, obligatorio
    @NotNull
    private Integer idEtapa;
    
    // Esterilizado, default false
    private Boolean esterilizado = false;
    
    // Alergias, opcional
    @Size(max = 128)
    private String alergias;
    
    //  Peso, mínimo 0.0
    @DecimalMin("0.0")
    private BigDecimal peso = BigDecimal.ZERO;
    
    // Tiene chip, default false
    private Boolean chip = false;
    
    //  Tiene pedigree, default false
    private Boolean pedigree = false;
    
    // Factor DEA, default false
    private Boolean factorDea = false;
    
    // Es agresiva, default false
    private Boolean agresividad = false;
    
    // Foto, opcional
    @Size(max = 255)
    private String foto;
    
    // Relaciones adicionales (necesarias para el mapper y el servicio)
    private Long idColaborador;
    private Long idVeterinario;
    
}
