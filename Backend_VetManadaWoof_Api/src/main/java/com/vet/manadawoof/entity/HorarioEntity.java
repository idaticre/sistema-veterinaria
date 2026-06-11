package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "horarios")
public class HorarioEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    @JsonIgnore
    private ColaboradorEntity colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_id", nullable = false)
    @JsonIgnore
    private DiaEntity dia;

    @Column(name = "trabaja", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean trabaja = false;

    @Column(name = "hora_inicio", columnDefinition = "timestamp")
    private LocalTime horaInicio;

    @Column(name = "hora_fin", columnDefinition = "timestamp")
    private LocalTime horaFin;
}
