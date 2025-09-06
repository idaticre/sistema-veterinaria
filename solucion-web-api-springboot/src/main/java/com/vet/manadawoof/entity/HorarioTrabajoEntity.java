package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "HorarioTrabajoEntity")
@Table(name = "horarios_trabajo")
public class  HorarioTrabajoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private ColaboradorEntity colaborador;

    @ManyToOne
    @JoinColumn(name = "id_dia_semana")
    private DiaEntity dia;

    @ManyToOne
    @JoinColumn(name = "id_tipo_dia")
    private TipoDiaEntity tipoDia;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;


    @Column(name = "hora_fin", columnDefinition = "VARCHAR(255)")
    private LocalTime horaFin;
}
