package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "asignacion_horarios", uniqueConstraints = {
        @UniqueConstraint(name = "uq_colab_dia", columnNames = {"id_colaborador", "id_dia_semana", "fecha_inicio_vigencia", "activo"})})
public class AsignacionHorarioEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", nullable = false)
    @JsonIgnore
    private ColaboradorEntity colaborador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_base", nullable = false)
    @JsonIgnore
    private HorarioBaseEntity horarioBase;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_semana", nullable = false)
    @JsonIgnore
    private DiaEntity dia;
    
    @Column(name = "fecha_inicio_vigencia", nullable = false)
    private LocalDate fechaInicioVigencia;
    
    @Column(name = "fecha_fin_vigencia")
    private LocalDate fechaFinVigencia;
    
    @Column(name = "motivo_cambio", length = 255)
    private String motivoCambio;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion = LocalDateTime.now();
    
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;
    
    @OneToMany(mappedBy = "asignacion", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AsignacionHorarioDetalleEntity> detalles;
    
    @PreUpdate
    public void preUpdate() {
        // Validar fechas de vigencia
        if(fechaFinVigencia != null && fechaFinVigencia.isBefore(fechaInicioVigencia)) {
            throw new IllegalStateException("La fecha fin de vigencia no puede ser anterior a la fecha inicio");
        }
    }
}
