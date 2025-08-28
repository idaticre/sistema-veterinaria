package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "HorarioTrabajoEntity")
@Table(name = "horarios_trabajo")
public class HorarioTrabajoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private ColaboradorEntity colaborador;

    @ManyToOne
    @JoinColumn(name = "id_dia")
    private DiaEntity dia;

    @ManyToOne
    @JoinColumn(name = "id_tipo_dia")
    private TipoDiaEntity tipoDia;

    @Column(name = "hora_inicio")
    private String horaInicio;

    @Column(name = "hora_fin")
    private String horaFin;
}
