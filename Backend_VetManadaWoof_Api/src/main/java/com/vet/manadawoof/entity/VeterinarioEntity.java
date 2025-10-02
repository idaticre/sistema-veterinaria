package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "veterinarios")
public class VeterinarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String codigo;

    @Column(length = 32, nullable = false)
    private String cmp;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;

    // Relación con colaborador
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_colaborador", nullable = false)
    private ColaboradorEntity colaborador;

    // Relación con especialidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_especialidad", nullable = false)
    private EspecialidadEntity especialidad;
}
