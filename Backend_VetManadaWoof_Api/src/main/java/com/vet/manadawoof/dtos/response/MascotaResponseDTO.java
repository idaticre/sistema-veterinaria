package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MascotaResponseDTO {

    // ID interno de la mascota
    private Long id;

    // Código único generado automáticamente por el sistema
    private String codigo;

    // Nombre de la mascota
    private String nombre;

    // Sexo de la mascota: M/H/O
    private String sexo;

    private MascotaRelacionResponseDTO cliente;
    private MascotaRelacionResponseDTO raza;
    private MascotaRelacionResponseDTO especie;
    private MascotaRelacionResponseDTO estado;
    private MascotaRelacionResponseDTO tamano;
    private MascotaRelacionResponseDTO etapa;

    // Fecha de nacimiento de la mascota
    private LocalDate fechaNacimiento;

    // Descripción del pelaje
    private String pelaje;

    // Indica si la mascota está esterilizada
    private Boolean esterilizado;

    // Registro de alergias
    private String alergias;

    // Peso actual de la mascota
    private BigDecimal peso;

    // Indica si la mascota tiene chip
    private Boolean chip;

    // Indica si la mascota tiene pedigree
    private Boolean pedigree;

    // Factor DEA (para transfusiones)
    private Boolean factorDea;

    // Indica si la mascota muestra agresividad
    private Boolean agresividad;

    // URL o path de la foto
    private String foto;

    // Fecha de creación del registro
    private LocalDateTime fechaRegistro;

    // Fecha de última modificación del registro
    private LocalDateTime fechaModificacion;

    // Relaciones adicionales (necesarias para el mapper y el servicio)
    private Long idColaborador;
    private Long idVeterinario;
}
