package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "registro_asistencia",
        indexes = {
            @Index(name = "idx_asistencia_colaborador_fecha", columnList = "id_colaborador, fecha"),
            @Index(name = "idx_asistencia_fecha", columnList = "fecha")
        })
public class RegistroAsistenciaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false)
    @JsonIgnore
    private ColaboradorEntity colaborador;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
