package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "mascotas")
public class MascotaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    // ID interno de la mascota
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Código único generado automáticamente
    @Column(nullable = false, unique = true, length = 16)
    private String codigo;
    
    // Nombre de la mascota
    @Column(nullable = false, length = 64)
    private String nombre;
    
    // Sexo de la mascota: 'M', 'F', 'O'
    @Column(length = 1)
    private String sexo;
    
    // Fecha de nacimiento
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    // Pelaje
    @Column(length = 16)
    private String pelaje;
    
    // Indica si la mascota está esterilizada
    @Column(name = "esterilizado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean esterilizado = false;
    
    // Registro de alergias
    @Column(length = 128)
    private String alergias;
    
    // Peso de la mascota
    @Column(precision = 6, scale = 2)
    private BigDecimal peso = BigDecimal.ZERO;
    
    // Indica si tiene chip
    @Column(name = "chip", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean chip = false;
    
    // Indica si tiene pedigree
    @Column(name = "pedigree", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean pedigree = false;
    
    // Factor DEA para transfusiones
    @Column(name = "factor_dea", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean factorDea = false;
    
    // Indica agresividad
    @Column(name = "agresividad", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean agresividad = false;
    
    // URL o path de la foto
    @Column(length = 255)
    private String foto;
    
    // Fecha de creación del registro
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    // Fecha de última modificación
    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;
    
    // Relaciones
    
    // Cliente propietario de la mascota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnore
    private ClienteEntity cliente;
    
    // Raza de la mascota
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raza")
    @JsonIgnore
    private RazaEntity raza;
    
    // Especie de la mascota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_especie", nullable = false)
    @JsonIgnore
    private EspecieEntity especie;
    
    // Estado actual de la mascota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    @JsonIgnore
    private EstadoMascotaEntity estado;
    
    // Tamaño de la mascota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tamano", nullable = false)
    @JsonIgnore
    private TamanoMascEntity tamano;
    
    // Etapa de vida de la mascota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_etapa", nullable = false)
    @JsonIgnore
    private EtapaVidaEntity etapa;
    
    // Colaborador asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    @JsonIgnore
    private ColaboradorEntity colaborador;
    
    // Veterinario asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    @JsonIgnore
    private VeterinarioEntity veterinario;
}
