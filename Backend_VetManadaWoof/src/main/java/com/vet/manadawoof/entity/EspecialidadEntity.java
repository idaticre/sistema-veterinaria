package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "EspecialidadEntity")
@Table(name = "especialidades")
@NamedStoredProcedureQuery(
        name = "EspecialidadEntity.spEspecialidades",
        procedureName = "sp_especialidades",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
public class EspecialidadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;

    private String nombre;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;

    @OneToMany(mappedBy = "especialidad")
    @JsonIgnore
    private List<VeterinarioEntity> veterinarios;
}
