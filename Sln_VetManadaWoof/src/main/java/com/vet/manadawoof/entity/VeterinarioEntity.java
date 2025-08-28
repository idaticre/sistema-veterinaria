package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "VeterinarioEntity")
@Table(name = "veterinarios")
public class VeterinarioEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "cmp")
    private String cmp;

    @Column(name = "activo")
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", referencedColumnName = "id")
    private ColaboradorEntity colaborador;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", referencedColumnName = "id")
    private EspecialidadEntity especialidad;
}