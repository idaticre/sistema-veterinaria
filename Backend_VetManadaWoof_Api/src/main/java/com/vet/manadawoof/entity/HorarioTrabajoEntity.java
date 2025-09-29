package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "horarios_trabajo")
public class HorarioTrabajoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 16)
    private String codigo;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false)
    private ColaboradorEntity colaborador;

    @ManyToOne
    @JoinColumn(name = "id_dia_semana", nullable = false)
    private DiaEntity dia;

    @ManyToOne
    @JoinColumn(name = "id_tipo_dia")
    private TipoDiaEntity tipoDia;
}
