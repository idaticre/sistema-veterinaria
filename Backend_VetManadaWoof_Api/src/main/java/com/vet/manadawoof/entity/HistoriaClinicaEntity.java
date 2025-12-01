package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "historia_clinica")
public class HistoriaClinicaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false, unique = true)
    private MascotaEntity mascota;
    
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDate fechaApertura = LocalDate.now();
    
    @Column(columnDefinition = "TEXT")
    private String observacionesGenerales;
    
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
