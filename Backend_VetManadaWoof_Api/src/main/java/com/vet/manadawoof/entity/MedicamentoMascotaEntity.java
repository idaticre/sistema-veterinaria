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
@Table(name = "medicamentos_mascota")
public class MedicamentoMascotaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // ID interno autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Código único generado automáticamente
    @Column(nullable = false, unique = true, length = 16)
    private String codigo;
    
    // Dosis administrada
    @Column(length = 32)
    private String dosis;
    
    // Fecha de aplicación del medicamento
    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;
    
    // Fecha de última modificación
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    // Observaciones del registro
    @Column(length = 64)
    private String observaciones;
    
    // Fecha de creación del registro
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    // Estado lógico: 1 = activo, 0 = eliminado
    @Column(name = "activo", columnDefinition = "TINYINT(1)")
    private Boolean activo;
    
    // Relaciones
    
    // Mascota a la que se aplica el medicamento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaEntity mascota;
    
    // Medicamento administrado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medicamento", nullable = false)
    private MedicamentoEntity medicamento;
    
    // Vía de aplicación del medicamento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_via", nullable = false)
    private AplicacionViaEntity via;
    
    // Colaborador que aplicó el medicamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    private ColaboradorEntity colaborador;
    
    // Veterinario responsable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    private VeterinarioEntity veterinario;
}
