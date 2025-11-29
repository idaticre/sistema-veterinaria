package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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
@Table(name = "historia_clinica", indexes = {
        @Index(name = "idx_historia_mascota", columnList = "id_mascota", unique = true), @Index(name = "idx_historia_codigo", columnList = "codigo", unique = true)})
public class HistoriaClinicaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_historia_mascota"))
    private MascotaEntity mascota;
    
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDate fechaApertura = LocalDate.now();
    
    @Column(columnDefinition = "TEXT")
    private String observacionesGenerales;
    
    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Boolean activa = true;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
