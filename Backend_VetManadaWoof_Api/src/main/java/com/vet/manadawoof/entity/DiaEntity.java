package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "dias_semana")
public class DiaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 32, nullable = false, unique = true)
    private String nombre;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;

    @OneToMany(mappedBy = "dia")
    @JsonIgnore
    private List<HorarioTrabajoEntity> horario;
}
